package com.github.barmiro.sysh_server.security;

public record TokenResponse(
		String username,
		String token
		) {

}
