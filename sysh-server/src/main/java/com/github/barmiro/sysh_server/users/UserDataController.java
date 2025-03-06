package com.github.barmiro.sysh_server.users;

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
	SyshUserRepository userDataRepository;
	
	public UserDataController(SpotifyTokenService tkn,
			RestClient apiClient,
			SyshUserRepository userDataRepository) {
		this.tkn = tkn;
		this.apiClient = apiClient;
		this.userDataRepository = userDataRepository;
	}
	


	@GetMapping("/userData")
	public String getUserData() {
		
		tkn.refresh();
		
		ResponseEntity<String> response = apiClient
				.get()
				.uri("me/")
				.header("Authorization", "Bearer " + tkn.getToken())
				.retrieve()
				.toEntity(String.class);
		
		SpotifyUserData userData = ConvertDTOs.userData(response);
		
//		int updated = userDataRepository.addUserData(userData);
//		
//		if (updated == 1) {
//			return userData.display_name();			
//		}
		if (userData.display_name() == null) {
			return ("unknown username");			
		} else {
			return userData.display_name();
		}
	
	}
}
