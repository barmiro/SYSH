package com.github.barmiro.sysh_server.security;

public record SyshUser(
		String username,
		String password,
		String spotify_state) {
	
	public SyshUser(String username, String password) {
		this(username,
			password,
			"state");
	}
}
