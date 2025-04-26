package com.github.barmiro.demo_data_generator;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.github.barmiro.demo_data_generator.util.GetRandom;

@RestController
public class SpotifyAuthorizationController {
	
	private SpotifyTokenService tkn;
	
	public SpotifyAuthorizationController(SpotifyTokenService tkn) {
		this.tkn = tkn;
	}
	
	private final String clientId = System.getenv("SPOTIFY_CLIENT_ID");
	private final String serverUrl = System.getenv("SYSH_SERVER_URL");
	private final String serverPort = System.getenv("SYSH_SERVER_PORT");
	private final String state = GetRandom.alphaNumeric(32);
	
	@GetMapping("/authorize")
	public RedirectView authorize() {
		String url = "https://accounts.spotify.com/authorize?"
				+ "response_type=code&"
				+ "client_id=" + clientId + "&"
				+ "redirect_uri=http://" + serverUrl + ":" + serverPort + "/callback&"
				+ "state=" + state;
		return new RedirectView(url);
	}
	
	
	
	@GetMapping("/callback")
	public String callback(
			@RequestParam(required=false) Optional<String> code,
			@RequestParam String state) {
		
		String codeValue = code.orElseThrow();
		
		
		tkn.getNewToken(codeValue);

		if (!state.equals(this.state)) {
			return "Expected state: " + this.state + " but was: " + state;
		}

		return codeValue;	
	};
}
