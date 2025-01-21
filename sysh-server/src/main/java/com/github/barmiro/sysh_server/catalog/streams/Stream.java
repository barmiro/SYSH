package com.github.barmiro.sysh_server.catalog.streams;

import java.sql.Timestamp;

public record Stream(
	Timestamp ts,
	Integer ms_played,
	String spotify_track_id) {
	
}
