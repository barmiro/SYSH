package com.github.barmiro.sysh_server.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
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
	
	Logger log = LoggerFactory.getLogger(UserDataController.class);


	@GetMapping("/userData")
	public AppUserData getUserData() throws HttpClientErrorException {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		
		tkn.refresh(username);
		

		ResponseEntity<String> response = apiClient
				.get()
				.uri("me/")
				.header("Authorization", "Bearer " + tkn.getToken(username))
				.retrieve()
				.toEntity(String.class);
		
		SpotifyUserDataDTO spotifyUserData = ConvertDTOs.spotifyUserData(response);
		
		int updated = userDataRepository.addSpotifyUserData(username, spotifyUserData);
		
		if (updated == 1) {
//			return userData.display_name();			
		} else {
			log.error("Couldn't add display name for " + username + " to database");
		}

		
		return userDataRepository.getAppUserData(username);
	
	}
}
