package com.github.barmiro.sysh_server.spotifyauthorization;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "User not authorized")
public class InternalUnauthorizedException extends RuntimeException {
	private static final long serialVersionUID = -240117406102747402L;

	public InternalUnauthorizedException(String message) {
		super(message);
	}
}
