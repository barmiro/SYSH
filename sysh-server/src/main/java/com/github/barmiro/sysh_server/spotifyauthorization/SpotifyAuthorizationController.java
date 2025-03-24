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
	private final String serverUrl = System.getenv("SYSH_SERVER_URL");
	private final String serverPort = System.getenv("SYSH_SERVER_PORT");
	
	@GetMapping("/authorize")
	public String authorize() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		String state = userManager.getUserSpotifyState(username);
		String url = "https://accounts.spotify.com/authorize?"
				+ "response_type=code&"
				+ "client_id=" + clientId + "&"
				+ "scope=user-read-recently-played%20"
				+ "user-read-currently-playing%20"
				+ "user-read-playback-state%20"
				+ "user-modify-playback-state&"
				+ "redirect_uri=http://" + serverUrl + ":" + serverPort + "/callback&"
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
				.status(HttpStatus.FOUND)
				.header(HttpHeaders.LOCATION, redirectUrl)
				.build();
		
	};
}
