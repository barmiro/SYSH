package com.github.barmiro.sysh_server.users.register;

import com.github.barmiro.sysh_server.security.UserRole;

public record RegisterResponse(
		String username,
		UserRole role
		) {

}
