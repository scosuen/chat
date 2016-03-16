package com.datasage.oi.dao.chat;

import java.util.List;
import java.util.Map;

import com.datasage.oi.model.chat.ChatSession;
import com.datasage.oi.model.chat.ChatSessionMessages;
import com.datasage.oi.model.chat.ChatSessionUsers;

public interface IChatDAO {
	
	Long insertChatSession (ChatSession chatSession);

	int insertUser(ChatSessionUsers chatSessionUsers);

	ChatSession querySessionById(Long sessionId);

	int insertMessage(ChatSessionMessages chatSessionMessage);

	int[] insertChatSessionUserList(List<ChatSessionUsers> chatSessionUsersList);

	int[] deleteChatSessionUserList(List<ChatSessionUsers> chatSessionUsersList);

	List<ChatSessionUsers> querySessionUserListBySessionId(Long sessionId);

	List<ChatSessionMessages> querySessionMessageList(ChatSessionMessages chatSessionMessages);

	List<ChatSession> querySessionsByUserId(ChatSession chatSession);

	List<ChatSessionUsers> queryAllUsers();

	List<ChatSessionUsers> queryAllUsersBySessionId(long sessionId);

	void updateSessionUpdateTime();

	List<ChatSession> querySessions(ChatSession chatSession);

	int[] insertMessageList(List<ChatSessionMessages> chatSessionMessageList);

}
