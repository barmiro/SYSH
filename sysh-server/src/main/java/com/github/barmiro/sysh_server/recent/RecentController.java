package com.github.barmiro.sysh_server.recent;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.barmiro.sysh_server.auth.TokenService;
import com.github.barmiro.sysh_server.recent.dto.ItemsWrapper;

@RestController
public class RecentController {
	
	TokenService tkn;
	RestClient recentClient;
	
	public RecentController(TokenService tkn, RestClient recentClient) {
		this.tkn = tkn;
		this.recentClient = recentClient;
	}
	
	ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	@GetMapping("/recent")
	public ItemsWrapper recent() throws Exception {
		ResponseEntity<String> response = recentClient
				.get()
				.uri("me/player/recently-played")
				.header("Authorization", "Bearer " + tkn.getToken())
				.retrieve()
				.toEntity(String.class);
		
		
		return objectMapper.readValue(response.getBody(), ItemsWrapper.class);
	}
}
