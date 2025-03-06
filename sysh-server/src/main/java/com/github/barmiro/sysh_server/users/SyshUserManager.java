package com.github.barmiro.sysh_server.users;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyshUserManager {

private static final Logger log = LoggerFactory.getLogger(SyshUserManager.class);
	
	private SyshUserRepository userRepo;
	
	public SyshUserManager(SyshUserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
	public SyshUserDetails loadUserBySpotifyState(String spotifyState) {
		
		SyshUser user = userRepo.findBySpotifyState(spotifyState);
		
		if (user == null) {
			throw new RuntimeException("User spotify_state not found");
		} else {
			return new SyshUserDetails(user);
		}
	}
	
	public void updateUserSpotifyAccessToken(String username, String spotifyAccessToken) {
		
	}
	
	public String getUserSpotifyAccessToken(String username) {
		
	}
	
	public void updateUserSpotifyRefreshToken(String username, String spotifyAccessToken) {
		
	}
	
	public String getUserSpotifyRefreshToken(String username) {
		
	}
	
	public LocalDateTime getUserTokenExpirationTime(String username) {
		
	}

	
	public void createUser(SyshUserDetails user) {
		int created = userRepo.createUser(user);
		if (created == 0) {
			log.error("User creation for " + user.getUsername() + " failed");
		} else if (created == 1) {
			log.info("User " + user.getUsername() + " created succcessfully");
		}
	}
}
