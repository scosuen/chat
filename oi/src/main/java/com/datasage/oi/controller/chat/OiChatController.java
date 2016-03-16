package com.datasage.oi.controller.chat;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.datasage.oi.bll.chat.ChatBLL;
import com.datasage.oi.common.utils.CommonJSONBuilder;
import com.datasage.oi.common.utils.ObjectTransition;
import com.datasage.oi.dao.chat.ChatOracleDAO;
import com.datasage.oi.model.chat.ChatSession;
import com.datasage.oi.model.chat.ChatSessionMessages;
import com.datasage.oi.model.chat.ChatSessionUsers;

@RestController
@RequestMapping(value = "/chat")
public class OiChatController {
	
	private static Logger logger = LoggerFactory.getLogger(ChatOracleDAO.class);

	@Resource
	private ChatBLL chatBLL;

	@Resource
	private ChatJSONBuilder chatJSONBuilder;

	/**
	 * request json
	 * {
		   "createUserserId":20000,
		   "createUserName":"Yve"
		}
	 * @param json
	 * @return
	 */
	@RequestMapping(value = "/createSession", method = RequestMethod.POST)
	public String createSession (@RequestBody String json) {
		
		ChatSession chatSession = chatJSONBuilder.chatSessionJSON2Model (json);
		Date now = new Date();
		chatSession.setCreateTime(now);
		chatSession.setUpdateTime(now);
		
		Long sessionID = chatBLL.createSession(chatSession);
		chatSession.setSessionId(sessionID);
		
		return chatJSONBuilder.buildSessionJSON(chatSession);
	}
	
	@RequestMapping(value = "/getSessionsNUsers/{userId}", method = RequestMethod.GET)
	public String getSessionsNUsers (@PathVariable("userId") long userId) {
		ChatSession chatSessionSearchPara = new ChatSession();
		chatSessionSearchPara.setCreateUserId(userId); //this is not Create User, just for make easy to search.
		
		List<ChatSession> chatSessionList = chatBLL.getSessionsByUserId (chatSessionSearchPara);
		
		for (ChatSession chatSession : chatSessionList) {
			chatSession.setChatSessionUserList(chatBLL.getSessionUserListBySessionId(chatSession.getSessionId()));
		}
		
		return chatJSONBuilder.buildSessionListOjbectJSON(chatSessionList);
		
	}
	
	@RequestMapping(value = "/getAllUsers", method = RequestMethod.GET)
	public String getAllUsers () {
		List<ChatSessionUsers> chatSessionUserList = chatBLL.getAllUsers();
		
		return chatJSONBuilder.buildSessionUserListObjectJSON(chatSessionUserList);
	}
	
	@RequestMapping(value = "/getAllUsersBySessionId/{sessionId}", method = RequestMethod.GET)
	public String getAllUsersBySessionId (@PathVariable("sessionId") long sessionId) {
		List<ChatSessionUsers> chatSessionUserList = chatBLL.getAllUsersBySessionId(sessionId);
		
		return chatJSONBuilder.buildSessionUserListObjectJSON(chatSessionUserList);
	}
	
	@RequestMapping(value = "/getSessionUsers/{sessionId}", method = RequestMethod.GET)
	public String getSessionUsers (@PathVariable("sessionId") long sessionId) {
		List<ChatSessionUsers> userList = chatBLL.getSessionUserListBySessionId(sessionId);
		
		return chatJSONBuilder.buildSessionUserListObjectJSON(userList);
	}
	
	
	/**
	 * request json
	 * {
		   "sessionUsers":[
		      {
		         "sessionId":1,
		         "userId":2000,
		         "userName":"Yve"
		      },
		      {
		         "sessionId":1,
		         "userId":2000,
		         "userName":"Yve"
		      }
		   ]
		}
	 * @param json
	 * @return
	 */
	@RequestMapping(value = "/addUsers", method = RequestMethod.POST)
	public String addUsers(@RequestBody String json) {

		List<ChatSessionUsers> chatSessionUsersList = chatJSONBuilder.chatSessionUserListJSON2Model(json);

		for (ChatSessionUsers chatSessionUsers : chatSessionUsersList) {
			chatSessionUsers.setJoinTime(new Date());
		}

		List<ChatSessionUsers> resultList = chatBLL.addUserList(chatSessionUsersList);

		if (resultList == null)
			return CommonJSONBuilder.getResponseJSON(400, "Add user failed");

		return chatJSONBuilder.buildSessionUserListObjectJSON(resultList);

	}
	
	/**
	 * request json
	 * {
		   "sessionUser":[
		      {
		         "sessionId":1,
		         "userId":2000,
		         "userName":"Yve"
		      },
		      {
		         "sessionId":1,
		         "userId":2000,
		         "userName":"Yve"
		      }
		   ]
		}
	 * @param json
	 * @return
	 */
	@RequestMapping(value = "/removeUsers", method = RequestMethod.DELETE)
	public String removeUsers (@RequestBody String json) {
		List<ChatSessionUsers> chatSessionUsersList = chatJSONBuilder.chatSessionUserListJSON2Model(json);
		
		List<ChatSessionUsers> resultList = chatBLL.removeUserList(chatSessionUsersList);

		if (resultList == null)
			return CommonJSONBuilder.getResponseJSON(400, "remove user failed");

		return chatJSONBuilder.buildSessionUserListObjectJSON(resultList);
	}
	
	/**
	 * request json
	 * {
		   "sessionId":10000,
		   "sendUserId":2000,
		   "sendUserName":"Yve",
		   "message":"this is a message"
		}
	 * @param json
	 * @return
	 */
	@RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
	public String sendMessage(@RequestBody String json) {
		ChatSessionMessages chatSessionMessage = chatJSONBuilder.chatSessionMessagesJSON2Model(json);
		
		chatSessionMessage.setSendTime(new Date());	
		int flag = chatBLL.sendMessage(chatSessionMessage);
		
		if (flag <= 0)
			return CommonJSONBuilder.getResponseJSON(400, "send message failed");
		
		return CommonJSONBuilder.getResponseJSON();
	}
	
	
	/**
	 * respones json
	 * {
		   "sessionMessages":[
		      {
		         "sessionMessagesId":24,
		         "sessionId":1,
		         "sendUserId":2002,
		         "sendUserName":"Yve",
		         "message":"this is a message 23",
		         "sendTime":"2015-05-27 23:18:02"
		      },
		      ...
		   ]
		}
	 * 
	 * @param sessionId
	 * @param time
	 * @return
	 */
	@RequestMapping(value = "/getSessionMessages/{sessionId}/{userId}/{userName}/{time}", method = RequestMethod.GET)
	public String getSessionMessages (@PathVariable("sessionId") long sessionId,
			@PathVariable("userId") long userId,
			@PathVariable("userName") String userName,
			@PathVariable("time") long time) {
		ChatSessionMessages chatSessionMessages = new ChatSessionMessages();
		chatSessionMessages.setSessionId(sessionId); 
		chatSessionMessages.setSendUserId(userId);
		chatSessionMessages.setSendUserName(userName);
		Date requestTime = new Date();
		
		if (time <= 0)
			chatSessionMessages.setSendTime(new Date());
		else 
			chatSessionMessages.setSendTime(new Date(time));
		
		List<ChatSessionMessages> messageList = chatBLL.getSessionMessages(chatSessionMessages);
		
		if (messageList == null)
			return CommonJSONBuilder.getResponseJSON(400, "Chat " + sessionId + "  has expired.");
		
		return chatJSONBuilder.buildSessionMessageListObjectJSON(messageList, requestTime);
	}
	
	@RequestMapping(value = "/clearMessageCache/{sessionAvailableMinutes}/{messageAvailableSeconds}", method = RequestMethod.GET)
	public String clearMessageCache (@PathVariable("sessionAvailableMinutes") int sessionAvailableMinutes ,
			@PathVariable("messageAvailableSeconds") int messageAvailableSeconds) {
		chatBLL.clearMessageCache(messageAvailableSeconds, sessionAvailableMinutes);
		return CommonJSONBuilder.getResponseJSON(); 
	}
	
}
