package com.github.barmiro.sysh_server.stats.dto;

public record FullStats(
		String username,
		Integer minutes_streamed,
		Integer stream_count,
		Integer track_count,
		Integer album_count,
		Integer artist_count) {

}
