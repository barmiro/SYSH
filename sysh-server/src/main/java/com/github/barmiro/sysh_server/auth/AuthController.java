package com.github.barmiro.sysh_server.auth;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.github.barmiro.sysh_server.common.utils.GetRandom;

@RestController
public class AuthController {
	
	private TokenService tkn;
	
	public AuthController(TokenService tkn) {
		this.tkn = tkn;
	}
	
	private final String clientId = System.getenv("SPOTIFY_CLIENT_ID");
	private final String state = GetRandom.alphaNumeric(16);
	private final String serverUrl = System.getenv("SYSH_SERVER_URL");
	private final String serverPort = System.getenv("SYSH_SERVER_PORT");
	
	@GetMapping("/state")
	public String state() {
		return state;
	}
	
	@GetMapping("/authorize")
	public RedirectView authorize() {
		String url = "https://accounts.spotify.com/authorize?"
				+ "response_type=code&"
				+ "client_id=" + clientId + "&"
				+ "scope=user-read-recently-played%20"
				+ "user-read-currently-playing%20"
				+ "user-read-playback-state%20"
				+ "user-modify-playback-state&"
				+ "redirect_uri=http://" + serverUrl + ":" + serverPort + "/callback&"
				+ "state=" + state;
		return new RedirectView(url);
	}
	
	
	@GetMapping("/error")
	public String error(@RequestParam(required=false) String message) {
		return "There was an error. \n" + message;
	}
	
	
	@GetMapping("/callback")
	public RedirectView callback(@RequestParam(required=false) Optional<String> code, @RequestParam String state) {
		
		
		if (!state.equals(this.state)) {
			RedirectView stateMismatch = new RedirectView("/error");
			stateMismatch.addStaticAttribute("message", "The state returned by Spotify was wrong");
			return stateMismatch;
		}
		
		String codeValue = code.orElseThrow();
		
		tkn.getNewToken(codeValue);
		
		return new RedirectView("/userData");	
	};
}
