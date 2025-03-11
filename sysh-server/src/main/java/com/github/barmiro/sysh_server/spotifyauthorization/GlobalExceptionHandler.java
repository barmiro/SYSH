package com.github.barmiro.sysh_server.spotifyauthorization;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	SpotifyTokenService tkn;
	
	GlobalExceptionHandler(SpotifyTokenService tkn) {
		this.tkn = tkn;
	}


	
	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<String> handleHttpClientError(HttpClientErrorException e) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth != null && auth.isAuthenticated()) {
			
			String username = auth.getName();
			
			if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
				return new ResponseEntity<String>("User '"
						+ username 
						+ "' not authorized with Spotify",
						HttpStatus.BAD_GATEWAY);
				
			} else if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
				
				return new ResponseEntity<String>("The server is making too many calls to Spotify. "
						+ "If the issue persists, please contact your system administrator",
						HttpStatus.TOO_MANY_REQUESTS);
			}
		}
		
		System.out.println("Encountered error: "
				+ e.getStatusCode()
				+ "\nAll changes rolled back.");
		
		return new ResponseEntity<String>(e.getMessage(), e.getStatusCode());
	}
	
	@ExceptionHandler(HttpServerErrorException.class)
	public ResponseEntity<String> handleHttpServerError(HttpServerErrorException e) {
		return new ResponseEntity<String>("Spotify is having some issues. Please try again later.", HttpStatus.BAD_GATEWAY);
	}
	
	
	
}




