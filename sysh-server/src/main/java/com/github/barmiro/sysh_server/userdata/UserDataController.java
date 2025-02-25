package com.github.barmiro.sysh_server.userdata;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.github.barmiro.sysh_server.auth.TokenService;
import com.github.barmiro.sysh_server.common.utils.ConvertDTOs;

@RestController
public class UserDataController {
	TokenService tkn;
	RestClient apiClient;
	
	public UserDataController(TokenService tkn,
			RestClient apiClient) {
		this.tkn = tkn;
		this.apiClient = apiClient;
	}
	


	@GetMapping("/userData")
	public String recent() {
		
		tkn.refresh();
		
		ResponseEntity<String> response = apiClient
				.get()
				.uri("me/")
				.header("Authorization", "Bearer " + tkn.getToken())
				.retrieve()
				.toEntity(String.class);
		
		UserData userData = ConvertDTOs.userData(response);
		
	

		return result;
	}
}
