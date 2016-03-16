package com.datasage.oi.bll.chat;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.datasage.oi.common.utils.ObjectTransition;
import com.datasage.oi.dao.chat.IChatDAO;
import com.datasage.oi.model.chat.ChatSession;
import com.datasage.oi.model.chat.ChatSessionMessages;
import com.datasage.oi.model.chat.ChatSessionUsers;
import com.datasage.oi.model.user.User;

@PropertySource(value = "classpath:chat_config.properties")
@Service
public class ChatBLL {
	
	@Value("${message_cache.swith}")
	private boolean messageCacheSwith;
	@Value("${message_cache.session_available_minutes}")
	private int sessionAvailableMinutes;
	
	@Resource
	private ChatCacheBLL chatCacheBLL;
	@Resource
	private UserCacheBLL userCacheBLL;
	
	private IChatDAO chatDAO;
	@Resource(name = "chatOracleDAO")
	private void setChatDAO(IChatDAO chatDAO) {
		this.chatDAO = chatDAO;
	}
	
	public Long createSession(ChatSession chatSession) {
		Long sessionId = chatDAO.insertChatSession(chatSession);
		
		//add create user to this session
		ChatSessionUsers chatSessionUsers = new ChatSessionUsers();
		chatSessionUsers.setSessionId(sessionId);
		chatSessionUsers.setUserId(chatSession.getCreateUserId());
		chatSessionUsers.setUserName(chatSession.getCreateUserName());
		chatSessionUsers.setJoinTime(chatSession.getCreateTime());
		chatDAO.insertUser(chatSessionUsers);
		
		userCacheBLL.updateUserLastActiveTime(new User(chatSession.getCreateUserId(), chatSession.getCreateUserName()));
		
		//add session to the Cache
		if (messageCacheSwith)
			chatCacheBLL.addSession(chatSession);
		return sessionId;
	}

	public int addUser(ChatSessionUsers chatSessionUsers) {
		// check whether session id exist.
		if (chatDAO.querySessionById(chatSessionUsers.getSessionId()) == null)
			return 0;
		// insert user
		return chatDAO.insertUser(chatSessionUsers);
	}

	public int sendMessage(ChatSessionMessages chatSessionMessage) {
		// check whether session id exist.
//		if (chatDAO.querySessionById(chatSessionMessage.getSessionId()) == null)
//			return 0;
		userCacheBLL.updateUserLastActiveTime(new User(chatSessionMessage.getSendUserId(), chatSessionMessage.getSendUserName()));
		
		if (messageCacheSwith)
			return chatCacheBLL.addMessage(chatSessionMessage);
		else 
			return chatDAO.insertMessage(chatSessionMessage);
	}

	public List<ChatSessionUsers> addUserList(List<ChatSessionUsers> chatSessionUsersList) {
		if (chatSessionUsersList == null || chatSessionUsersList.isEmpty())
			return null;
		// validate if session id is exist.

		// insert user list
		int flag =  chatDAO.insertChatSessionUserList(chatSessionUsersList).length;
		if (flag <= 0)
			return null;
		
		return getSessionUserListBySessionId(chatSessionUsersList.get(0).getSessionId());
	}

	public List<ChatSessionUsers> removeUserList(List<ChatSessionUsers> chatSessionUsersList) {
		if (chatSessionUsersList == null || chatSessionUsersList.isEmpty())
			return null;
		// validate if session id is exist.
		
		//delete user list
		int flag = chatDAO.deleteChatSessionUserList(chatSessionUsersList).length;
		if (flag <= 0)
			return null;
		
		return getSessionUserListBySessionId(chatSessionUsersList.get(0).getSessionId());
	}

	public List<ChatSessionUsers> getSessionUserListBySessionId(Long sessionId) {
		// check whether session id exist.
		if (chatDAO.querySessionById(sessionId) == null)
			return null;
		
		List<ChatSessionUsers> userList = chatDAO.querySessionUserListBySessionId(sessionId);
		for (ChatSessionUsers user : userList) {
			user.setStatus(userCacheBLL.getUserstatusById(user.getUserId()));
		}
		
		return userList; 
	}

	public List<ChatSessionMessages> getSessionMessages(ChatSessionMessages chatSessionMessages) {
		// check whether session id exist.
		/*if (chatDAO.querySessionById(chatSessionMessages.getSessionId()) == null)
			return null;*/
		
		userCacheBLL.upateUserLastRequestTime(new User(chatSessionMessages.getSendUserId(), chatSessionMessages.getSendUserName()));
		if (messageCacheSwith)
			return chatCacheBLL.getMessage(chatSessionMessages);
		else 
			return chatDAO.querySessionMessageList (chatSessionMessages);
	}

	public List<ChatSession> getSessionsByUserId(ChatSession chatSession) {
		userCacheBLL.upateUserLastRequestTime(new User(chatSession.getCreateUserId(), chatSession.getCreateUserName()));
		
		chatSession.setUpdateTime( ObjectTransition.timeAddMinutes(new Date(), -sessionAvailableMinutes));
		return chatDAO.querySessionsByUserId(chatSession);
	}

	public List<ChatSessionUsers> getAllUsers() {
		return chatDAO.queryAllUsers();
	}

	public List<ChatSessionUsers> getAllUsersBySessionId(long sessionId) {
		List<ChatSessionUsers> userList = chatDAO.queryAllUsersBySessionId(sessionId);
		
		for (ChatSessionUsers user : userList) {
			user.setStatus(userCacheBLL.getUserstatusById(user.getUserId()));
		}
		
		return userList;
	}
	
	/**
	 * for spring schedule
	 */
	public void updateSessionUpdateTime () {
		chatDAO.updateSessionUpdateTime ();
	}

	public void clearMessageCache () {
		chatCacheBLL.clearCache();
	}
	
	public void clearMessageCache (int messageAvailableSeconds, int sessionAvailableMinutes) {
		chatCacheBLL.clearCache(messageAvailableSeconds, sessionAvailableMinutes);
	}
}
