package com.datasage.oi.model.user;

import java.util.Date;

public class User {
	public static final String STATUS_ONLINE = "online";
	public static final String STATUS_AWAY = "away";
	public static final String STATUS_OFFLINE = "offline";
	
	private Long userId;
	private String userName;
	private String status = STATUS_ONLINE;
	private Date loginTime = new Date();
	private Date lastActiveTime = new Date();
	private Date lastRequestTime = new Date();
	
	public User () {
		
	}
	
	public User (Long userId, String userName) {
		this.userId = userId;
		this.userName = userName;
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
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
	public Date getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}
	public Date getLastActiveTime() {
		return lastActiveTime;
	}
	public void setLastActiveTime(Date lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}
	public Date getLastRequestTime() {
		return lastRequestTime;
	}
	public void setLastRequestTime(Date lastRequestTime) {
		this.lastRequestTime = lastRequestTime;
	}

}
