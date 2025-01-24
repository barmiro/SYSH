package com.github.barmiro.sysh_server.dataintake.json;

import org.springframework.http.HttpStatus;
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
				HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<String> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
		return new ResponseEntity<>(
				"Media type not supported. Check your headers.\n"
				+ e.getMessage()
				+ "\n",
				HttpStatus.UNSUPPORTED_MEDIA_TYPE);
				
	}
}
