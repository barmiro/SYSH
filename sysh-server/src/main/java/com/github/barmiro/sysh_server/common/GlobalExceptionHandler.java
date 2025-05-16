package com.github.barmiro.sysh_server.common;

import java.nio.channels.UnresolvedAddressException;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<String> handleHttpClientError(HttpClientErrorException e) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth != null && auth.isAuthenticated()) {
			
			String username = auth.getName();
			
			if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) {
				String message = ("User '"
						+ username 
						+ "' not authorized with Spotify");
				log.error(message + ": " + e.getMessage());
				return new ResponseEntity<String>(message,
						HttpStatus.PRECONDITION_FAILED);
				
			} else if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
				log.error(e.getMessage());
				return new ResponseEntity<String>("The server is making too many calls to Spotify. "
						+ "If the issue persists, please contact your system administrator",
						HttpStatus.TOO_MANY_REQUESTS);
			}
		}
		e.printStackTrace();
		log.error("Encountered error: "
				+ e.getStatusCode()
				+ "\nAll changes rolled back.");
		
		return new ResponseEntity<String>(e.getMessage(), e.getStatusCode());
	}
	
	@ExceptionHandler(HttpServerErrorException.class)
	public ResponseEntity<String> handleHttpServerError(HttpServerErrorException e) {
		e.printStackTrace();
		return new ResponseEntity<String>("Spotify is having some issues. Please try again later.", HttpStatus.BAD_GATEWAY);
	}
	
	@ExceptionHandler(ResourceAccessException.class)
	public ResponseEntity<String> handleResourceAccessError(ResourceAccessException e) {
		e.printStackTrace();
		return new ResponseEntity<String>("Spotify is having some issues. Please try again later.", HttpStatus.BAD_GATEWAY);
	}
	
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<String> handleNoSuchElement(NoSuchElementException e) {
		return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(UnresolvedAddressException.class)
	public ResponseEntity<String> handleUnresolvedAddress(UnresolvedAddressException e) {
		log.error("Encountered error: " + e.getMessage() + "\nAll changes rolled back.");
		return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}




