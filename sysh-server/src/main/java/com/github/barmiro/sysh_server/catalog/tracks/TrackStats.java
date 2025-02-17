package com.github.barmiro.sysh_server.catalog.tracks;

public record TrackStats(
		String spotify_track_id,
		String name,
		Integer stream_count,
		Integer minutes_played) {
}
