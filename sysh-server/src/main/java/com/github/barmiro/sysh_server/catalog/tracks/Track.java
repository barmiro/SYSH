package com.github.barmiro.sysh_server.catalog.tracks;

public record Track(
		String spotify_track_id,
		String name,
		Integer duration_ms,
		String album_id
){
}
