package com.github.barmiro.sysh_server.integration.json;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = JsonController.class)
public class JsonControllerExceptionHandler {

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<String> handleMessageNotReadable(HttpMessageNotReadableException e) {
		return new ResponseEntity<>(
				"Please select a valid .json file\n"
				+ e.getMessage()
				+ "\n",
				HttpStatusCode.valueOf(400));
	}
	
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<String> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
		return new ResponseEntity<>(
				"Media type not supported. Check your headers.\n"
				+ e.getMessage()
				+ "\n",
				HttpStatusCode.valueOf(415));
				
	}
}
