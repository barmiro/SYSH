package com.github.barmiro.sysh_server.auth;

import java.util.Base64;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.barmiro.sysh_server.utils.GetRandom;

@RestController
public class AuthController {
	
	private RestClient tokenClient;
	private RestClient recentClient;
	private AuthResponseBody responseBody;
	
	public AuthController(RestClient tokenClient, RestClient recentClient) {
		this.tokenClient = tokenClient;
		this.recentClient = recentClient;
	}
	
	private final String clientId = System.getenv("SPOTIFY_CLIENT_ID");
	private final String clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");
	private final String base64 = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
	private final String state = GetRandom.alphaNumeric(16);
	
	@GetMapping("/state")
	public String state() {
		return state;
	}
	
	@GetMapping("/authorize")
	public RedirectView authorize() {
		String url = "https://accounts.spotify.com/authorize?"
				+"response_type=code&"
				+"client_id=" + clientId + "&"
				+"scope=user-read-recently-played%20"
				+"user-read-currently-playing%20"
				+"user-read-playback-state%20"
				+"user-modify-playback-state&"
				+"redirect_uri=http://localhost:8080/callback&"
				+"state=" + state;
		return new RedirectView(url);
	}
	
//	TODO: catch exceptions
	@GetMapping("/callback")
	public HttpStatusCode callback(@RequestParam(required=false) String code, @RequestParam(required=true) String state) {
		if (!state.equals(this.state)) {
			return null;
		}
		MultiValueMap<String, String> newBody = new LinkedMultiValueMap<>();
		newBody.add("grant_type", "authorization_code");
		newBody.add("code", code);
		newBody.add("redirect_uri", "http://localhost:8080/callback");
		
		ResponseEntity<String> newEntity = tokenClient
				.post()
				.body(newBody)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.header("Authorization", "Basic " + base64)
				.retrieve()
				.toEntity(String.class);
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		try {
			responseBody = objectMapper.readValue(newEntity.getBody(), AuthResponseBody.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return newEntity.getStatusCode();
	};
	
	
	@GetMapping("/recent")
	public String recent() throws Exception {
		ResponseEntity<String> response = recentClient
				.get()
				.uri("me/player/recently-played")
				.header("Authorization", "Bearer " + responseBody.access_token())
				.retrieve()
				.toEntity(String.class);
		return response.getBody();
	}
}
