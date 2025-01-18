package com.github.barmiro.sysh_server.catalog.tracks;

import java.sql.Timestamp;

public record Track(
		String spotify_track_uri,
		String master_metadata_track_name,
		String master_metadata_album_album_name,		//change to album URI
		String master_metadata_album_artist_name,		//change to list of artist URIs
		Integer stream_number,
		Integer total_ms_played,
		Timestamp first_played,
		Integer stream_count
){
}
