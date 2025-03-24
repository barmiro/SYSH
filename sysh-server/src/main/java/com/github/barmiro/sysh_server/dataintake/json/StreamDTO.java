package com.github.barmiro.sysh_server.dataintake.json;

import java.time.OffsetDateTime;

public record StreamDTO(
		OffsetDateTime ts,
		Integer ms_played,
		String spotify_track_uri) {

}
