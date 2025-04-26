package com.github.barmiro.demo_data_generator;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.barmiro.demo_data_generator.dto.SpotifyAuthorizationResponseDTO;

@Service
public class SpotifyTokenService {

	private final String clientId = System.getenv("SPOTIFY_CLIENT_ID");
	private final String clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");
	private final String base64 = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

	private RestClient tokenClient;
	
	public SpotifyTokenService(
			RestClient tokenClient) {
		this.tokenClient = tokenClient;
	}
	
	ObjectMapper objectMapper = new ObjectMapper();
	private static final Logger log = LoggerFactory.getLogger(SpotifyTokenService.class);
	
	private final String serverUrl = System.getenv("SYSH_SERVER_URL");
	private final String serverPort = System.getenv("SYSH_SERVER_PORT");
	
	public static String spotifyToken;

	public void getNewToken(String code) {
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
			spotifyToken = responseBody.access_token();
			return;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
		}
	}
}
