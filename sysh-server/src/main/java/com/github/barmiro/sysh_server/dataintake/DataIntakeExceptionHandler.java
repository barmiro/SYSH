package com.github.barmiro.sysh_server.dataintake;

import java.nio.channels.UnresolvedAddressException;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DataIntakeExceptionHandler {

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<String> handleNoSuchElement(NoSuchElementException e) {
		return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(UnresolvedAddressException.class)
	public ResponseEntity<String> handleUnresolvedAddress(UnresolvedAddressException e) {
		System.out.println("Encountered error: " + e.getMessage() + "\nAll changes rolled back.");
		return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
