package com.github.barmiro.sysh_server.cron;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

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
		
		for (String username:usernameList) {
			try {
				recentController.recentByUsername(username);				
			} catch (HttpClientErrorException e) {
				
			}
		}
	}
}
