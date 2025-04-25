package com.github.barmiro.sysh_server.security;

public record SyshUser(
		String username,
		String password,
		UserRole role,
		String timezone,
		String spotify_state) {
	
	public SyshUser(String username, String password, String timezone) {
		this(username,
			password,
			UserRole.USER,
			timezone,
			"state");
	}
}
