package com.datasage.oi.model.chat;

import java.util.Date;

public class ChatSessionMessages {
	
	private Long sessionMessagesId;
	private Long sessionId;
	private Long sendUserId;
	private String message;
	private Date sendTime;
	private String sendUserName;
	
	public Long getSessionMessagesId() {
		return sessionMessagesId;
	}
	public void setSessionMessagesId(Long sessionMessagesId) {
		this.sessionMessagesId = sessionMessagesId;
	}
	public Long getSessionId() {
		return sessionId;
	}
	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}
	public Long getSendUserId() {
		return sendUserId;
	}
	public void setSendUserId(Long sendUser) {
		this.sendUserId = sendUser;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	public String getSendUserName() {
		return sendUserName;
	}
	public void setSendUserName(String sendUserName) {
		this.sendUserName = sendUserName;
	}
	
}
