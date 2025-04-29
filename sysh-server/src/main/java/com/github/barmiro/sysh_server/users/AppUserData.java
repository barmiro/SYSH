package com.github.barmiro.sysh_server.users;

import com.github.barmiro.sysh_server.security.UserRole;

public record AppUserData(
		String username,
		String display_name,
		String image_url,
		String timezone,
		Boolean has_imported_data,
		UserRole role
		) {

}
