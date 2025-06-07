package com.github.barmiro.sysh_server.spotifyauthorization;

import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.barmiro.sysh_server.users.SyshUserManager;

@RestController
public class SpotifyAuthorizationController {
	
	private SpotifyTokenService tkn;
	private SyshUserManager userManager;
	
	public SpotifyAuthorizationController(SpotifyTokenService tkn, SyshUserManager manager) {
		this.tkn = tkn;
		this.userManager = manager;
	}
	
	private final String clientId = System.getenv("SPOTIFY_CLIENT_ID");
	
	@GetMapping("/authorize")
	public String authorize() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		String state = userManager.getUserSpotifyState(username);
		String url = "https://accounts.spotify.com/authorize?"
				+ "response_type=code&"
				+ "client_id=" + clientId + "&"
				+ "scope=user-read-recently-played&"
//				TODO: IF THIS STOPS WORKING, ROLL BACK
				+ "redirect_uri=sysh://open/callback&"
				+ "state=" + state;
		return url;
	}
	
	
	@GetMapping("/error")
	public String error(@RequestParam(required=false) String message) {
		return "There was an error. \n" + message;
	}
	
	
	@GetMapping("/callback")
	public ResponseEntity<Void> callback(
			@RequestParam(required=false) Optional<String> code,
			@RequestParam String state) {
		String username = userManager.getUsernameBySpotifyState(state);
		
		String codeValue = code.orElseThrow();
		
		tkn.getNewToken(codeValue, username);
		
		String redirectUrl = "sysh://open";
		
		return ResponseEntity
				.status(HttpStatus.SEE_OTHER)
				.header(HttpHeaders.LOCATION, redirectUrl)
				.build();
		
	};
	
	@GetMapping("/callbackNew")
	public void callbackNew(
			@RequestParam String code,
			@RequestParam String state) {
		
		String username = userManager.getUsernameBySpotifyState(state);
		
		tkn.getNewToken(code, username);
		
		return;
	};
}
