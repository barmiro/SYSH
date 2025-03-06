package com.github.barmiro.sysh_server.spotifyauthorization;

import java.time.LocalDateTime;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.barmiro.sysh_server.users.SyshUserDetails;
import com.github.barmiro.sysh_server.users.SyshUserManager;

import jakarta.annotation.PostConstruct;

@Service
public class SpotifyTokenService {
	

	
	private final String clientId = System.getenv("SPOTIFY_CLIENT_ID");
	private final String clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");
	private final String base64 = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

	private RestClient tokenClient;
	private SpotifyTokenInit tokenInit;
	private SyshUserManager userManager;
	
	public SpotifyTokenService(
			RestClient tokenClient,
			SpotifyTokenInit tokenInit,
			SyshUserManager userManager) {
		this.tokenClient = tokenClient;
		this.tokenInit = tokenInit;
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
	
	public LocalDateTime expTimeFromExpiresIn(int seconds) {
		return LocalDateTime.now().plusSeconds(seconds);
	}
	
	public LocalDateTime getExpTime(String username) {
		return userManager.getUserTokenExpirationTime(username);
	}
	
	public void setRefreshToken(String username, String refreshToken) {
		if (refreshToken != null) {
			userManager.updateUserSpotifyRefreshToken(username, refreshToken);
//			TODO: deal with this
			tokenInit.saveToken(refreshToken);
		}
	}
	
	public String getRefreshToken(String username) {
		return userManager.getUserSpotifyRefreshToken(username);
	}
	
	
	
	public boolean expirationImminent() {
		if (LocalDateTime.now().plusMinutes(5).isAfter(expirationTime)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isAuthenticated() {
		return authenticated;
	}
	

	
	private final String serverUrl = System.getenv("SYSH_SERVER_URL");
	private final String serverPort = System.getenv("SYSH_SERVER_PORT");
	
	public void getNewToken(String code, String username) {
		SpotifyAuthorizationResponseDTO responseBody;
		MultiValueMap<String, String> newBody = new LinkedMultiValueMap<>();
		
		newBody.add("grant_type", "authorization_code");
		newBody.add("code", code);
		newBody.add("redirect_uri", "http://" + serverUrl + ":" + serverPort + "/callback");
		
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
			expTimeFromExpiresIn(responseBody.expires_in());
			setRefreshToken(username, responseBody.refresh_token());
			authenticated = true;
			return;
		} catch (JsonMappingException e) {
			e.printStackTrace();
			throw new HttpClientErrorException(HttpStatus.BAD_GATEWAY);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new HttpClientErrorException(HttpStatus.BAD_GATEWAY);
		}
		
	}
	
	public boolean refresh(String username) {
		
		if (expirationTime == null) {
			if (!authenticated) {
				log.info("Not authenticated with Spotify yet");
				throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
			}
			log.info("The server has been restarted. Refreshing token...");
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
			setToken(responseBody.access_token());
			expTimeFromExpiresIn(responseBody.expires_in());
			if (responseBody.refresh_token() != null) {
				setRefreshToken(responseBody.refresh_token());				
			}
			return true;
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		
		throw new HttpClientErrorException(HttpStatus.BAD_GATEWAY);
	}
	
	@Value("${test.env:false}")
	private boolean isTestEnv;
	
	@PostConstruct
	void initializeToken() {
		setRefreshToken(tokenInit.initToken());
	}
	

}
