package com.github.barmiro.sysh_server.catalog.streams;

import java.time.OffsetDateTime;

//This was originally Stream, but wanted to avoid collisions with the Stream class
public record SongStream(
	OffsetDateTime ts,
	String username,
	Integer ms_played,
	String spotify_track_id) {
	
}
