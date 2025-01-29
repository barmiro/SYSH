package com.github.barmiro.sysh_server.common.records.stats;

public record FullStats(
		Integer minutes_streamed,
		Integer stream_count,
		Integer track_count,
		Integer album_count,
		Integer artist_count) {

}
