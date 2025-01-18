package com.github.barmiro.sysh_server.catalog.tracks;

import java.sql.Timestamp;

public record Track(
		String spotify_track_id,
		Integer stream_number,
		Integer total_ms_played,
		Timestamp first_played,
		Integer stream_count
){
}
