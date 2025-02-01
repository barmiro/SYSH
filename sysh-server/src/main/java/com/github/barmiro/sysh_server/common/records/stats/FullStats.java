package com.github.barmiro.sysh_server.common.records.stats;

import java.sql.Timestamp;

public record FullStats(
		Timestamp start_date,
		Timestamp end_date,
		Integer minutes_streamed,
		Integer stream_count,
		Integer track_count,
		Integer album_count,
		Integer artist_count) {

}
