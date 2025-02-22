package com.github.barmiro.sysh_server.common.utils;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;

import com.github.barmiro.sysh_server.catalog.streams.SongStream;
import com.github.barmiro.sysh_server.common.records.TimestampRange;

public class GetTimestampRange {

	 public static TimestampRange fromSongStreamList (List<SongStream> streams) {
		Timestamp oldestStreamTimestamp = streams
//				not to be confused with a single stream from the list, see: SongStream.java
				.stream()
				.min(Comparator.comparing(SongStream::ts))
				.get()
				.ts();
		
		Timestamp newestStreamTimestamp = streams
//				not to be confused with a single stream from the list, see: SongStream.java
				.stream()
				.max(Comparator.comparing(SongStream::ts))
				.get()
				.ts();
		
		return new TimestampRange(oldestStreamTimestamp, newestStreamTimestamp);
	}
}
