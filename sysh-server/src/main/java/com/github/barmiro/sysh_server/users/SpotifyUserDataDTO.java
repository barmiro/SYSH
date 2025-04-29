package com.github.barmiro.sysh_server.users;

import java.util.List;

public record SpotifyUserDataDTO(
		String display_name,
		List<SpotifyImageDTO> images) {

}
