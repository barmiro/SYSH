package com.github.barmiro.sysh_server.stats.dto;

import java.time.OffsetDateTime;

public record StatsForRange(
		String username,
		OffsetDateTime start_date,
		OffsetDateTime end_date,
		Integer minutes_streamed,
		Integer stream_count,
		Integer track_count,
		Integer album_count,
		Integer artist_count) {

}
