package com.datasage.oi.bll.chat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.datasage.oi.common.utils.ObjectTransition;
import com.datasage.oi.dao.chat.IChatDAO;
import com.datasage.oi.model.chat.ChatSession;
import com.datasage.oi.model.chat.ChatSessionMessages;

@PropertySource(value = "classpath:chat_config.properties")
@Component
@Lazy(false)
public class ChatCacheBLL implements InitializingBean {

	@Value("${message_cache.message_available_seconds}")
	private int messageAvailableSeconds;
	@Value("${message_cache.session_available_minutes}")
	private int sessionAvailableMinutes;
	
	private List<ChatSession> sessionsMessagesCacheList = new Vector<ChatSession>();
	private IChatDAO chatDAO;

	@Resource(name = "chatOracleDAO")
	private void setChatDAO(IChatDAO chatDAO) {
		this.chatDAO = chatDAO;
	}
	
	public void afterPropertiesSet() throws Exception {
		// initialize message cache
		ChatSession chatSessionPara = new ChatSession();
		chatSessionPara.setUpdateTime(ObjectTransition.timeAddMinutes(new Date(), -sessionAvailableMinutes));
		for (ChatSession chatSession : chatDAO.querySessions(chatSessionPara)) {
			addSession(chatSession);
		}
	}

	public void addSession(ChatSession chatSession) {
		synchronized (sessionsMessagesCacheList) {
			sessionsMessagesCacheList.add(chatSession);
		}
	}

	public int addMessage(ChatSessionMessages chatSessionMessage) {
		synchronized (sessionsMessagesCacheList) {
			for (ChatSession chatSession : sessionsMessagesCacheList) {
				if (chatSession.getSessionId().equals(chatSessionMessage.getSessionId())) {
					chatSession.addChatSesssionMessage(chatSessionMessage);
					chatSession.setUpdateTime(chatSessionMessage.getSendTime());
					return 1;
				}
			}
		}
		return 0;
	}
	
	public List<ChatSessionMessages> getMessage (ChatSessionMessages chatSessionMessage) {
		List<ChatSessionMessages> messageList = new ArrayList<ChatSessionMessages>();
		for (ChatSession chatSession : sessionsMessagesCacheList) {
			
			if (chatSession.getSessionId().equals(chatSessionMessage.getSessionId())) {
				for (ChatSessionMessages msg : chatSession.getChatSessionMessageList()) {
					if (msg.getSendTime().getTime() >= chatSessionMessage.getSendTime().getTime() && 
							!msg.getSendUserId().equals(chatSessionMessage.getSendUserId()))
						messageList.add(msg);
				}
				return messageList;
			}
		}
		
		return null;
	}
	
	public void clearCache () {
		clearCache(messageAvailableSeconds, sessionAvailableMinutes);
	}

	public void clearCache(int messageAvailableSeconds, int sessionAvailableMinutes) {
		List<ChatSession> sessionsMessagesCacheListTemp = new Vector<ChatSession>();
		List<ChatSessionMessages> messageListForDB = new ArrayList<ChatSessionMessages>();
		List<ChatSessionMessages> tempMessageList = null;
		
		Date messageAvailalbeTime = ObjectTransition.timeAddSeconds(new Date(), -messageAvailableSeconds);
		Date sessionAvailableTime = ObjectTransition.timeAddMinutes(new Date(), -sessionAvailableMinutes);
		
		synchronized (sessionsMessagesCacheList) {
			for (ChatSession chatSession : sessionsMessagesCacheList) {
				// check if session is available
				if (chatSession.getUpdateTime().getTime() < sessionAvailableTime.getTime())
					continue;

				tempMessageList = new ArrayList<ChatSessionMessages>();
				// check if message in this session is available
				for (ChatSessionMessages msg : chatSession.getChatSessionMessageList()) {
					if (msg.getSendTime().getTime() < messageAvailalbeTime.getTime()) {
						// delect from cache, store to db
						messageListForDB.add(msg);
					} else {
						// still save in cache
						tempMessageList.add(msg);
					}
				}
				chatSession.setChatSessionMessageList(tempMessageList);
				sessionsMessagesCacheListTemp.add(chatSession);
			}
			
			sessionsMessagesCacheList = sessionsMessagesCacheListTemp;
		}
		
		//store to DB
		if (!messageListForDB.isEmpty()) 
			chatDAO.insertMessageList(messageListForDB);
	}
	
	private String printCache() {
		StringBuffer cacheStr = new StringBuffer();
		for (ChatSession chatSession : sessionsMessagesCacheList) {
			cacheStr.append("session id : " + chatSession.getSessionId() + "\r\n");
			for (ChatSessionMessages msg : chatSession.getChatSessionMessageList()) {
				cacheStr.append("	(" + msg.getSendTime() + ")").append(msg.getSendUserName()).append(":" + msg.getMessage() + "\r\n");
			}
		}
		
		return cacheStr.toString();
		
	}

}
