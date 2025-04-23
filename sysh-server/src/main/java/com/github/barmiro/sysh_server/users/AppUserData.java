package com.github.barmiro.sysh_server.users;

public record AppUserData(
		String username,
		String display_name,
		String timezone,
		Boolean has_imported_data
		) {

}
