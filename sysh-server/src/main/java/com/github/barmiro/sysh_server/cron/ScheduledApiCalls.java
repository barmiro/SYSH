package com.github.barmiro.sysh_server.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.barmiro.sysh_server.auth.TokenService;
import com.github.barmiro.sysh_server.dataintake.recent.RecentController;

@Component
public class ScheduledApiCalls {

	TokenService tkn;
//	I should really move this to a service...
	RecentController recentController;
	
	ScheduledApiCalls(TokenService tkn, RecentController recentController) {
		this.tkn = tkn;
		this.recentController = recentController;
	}
	
	private static final Logger log = LoggerFactory.getLogger(ScheduledApiCalls.class);
	
//	called every 25 minutes, the mathematical minimum time required
//	to reach Spotify's limit of 50 streams per call
	@Scheduled(fixedRate = 60000)
	public void scheduledGetRecent() {
		if (tkn.isAuthenticated()) {
			recentController.recent();
		} else {
			log.error("Not authenticated with Spotify, couldn't fetch recent streams.");			
		}
	}
}
