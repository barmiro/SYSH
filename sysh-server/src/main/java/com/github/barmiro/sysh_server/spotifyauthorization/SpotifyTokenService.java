package com.github.barmiro.sysh_server.spotifyauthorization;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.barmiro.sysh_server.users.SyshUserManager;

@Service
public class SpotifyTokenService {
	

	
	private final String clientId = System.getenv("SPOTIFY_CLIENT_ID");
	private final String clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");
	private final String base64 = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

	private RestClient tokenClient;
	private SyshUserManager userManager;
	
	public SpotifyTokenService(
			RestClient tokenClient,
			SyshUserManager userManager) {
		this.tokenClient = tokenClient;
		this.userManager = userManager;
	}
	
	ObjectMapper objectMapper = new ObjectMapper();
	private static final Logger log = LoggerFactory.getLogger(SpotifyTokenService.class);
	
	public void setToken(String username, String token) {
		userManager.updateUserSpotifyAccessToken(username, token);
	}
	
	public String getToken(String username) {
		return userManager.getUserSpotifyAccessToken(username);
	}
	
	public void expTimeFromExpiresIn(String username, int seconds) {
		Timestamp tokenExpires = Timestamp.valueOf(LocalDateTime.now().plusSeconds(seconds));
		userManager.updateUserTokenExpirationTime(username, tokenExpires);
	}
	
	public LocalDateTime getExpTime(String username) {
		return userManager.getUserTokenExpirationTime(username);
	}
	
	public void setRefreshToken(String username, String refreshToken) {
		if (refreshToken != null) {
			userManager.updateUserSpotifyRefreshToken(username, refreshToken);
		}
	}
	
	public String getRefreshToken(String username) {
		return userManager.getUserSpotifyRefreshToken(username);
	}
	
	
	
	public boolean expirationImminent(String username) {
		LocalDateTime expirationTime = userManager.getUserTokenExpirationTime(username);
		
		if (LocalDateTime.now().plusMinutes(5).isAfter(expirationTime)) {
			return true;
		} else {
			return false;
		}
	}
	
	private final String serverUrl = System.getenv("SYSH_SERVER_URL");
	private final String serverPort = System.getenv("SYSH_SERVER_PORT");
	
    @Retryable(
            value = { 
        		HttpServerErrorException.class,
        		ResourceAccessException.class
            },
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2),
            label = "SpotifyTokenService.getNewToken"
        )
	public void getNewToken(String code, String username) {
		SpotifyAuthorizationResponseDTO responseBody;
		MultiValueMap<String, String> newBody = new LinkedMultiValueMap<>();
		
		newBody.add("grant_type", "authorization_code");
		newBody.add("code", code);
		newBody.add("redirect_uri", "http://127.0.0.1:5754/callback");
		
		ResponseEntity<String> newEntity = tokenClient
				.post()
				.body(newBody)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.header("Authorization", "Basic " + base64)
				.retrieve()
				.toEntity(String.class);
		
		
		try {
			responseBody = objectMapper.readValue(newEntity.getBody(), SpotifyAuthorizationResponseDTO.class);
			setToken(username, responseBody.access_token());
			expTimeFromExpiresIn(username, responseBody.expires_in());
			setRefreshToken(username, responseBody.refresh_token());
			log.info("Set token for user " + username);
			return;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new HttpClientErrorException(HttpStatus.PRECONDITION_FAILED);
		}
		
	}
    
    
    @Retryable(
            value = { 
        		HttpServerErrorException.class,
        		ResourceAccessException.class
            },
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2),
            label = "SpotifyTokenService.refresh"
        )
	public boolean refresh(String username) {
		
		LocalDateTime expirationTime = userManager.getUserTokenExpirationTime(username);
		String refreshToken = userManager.getUserSpotifyRefreshToken(username);
		
		if (expirationTime == null || refreshToken == null) {
			log.error("User " + username + " not authenticated with Spotify");
			throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
		} else if (LocalDateTime.now().plusMinutes(5).isBefore(expirationTime)) {
			log.info("Token valid until: " + expirationTime + ". Proceeding...");
			return false;
		} else {
			log.info("Token expires: " + expirationTime + ". Getting new token...");			
		}
		SpotifyAuthorizationResponseDTO responseBody;
		MultiValueMap<String, String> newBody = new LinkedMultiValueMap<>();
		
		newBody.add("grant_type", "refresh_token");
		newBody.add("refresh_token", refreshToken);
		
		ResponseEntity<String> newEntity = tokenClient
				.post()
				.body(newBody)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.header("Authorization", "Basic " + base64)
				.retrieve()
				.toEntity(String.class);
		
		try {
			responseBody = objectMapper.readValue(newEntity.getBody(), SpotifyAuthorizationResponseDTO.class);
			setToken(username, responseBody.access_token());
			expTimeFromExpiresIn(username, responseBody.expires_in());
			if (responseBody.refresh_token() != null) {
				setRefreshToken(username, responseBody.refresh_token());
			}
			log.info("Received new token for user " + username);
			return true;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
		}
	}

}
