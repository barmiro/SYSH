package com.example.sysh_server.streams;

import java.sql.Timestamp;

public record Track(
		String spotify_track_uri,
		String master_metadata_track_name,
		String master_metadata_album_artist_name,
		String master_metadata_album_album_name,
		Integer stream_number,
		Integer total_ms_played,
		Timestamp first_played
){
}
