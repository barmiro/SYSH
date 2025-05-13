package com.github.barmiro.sysh_server.security;

public record SyshUser(
		String username,
		String password,
		UserRole role,
		String timezone,
		String spotify_state,
		Boolean must_change_password) {
	
//	for user registration
	public SyshUser(String username, String password, String timezone) {
		this(username,
			password,
			UserRole.USER,
			timezone,
			"state",
			false);
	}
	
//	for creating users as admin
	public SyshUser(String username, String password, String timezone, UserRole role) {
		this(username,
			password,
			role,
			timezone,
			"state",
			true);
	}
}
