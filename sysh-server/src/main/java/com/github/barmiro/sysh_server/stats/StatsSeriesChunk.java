package com.github.barmiro.sysh_server.stats;

import java.time.OffsetDateTime;

public record StatsSeriesChunk(
		String username,
		OffsetDateTime start_date,
		OffsetDateTime end_date,
		Integer minutes_streamed,
		Integer stream_count) {

}
