package com.github.barmiro.sysh_server.auth;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class TokenService {
	private String token;
	private LocalDateTime expirationTime;
	private String refreshToken;
	
	private final String clientId = System.getenv("SPOTIFY_CLIENT_ID");
	private final String clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");
	private final String base64 = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

	private RestClient tokenClient;
	private JdbcClient jdbc;
	
	public TokenService(
			RestClient tokenClient,
			JdbcClient jdbc) {
		this.tokenClient = tokenClient;
		this.jdbc = jdbc;
	}
	
	ObjectMapper objectMapper = new ObjectMapper();
	private static final Logger log = LoggerFactory.getLogger(TokenService.class);
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getToken() {
		return token;
	}
	
	public void expTimeFromExpiresIn(int seconds) {
		this.expirationTime = LocalDateTime.now().plusSeconds(seconds);
	}
	
	public LocalDateTime getExpTime() {
		return expirationTime;
	}
	
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}
	
	public boolean expirationImminent() {
		if (LocalDateTime.now().plusMinutes(5).isAfter(expirationTime)) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
	public void getNewToken(String code) {
		AuthResponseDTO responseBody;
		MultiValueMap<String, String> newBody = new LinkedMultiValueMap<>();
		
		newBody.add("grant_type", "authorization_code");
		newBody.add("code", code);
		newBody.add("redirect_uri", "http://localhost:8080/callback");
		
//		RestClient tokenClient = RestClient.builder()
//				.baseUrl("https://accounts.spotify.com/api/token")
//				.build();
		
		ResponseEntity<String> newEntity = tokenClient
				.post()
				.body(newBody)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.header("Authorization", "Basic " + base64)
				.retrieve()
				.toEntity(String.class);
		
		
		try {
			responseBody = objectMapper.readValue(newEntity.getBody(), AuthResponseDTO.class);
			setToken(responseBody.access_token());
			expTimeFromExpiresIn(responseBody.expires_in());
			setRefreshToken(responseBody.refresh_token());
			return;
		} catch (JsonMappingException e) {
			e.printStackTrace();
			throw new HttpClientErrorException(HttpStatus.BAD_GATEWAY);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new HttpClientErrorException(HttpStatus.BAD_GATEWAY);
		}
		
	}
	
	public boolean refresh() {
		
		if (expirationTime == null) {
			if (refreshToken == null) {
				log.info("Not authorized with Spotify yet");
				throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
			}
			log.info("The server has been restarted. Refreshing token...");
		} else if (LocalDateTime.now().plusMinutes(5).isBefore(expirationTime)) {
			log.info("Token valid until: " + expirationTime + ". Proceeding...");
			return false;
		} else {
			log.info("Token expires: " + expirationTime + ". Getting new token...");			
		}
		AuthResponseDTO responseBody;
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
			responseBody = objectMapper.readValue(newEntity.getBody(), AuthResponseDTO.class);
			setToken(responseBody.access_token());
			expTimeFromExpiresIn(responseBody.expires_in());
			setRefreshToken(responseBody.refresh_token());
			return true;
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		
		throw new HttpClientErrorException(HttpStatus.BAD_GATEWAY);
	}
	
//	I don't like this approach, but it was quick
	@Value("${test.env:false}")
	private boolean isTestEnv;

	@PostConstruct
	public void initToken() {
		
		
		if (isTestEnv) {
			return;
		}
		
		List<String> tokenList = new ArrayList<>();
		try {
			tokenList = jdbc.sql("SELECT token FROM Refresh LIMIT 1").query(String.class).list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (tokenList.size() == 0 || tokenList.get(0) == null || tokenList.get(0).isEmpty()) {
			return;
		}
		refreshToken = tokenList.get(0);
		jdbc.sql("DELETE FROM Refresh").update();
	}
	
	
	@PreDestroy
	public void saveToken() {
		if (isTestEnv) {
			return;
		}
		
		try {
			jdbc.sql("DELETE FROM Refresh").update();
			if (refreshToken == null) {
				return;
			}
			jdbc.sql("INSERT INTO Refresh (token) VALUES (:refreshToken)")
			.param("refreshToken", refreshToken, Types.VARCHAR)
			.update();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
