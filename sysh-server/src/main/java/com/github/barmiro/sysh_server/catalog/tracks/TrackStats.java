package com.github.barmiro.sysh_server.catalog.tracks;

public record TrackStats(
		String spotify_track_id,
		String name,
		String album_name,
		String thumbnail_url,
		String artist_names,
		Integer stream_count,
		Integer minutes_played) {
}
