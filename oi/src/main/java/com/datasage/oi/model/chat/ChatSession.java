package com.datasage.oi.model.chat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatSession implements Serializable {

	private Long sessionId;
	private Date createTime;
	private Long createUserId;
	private String createUserName;
	private Date updateTime;
	
	private List<ChatSessionUsers> chatSessionUserList = new ArrayList<ChatSessionUsers>();
	private List<ChatSessionMessages> chatSessionMessageList = new ArrayList<ChatSessionMessages>();
	
	public List<ChatSessionUsers> getChatSessionUserList() {
		return chatSessionUserList;
	}
	public void setChatSessionUserList(List<ChatSessionUsers> chatSessionUserList) {
		this.chatSessionUserList = chatSessionUserList;
	}
	public List<ChatSessionMessages> getChatSessionMessageList() {
		return chatSessionMessageList;
	}
	public void setChatSessionMessageList(List<ChatSessionMessages> chatSessionMessageList) {
		this.chatSessionMessageList = chatSessionMessageList;
	}
	public void clearChatSessionMessageList () {
		chatSessionMessageList = new ArrayList<ChatSessionMessages>();
	}
	
	public void addChatSesssionMessage (ChatSessionMessages chatSessionMessage) {
		getChatSessionMessageList().add(chatSessionMessage);
	}
	
	public Long getSessionId() {
		return sessionId;
	}
	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Long getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(Long userId) {
		this.createUserId = userId;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
