package com.github.barmiro.sysh_server.cron;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.barmiro.sysh_server.dataintake.recent.RecentController;
import com.github.barmiro.sysh_server.spotifyauthorization.SpotifyTokenService;
import com.github.barmiro.sysh_server.users.SyshUserManager;

@Component
public class ScheduledApiCalls {

	SpotifyTokenService tkn;
	RecentController recentController;
	SyshUserManager userManager;
	
	ScheduledApiCalls(SpotifyTokenService tkn,
			RecentController recentController,
			SyshUserManager userManager) {
		this.tkn = tkn;
		this.recentController = recentController;
		this.userManager = userManager;
	}
	
	private static final Logger log = LoggerFactory.getLogger(ScheduledApiCalls.class);
	
//	called every 25 minutes, the mathematical minimum time required
//	to reach Spotify's limit of 50 streams per call
	@Scheduled(fixedRate = 1500000)
	public void scheduledGetRecent() {
		List<String> usernameList = userManager.getAllUsernames();
		
		if (usernameList.isEmpty()) {
			log.error("No users found");
		}
		
//		the list is sorted alphabetically, so in normal operation the 25 minute delay will be kept,
//		might theoretically miss some streams when adding new users, but it's extremely unlikely
	    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	    long delayBetweenUsers = 1500000 / usernameList.size();
	    
	    for (int i = 0; i < usernameList.size(); i++) {
	        String username = usernameList.get(i);
	        scheduler.schedule(() -> {
	            try {
					recentController.recentByUsername(username);
				} catch (JsonProcessingException 
						| ClassCastException 
						| IllegalAccessException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
	        }, i * delayBetweenUsers, TimeUnit.MILLISECONDS);
	    }

	    scheduler.shutdown();
	}
}
