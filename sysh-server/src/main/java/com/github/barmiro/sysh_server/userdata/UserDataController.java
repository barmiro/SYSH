package com.github.barmiro.sysh_server.userdata;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.github.barmiro.sysh_server.common.utils.ConvertDTOs;
import com.github.barmiro.sysh_server.spotifyauthorization.SpotifyTokenService;

@RestController
public class UserDataController {
	SpotifyTokenService tkn;
	RestClient apiClient;
	UserDataRepository userDataRepository;
	
	public UserDataController(SpotifyTokenService tkn,
			RestClient apiClient,
			UserDataRepository userDataRepository) {
		this.tkn = tkn;
		this.apiClient = apiClient;
		this.userDataRepository = userDataRepository;
	}
	


	@GetMapping("/userData")
	public String userData() {
		
		tkn.refresh();
		
		ResponseEntity<String> response = apiClient
				.get()
				.uri("me/")
				.header("Authorization", "Bearer " + tkn.getToken())
				.retrieve()
				.toEntity(String.class);
		
		UserData userData = ConvertDTOs.userData(response);
		
		int updated = userDataRepository.addUserData(userData);
		
		if (updated == 1) {
			return userData.display_name();			
		}
		
		return ("unknown username");
	
	}
}
