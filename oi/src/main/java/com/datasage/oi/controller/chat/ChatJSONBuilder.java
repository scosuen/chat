package com.datasage.oi.controller.chat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.datasage.oi.common.utils.ObjectTransition;
import com.datasage.oi.model.chat.ChatSession;
import com.datasage.oi.model.chat.ChatSessionMessages;
import com.datasage.oi.model.chat.ChatSessionUsers;

@Component
public class ChatJSONBuilder {
	
	public String buildSessionListOjbectJSON (List<ChatSession> chatSessionList) {
		StringBuffer json = new StringBuffer();
		json.append("{").append(buildSessionListJSON(chatSessionList)).append("}");
		
		return json.toString();
	}
	
	public String buildSessionListJSON (List<ChatSession> chatSessionList) {
		if (chatSessionList == null || chatSessionList.isEmpty())
			return "\"sessions\": [ ]";
		
		StringBuilder json = null;
		for (ChatSession chatSession : chatSessionList) {
			if (json == null) {
				json = new StringBuilder("\"sessions\": [ ");
				json.append(buildSessionJSON(chatSession));
			} else {
				json.append(", ");
				json.append(buildSessionJSON(chatSession));
			}
		}
		json.append("]");
		
		return json.toString();
	}

	/**
	 * {"sessionId": 10000,"createTime": "2015-05-20 16:22:13","userId": 20000}
	 * @param chatSession
	 * @return
	 */
	public String buildSessionJSON(ChatSession chatSession) {
		StringBuffer json = new StringBuffer();
		json.append("{\"sessionId\": ").append(chatSession.getSessionId());
		json.append(", \"createUserId\": ").append(chatSession.getCreateUserId());
		json.append(", \"createUserName\": \"").append(chatSession.getCreateUserName()).append("\"");
		json.append(", \"createTime\": \"").append(ObjectTransition.dateToSting(chatSession.getCreateTime())).append("\"");
		json.append(", \"updateTime\": \"").append(ObjectTransition.dateToSting(chatSession.getUpdateTime())).append("\"");
		
		json.append(", ").append(buildSessionUserListJSON(chatSession.getChatSessionUserList()));
		
		json.append("}");
		
		
		return json.toString();
	}

	/**
	 * {"sessionUserId": 1,"sessionId": 10000,"userId": 2000,"joinTime": "2015-05-20 16:22:13"}
	 * @param chatSessionUser
	 * @return
	 */
	public String buildSessionUserJSON(ChatSessionUsers chatSessionUser) {
		StringBuffer json = new StringBuffer();
		json.append("{\"sessionUserId\": ").append(chatSessionUser.getSessionUserId());
		json.append(", \"sessionId\": ").append(chatSessionUser.getSessionId());
		json.append(", \"userId\": ").append(chatSessionUser.getUserId());
		json.append(", \"userName\": \"").append(chatSessionUser.getUserName()).append("\"");
		json.append(", \"joinTime\": \"").append(ObjectTransition.dateToSting(chatSessionUser.getJoinTime())).append("\"");
		json.append(", \"status\": \"").append(chatSessionUser.getStatus()).append("\"");
		
		json.append("}");
		return json.toString();
	}
	
	public String buildSessionUserListJSON (List<ChatSessionUsers> chatSessionUserList) {
		if (chatSessionUserList == null || chatSessionUserList.isEmpty())
			return "\"sessionUsers\": [ ]";
		
		StringBuilder json = null;
		for (ChatSessionUsers user : chatSessionUserList) {
			if (json == null) {
				json = new StringBuilder("\"sessionUsers\": [");
				json.append(buildSessionUserJSON(user));
			} else {
				json.append(", ");
				json.append(buildSessionUserJSON(user));
			}
		}
		
		json.append("]");
		
		return json.toString();
		
	}
	
	public String buildSessionUserListObjectJSON (List<ChatSessionUsers> chatSessionUserList) {
		StringBuffer json = new StringBuffer();
		json.append("{").append(buildSessionUserListJSON(chatSessionUserList)).append("}");
		
		return json.toString();
	}

	/**
	 * {"sessionMessagesId": 11, "sessionId": 10000, "sendUser": 2000, "message": "this is a message", "sendTime": "2015-05-20 16:22:13"}
	 * @param chatSessionMessages
	 * @return
	 */
	public String buildSessionMessageJSON(
			ChatSessionMessages chatSessionMessages) {
		StringBuffer json = new StringBuffer();
		json.append("{\"sessionMessagesId\": ").append(chatSessionMessages.getSessionMessagesId());
		json.append(", \"sessionId\": ").append(chatSessionMessages.getSessionId());
		json.append(", \"sendUserId\": ").append(chatSessionMessages.getSendUserId());
		json.append(", \"sendUserName\": \"").append(chatSessionMessages.getSendUserName()).append("\"");
		json.append(", \"message\": \"").append(chatSessionMessages.getMessage()).append("\"");
		json.append(", \"sendTime\": \"").append(ObjectTransition.dateToSting(chatSessionMessages.getSendTime())).append("\"");
		json.append("}");
		
		return json.toString();
	}
	
	public String buildSessionMessageListJSON (List<ChatSessionMessages> chatSessionMessageList, Date requestTime) {
		if (chatSessionMessageList == null || chatSessionMessageList.isEmpty())
			return "\"requestTime\": " + requestTime.getTime() + ", \"sessionMessages\": [  ]";
		
		StringBuffer json = null;
		
		for (ChatSessionMessages message : chatSessionMessageList) {
			if (json == null) {
				json = new StringBuffer();
				json.append("\"requestTime\": ").append(requestTime.getTime()).append(", ");
				json.append("\"sessionMessages\": [");
				json.append(buildSessionMessageJSON(message));
			} else {
				json.append(", ");
				json.append(buildSessionMessageJSON(message));
			}
		}
		
		json.append("]");
		
		return json.toString();
		
	}
	
	public String buildSessionMessageListObjectJSON (List<ChatSessionMessages> chatSessionMessageList, Date requestTime) {
		StringBuffer json = new StringBuffer();
		json.append("{").append(buildSessionMessageListJSON(chatSessionMessageList, requestTime)).append("}");
		
		return json.toString();
	}

	public List<ChatSessionUsers> chatSessionUserListJSON2Model (String json) {
		if (json == null || json.isEmpty())
			return null;
		
		JSONObject jsonObject = new JSONObject(json);
		JSONArray userListJSON = jsonObject.getJSONArray("sessionUsers");
		JSONObject userJSON = null;
		
		List<ChatSessionUsers> result = new ArrayList<ChatSessionUsers>();
		ChatSessionUsers chatSessionUsers = null;
		
		for (int i = 0 ; i < userListJSON.length() ; i ++) {
			chatSessionUsers = new ChatSessionUsers();
			userJSON = userListJSON.getJSONObject(i);
			
			chatSessionUsers.setSessionId(userJSON.getLong("sessionId"));
			chatSessionUsers.setUserId(userJSON.getLong("userId"));
			chatSessionUsers.setUserName(userJSON.getString("userName"));
			//chatSessionUsers.setJoinTime();
			//chatSessionUsers.setSessionUserId();
			result.add(chatSessionUsers);
			
		}
		
		return result;
	}
	
	/**
	 * {
		   "sessionId":10000,
		   "sendUserId":2000,
		   "sendUserName":"Yve",
		   "message":"this is a message"
		}
	 * 
	 * @return
	 */
	public ChatSessionMessages chatSessionMessagesJSON2Model (String json) {
		if (json == null || json.isEmpty())
			return null;
		
		JSONObject messageJSON = new JSONObject(json);
		
		ChatSessionMessages result = new ChatSessionMessages();
		result.setSessionId(messageJSON.getLong("sessionId"));
		result.setSendUserId(messageJSON.getLong("sendUserId"));
		result.setSendUserName(messageJSON.getString("sendUserName"));
		result.setMessage(messageJSON.getString("message"));
		
		return result;
	}

	/**
	 *{
		   "createUserserId":20000,
		   "createUserName":"Yve"
		} 
	 * @param json
	 * @return
	 */
	public ChatSession chatSessionJSON2Model(String json) {
		if (json == null || json.isEmpty())
			return null;
		
		JSONObject sessionJSON = new JSONObject(json);
		ChatSession result = new ChatSession();
		
		result.setCreateUserId(sessionJSON.getLong("createUserserId"));
		result.setCreateUserName(sessionJSON.getString("createUserName"));
		
		return result;
	}

}
