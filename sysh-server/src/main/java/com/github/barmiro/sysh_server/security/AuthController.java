package com.github.barmiro.sysh_server.security;


import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
	
	private final JwtService jwtService;
	
	public AuthController(JwtService jwtService) {
		this.jwtService = jwtService;
	}
	
	
	@PostMapping("/token")
	public TokenResponse token(Authentication authentication) {
		String token = jwtService.generateToken(authentication);
		
		TokenResponse response = new TokenResponse(authentication.getName(), token);
		
		return response;
	}
	
}
