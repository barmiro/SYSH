package com.github.barmiro.sysh_server.integration;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class IntegrationExceptionHandler {

	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<String> handleHttpClientError(HttpClientErrorException e) {
		return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
	}
}
