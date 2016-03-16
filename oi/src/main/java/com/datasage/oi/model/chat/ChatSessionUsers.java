package com.datasage.oi.model.chat;

import java.util.Date;

/**
 * @author Scott
 *
 */
public class ChatSessionUsers {
	
	private Long sessionUserId;
	private Long sessionId;
	private Long userId;
	private Date joinTime;
	private String userName;
	private String status;
	
	public Long getSessionUserId() {
		return sessionUserId;
	}
	public void setSessionUserId(Long sessionUserId) {
		this.sessionUserId = sessionUserId;
	}
	public Long getSessionId() {
		return sessionId;
	}
	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Date getJoinTime() {
		return joinTime;
	}
	public void setJoinTime(Date joinTime) {
		this.joinTime = joinTime;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
