package com.github.barmiro.sysh_server.catalog.streams;

import java.sql.Timestamp;

//This was originally Stream, but wanted to avoid collisions with the Stream class
public record SongStream(
	Timestamp ts,
	String username,
	Integer ms_played,
	String spotify_track_id) {
	
}
