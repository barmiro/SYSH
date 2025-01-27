package com.github.barmiro.sysh_server.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<String> handleHttpClientError(HttpClientErrorException e) throws InterruptedException {
		if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
			for (int i = 0; i < 5; i++) {
				try {
					//refresh logic
				} catch (HttpClientErrorException ex) {
					if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
						Thread.sleep(2000 * i);
					} else {
						throw ex;
					}
				}
				
			}
		}
		
		System.out.println("Encountered error: " + e.getStatusCode() + "\nAll changes rolled back.");
		return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
	}
	
}
