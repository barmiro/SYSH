package com.github.barmiro.sysh_server.auth;

import java.time.LocalDateTime;
import java.util.Base64;

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

@Service
public class TokenService {
	private String token;
	private LocalDateTime expirationTime;
	private String refreshToken;
	
	private final String clientId = System.getenv("SPOTIFY_CLIENT_ID");
	private final String clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");
	private final String base64 = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

	private RestClient tokenClient;
	
	public TokenService(RestClient tokenClient) {
		this.tokenClient = tokenClient;
	}
	
	ObjectMapper objectMapper = new ObjectMapper();
	
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
		System.out.println("getNewToken called");
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
	
	public void refresh() {
		
		if (LocalDateTime.now().plusMinutes(5).isBefore(expirationTime)) {
			System.out.println("Token valid until: " + expirationTime + ". Proceeding...");
			return;
		}
		System.out.println("Token expires: " + expirationTime + ", getting new token...");
		AuthResponseDTO responseBody;
		MultiValueMap<String, String> newBody = new LinkedMultiValueMap<>();
		
		newBody.add("grant_type", "refresh_token");
		newBody.add("refresh-token", refreshToken);
		
		
		
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
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		
		throw new HttpClientErrorException(HttpStatus.BAD_GATEWAY);
	}
	
}
