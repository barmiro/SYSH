package com.github.barmiro.sysh_server.streams;

import java.sql.Timestamp;

public record Stream(
	Timestamp ts,
	Integer ms_played,
	String master_metadata_track_name,
	String master_metadata_album_artist_name,
	String master_metadata_album_album_name,
	String spotify_track_uri
){
}
