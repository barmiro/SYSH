package com.github.barmiro.sysh_server.recent;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.github.barmiro.sysh_server.auth.TokenService;

@RestController
public class RecentController {
	
	TokenService tkn;
	RestClient recentClient;
	
	public RecentController(TokenService tkn, RestClient recentClient) {
		this.tkn = tkn;
		this.recentClient = recentClient;
	}
	
	@GetMapping("/recent")
	public String recent() throws Exception {
		ResponseEntity<String> response = recentClient
				.get()
				.uri("me/player/recently-played")
				.header("Authorization", "Bearer " + tkn.getToken())
				.retrieve()
				.toEntity(String.class);
		
		return response.getBody();
	}
}
