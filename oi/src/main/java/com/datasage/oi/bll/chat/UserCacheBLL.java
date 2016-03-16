package com.datasage.oi.bll.chat;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.datasage.oi.common.utils.ObjectTransition;
import com.datasage.oi.model.user.User;

@PropertySource(value = "classpath:chat_config.properties")
@Component
@Lazy(false)
public class UserCacheBLL {
	
	private Map<Long, User> userCacheMap = new ConcurrentHashMap<Long, User>();
	private Map<Long, User> userCacheMapTemp;
	
	@Value("${user_cache.away_limit_time_minutes}")
	private int awayLimitTimeMinutes;
	@Value("${user_cache.offline_limit_time_minutes}")
	private int offlineLimitTimeMinutes;
	
	public boolean addUser (User user) {
		synchronized (userCacheMap) {
			if (userCacheMap.containsKey(user.getUserId()))
				return false;
			
			userCacheMap.put(user.getUserId(), user);
			return true;
		}
	}
	
	public void upateUserLastRequestTime (User userPara) {
		synchronized (userCacheMap) {
			if (!userCacheMap.containsKey(userPara.getUserId()))
				userCacheMap.put(userPara.getUserId(), userPara);
			else 
				userCacheMap.get(userPara.getUserId()).setLastRequestTime(userPara.getLastRequestTime());
		}
	}
	
	public void updateUserLastActiveTime (User userPara) {
		synchronized (userCacheMap) {
			if (!userCacheMap.containsKey(userPara.getUserId()))
				userCacheMap.put(userPara.getUserId(), userPara);
			else
				userCacheMap.get(userPara.getUserId()).setLastActiveTime(userPara.getLastActiveTime());
		}
	}
	
	public User getUserById (Long userId) {
		if (!userCacheMap.containsKey(userId))
			return null;
		
		return userCacheMap.get(userId);
	}
	
	public String getUserstatusById (Long userId) {
		User user = getUserById(userId);
		
		return user == null ? User.STATUS_OFFLINE : user.getStatus();
	}
	
	public void updateUserStatus () {
		synchronized (userCacheMap) {
			userCacheMapTemp = new ConcurrentHashMap<Long, User>();
			Date offlineLimitTime = ObjectTransition.timeAddMinutes(new Date(), -offlineLimitTimeMinutes);
			Date awayLimitTime = ObjectTransition.timeAddMinutes(new Date(), -awayLimitTimeMinutes);
			
			for (Map.Entry<Long, User> userEntry : userCacheMap.entrySet()) {
				User user = userEntry.getValue();
				
				if (user.getLastRequestTime().getTime() < offlineLimitTime.getTime()) 
					continue;
				
				if (user.getLastActiveTime().getTime() < awayLimitTime.getTime() && !User.STATUS_AWAY.equals(user.getStatus()))
					user.setStatus(User.STATUS_AWAY);
				
				userCacheMapTemp.put(user.getUserId(), user);
			}
			userCacheMap = userCacheMapTemp;
			userCacheMapTemp = null;
		}
	}
	
}
