package com.datasage.oi.config.spring;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.datasage.oi.bll.chat.ChatBLL;
import com.datasage.oi.bll.chat.ChatCacheBLL;
import com.datasage.oi.bll.chat.UserCacheBLL;

@Configuration
@EnableScheduling
@PropertySources({
		@PropertySource(value = "classpath:chat_config.properties"),
		@PropertySource(value = "classpath:xx.properties", ignoreResourceNotFound = true) })
public class Schedule {
	
	private static boolean updateSessionUpdateTimeIsRunning = false;
	private static boolean clearMessagesCacheIsRunning = false;
	private static boolean updateUserStatusIsRunning = false;
	
	@Value("${message_cache.swith}")
	private boolean cacheSwith;
	@Value("${message_cache.message_available_seconds}")
	private int messageAvailableSeconds;
	@Value("${message_cache.session_available_minutes}")
	private int sessionAvailableMinutes;

	@Resource
	private ChatBLL chatbBLL;
	@Resource
	private UserCacheBLL userCacheBLL;

	@Scheduled(fixedDelay = 60 * 1000)
	public void updateSessionUpdateTime () {
		if (updateSessionUpdateTimeIsRunning == false) {
			updateSessionUpdateTimeIsRunning = true;
			chatbBLL.updateSessionUpdateTime();
			updateSessionUpdateTimeIsRunning = false;
		}
	}
	
	@Scheduled(fixedDelay = 5 * 1000)
	public void clearMessagesCache () {
		if (cacheSwith == true && clearMessagesCacheIsRunning == false)
			clearMessagesCacheIsRunning = true;
			chatbBLL.clearMessageCache(messageAvailableSeconds, sessionAvailableMinutes);
			clearMessagesCacheIsRunning = false;
	}
	
	@Scheduled(fixedDelay = 5 * 1000)
	public void updateUserStatus () {
		if (updateUserStatusIsRunning == false) {
			updateUserStatusIsRunning = true;
			userCacheBLL.updateUserStatus();
			updateUserStatusIsRunning = false;
		}
	}

}
