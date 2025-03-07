package com.github.barmiro.sysh_server.users;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SyshUserManager {

private static final Logger log = LoggerFactory.getLogger(SyshUserManager.class);
	
	private SyshUserRepository userRepo;
	
	public SyshUserManager(SyshUserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
	public String getUsernameBySpotifyState(String spotifyState) {
		
		String user =  userRepo.findBySpotifyState(spotifyState);
		
		if (user == null) {
			throw new RuntimeException("User spotify_state not found");
		} else {
			return user;
		}
	}
	
	public void updateUserSpotifyAccessToken(String username, String spotifyAccessToken) {
		int updated = userRepo.updateSpotifyAccessToken(username, spotifyAccessToken);
		
		if (updated != 1) {
			throw new RuntimeException("Spotify access token update failed: " + updated);
		}
	}
	
	public String getUserSpotifyAccessToken(String username) {
		return userRepo.getSpotifyAccessToken(username).orElse(null);
	}
	
	public void updateUserSpotifyRefreshToken(String username, String spotifyRefreshToken) {
		int updated = userRepo.updateSpotifyRefreshToken(username, spotifyRefreshToken);
		
		if (updated != 1) {
			throw new RuntimeException("Spotify refresh token update failed: " + updated);
		}
	}
	
	public String getUserSpotifyRefreshToken(String username) {
		return userRepo.getSpotifyRefreshToken(username).orElse(null);
	}
	
	public void updateUserTokenExpirationTime(String username, Timestamp tokenExpires) {
		int updated = userRepo.updateTokenExpirationTime(username, tokenExpires);
		
		if (updated != 1) {
			throw new RuntimeException("Spotify token expiration time update failed: " + updated);
		}
	}
	
	public LocalDateTime getUserTokenExpirationTime(String username) {
		try {
			return userRepo.getTokenExpirationTime(username).orElse(null).toLocalDateTime();			
		} catch (NullPointerException e) {
			return null;
		}
		
	}
	
	public String getUserSpotifyState(String username) {
		return userRepo.getSpotifyState(username);
	}

	public List<String> getAllUsernames() {
		return userRepo.findAllUsernames();
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
