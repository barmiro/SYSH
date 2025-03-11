package com.github.barmiro.sysh_server.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

	private static final Logger log = LoggerFactory.getLogger(AuthController.class);
	
	private final JwtService jwtService;
	
	public AuthController(JwtService jwtService) {
		this.jwtService = jwtService;
	}
	
	
	@PostMapping("/token")
	public TokenResponse token(Authentication authentication) {
		log.info("Token requested tor user: '{}'", authentication.getName());
		String token = jwtService.generateToken(authentication);
		log.info("Token granted: {}", token);
		
		TokenResponse response = new TokenResponse(token);
		
		return response;
	}
	
}
