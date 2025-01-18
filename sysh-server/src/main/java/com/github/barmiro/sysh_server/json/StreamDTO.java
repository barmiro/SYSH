package com.github.barmiro.sysh_server.json;

import java.sql.Timestamp;

public record StreamDTO(
		Timestamp ts,
		Integer ms_played,
		String spotify_track_uri) {

}
