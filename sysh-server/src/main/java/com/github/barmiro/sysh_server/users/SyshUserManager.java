package com.github.barmiro.sysh_server.users;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.barmiro.sysh_server.security.SyshUser;

@Service
public class SyshUserManager {

private static final Logger log = LoggerFactory.getLogger(SyshUserManager.class);
	
	private SyshUserRepository userRepo;
	
	public SyshUserManager(SyshUserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
	public String getUsernameBySpotifyState(String spotifyState) {
		
		Optional<String> user =  userRepo.findBySpotifyState(spotifyState);
		
		if (user.isEmpty()) {
			log.error("Spotify state " + spotifyState + " not found in database");
			return null;
//			throw new RuntimeException("User spotify_state not found");
		} else {
			return user.get();
		}
	}
	
	public SyshUser getUserByUsername(String username) {
		Optional<SyshUser> user = userRepo.findByUsername(username);
		
		return user.orElseThrow();
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
		} catch (NullPointerException e) { // TODO: i don't like this
			return null;
		}
		
	}
	
	public String getUserSpotifyState(String username) {
		return userRepo.getSpotifyState(username);
	}

	public List<String> getAllUsernames() {
		return userRepo.findAllUsernames();
	}
	
	public List<AppUserData> getAllUserData() {
		return userRepo.getAllUserData();
	}
	
	public Boolean isUsernameTaken(String username) {
		return userRepo.findByUsername(username).isPresent();
	}
	
	public Boolean adminExists() {
		return userRepo.adminCount() > 0;
	}
	
	
	public SyshUser createUser(SyshUser user) {
		int created = userRepo.createUser(user);
		if (created == 1) {
			log.info("User " + user.username() + " with role " + user.role() +  " created succcessfully");
			return user;
		} else {
			log.error("User creation for " + user.username() + " failed: " + created);
			return null;
		}
	}
	
	public int updateUserTimezone(String username, String timezone) {
		int updated = userRepo.updateUserTimezone(username, timezone);
		if (updated == 1) {

			log.info("Timezone for user " + username + " changed to: " + timezone);
		} else {
			log.error("Timezone change operation for " + username + " failed: " + updated);
			throw new RuntimeException("Timezone change failed");
		}
		return updated;
	}
	
	public int deleteUser(String username) {
		int deleted = userRepo.deleteUser(username);
		if (deleted == 1) {
			log.info("User " + username + " deleted succcessfully");
		} else {
			log.error("User delete operation for " + username + " failed: " + deleted);
			throw new RuntimeException("User deletion failed");
		}
		return deleted;
	}
	
	public int changePassword(String username, String newPassword, Boolean mustChangePassword) {
		int changed = userRepo.changePassword(username, newPassword, mustChangePassword);
		
		if (changed == 1) {
			log.info("Updated password for user " + username + ". Must change password: " + mustChangePassword);
			return changed;
		} else {
			throw new RuntimeException("Password change for user " + username + " failed.");
		}
	}
	
}
