package com.github.barmiro.sysh_server.common.utils;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

import com.github.barmiro.sysh_server.catalog.streams.SongStream;
import com.github.barmiro.sysh_server.common.records.OffsetDateTimeRange;

public class GetTimestampRange {

	 public static OffsetDateTimeRange fromSongStreamList (List<SongStream> streams) {
		OffsetDateTime oldestStreamDateTime = streams
//				not to be confused with a single stream from the list, see: SongStream.java
				.stream()
				.min(Comparator.comparing(SongStream::ts))
				.get()
				.ts();
		
		OffsetDateTime newestStreamDateTime = streams
//				not to be confused with a single stream from the list, see: SongStream.java
				.stream()
				.max(Comparator.comparing(SongStream::ts))
				.get()
				.ts();
		
		return new OffsetDateTimeRange(oldestStreamDateTime, newestStreamDateTime);
	}
}
