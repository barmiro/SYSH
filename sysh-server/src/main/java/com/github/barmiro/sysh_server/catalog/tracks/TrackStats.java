package com.github.barmiro.sysh_server.catalog.tracks;

public record TrackStats(
		String username,
		String spotify_track_id,
		String name,
		String album_name,
		String thumbnail_url,
		String image_url,
		String primary_artist_name,
		Integer stream_count,
		Integer total_ms_played) {
}
