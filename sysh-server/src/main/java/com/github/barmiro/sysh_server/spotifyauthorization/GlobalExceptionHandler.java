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

	@ExceptionHandler(InternalUnauthorizedException.class)
	public ResponseEntity<String> handleInternalUnauthorized(InternalUnauthorizedException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
	}
	
//	public ResponseEntity<ErrorResponse> handleInternalUnauthorized(InternalUnauthorizedException e) {
//		
//        return new ErrorResponse(HttpStatus.UNAUTHORIZED);
//	}
	
	
	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<String> handleHttpClientError(HttpClientErrorException e) throws InterruptedException {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth != null && auth.isAuthenticated()) {
			
			String username = auth.getName();
			
			if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
				throw new HttpServerErrorException(null);
			}
			
		}
		
		
		System.out.println("Encountered error: " + e.getStatusCode() + "\nAll changes rolled back.");
		
		return new ResponseEntity<String>(e.getMessage(), e.getStatusCode());
	}
	
}


//for (int i = 0; i < 5; i++) {
//	try {
//		tkn.refresh(username);
//	} catch (HttpClientErrorException ex) {
//		if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
//			Thread.sleep(2000 * i);
//		} else {
//			throw ex;
//		}
//	}
//	
//}