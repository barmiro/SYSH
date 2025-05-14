package com.github.barmiro.sysh_server.users.register;

public record PasswordChangeRequest(
		String oldPassword,
		String newPassword
		) {

}
