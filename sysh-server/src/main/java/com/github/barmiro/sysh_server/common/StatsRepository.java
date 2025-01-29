package com.github.barmiro.sysh_server.common;

import java.sql.Timestamp;
import java.sql.Types;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.github.barmiro.sysh_server.common.records.stats.FullStats;
import com.github.barmiro.sysh_server.common.records.stats.StatsDTO;

@Repository
public class StatsRepository {

	JdbcClient jdbc;
	
	StatsRepository(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}
	
	public FullStats streamStats(Timestamp startDate, Timestamp endDate) {
		String sql = ("SELECT "
				+ "SUM(Streams.ms_played) / 60000 "
				+ "AS minutes_streamed,"
				+ "COUNT(CASE WHEN Streams.ms_played > 30000 THEN 1 END) "
				+ "AS stream_count,"
				+ "COUNT(DISTINCT CASE WHEN NOT EXISTS ("
				+ "SELECT 1 FROM Track_Duplicates WHERE Tracks.spotify_track_id = Track_Duplicates.secondary_id"
				+ ") THEN Tracks.spotify_track_id END) "
				+ "AS track_count,"
				+ "COUNT(DISTINCT Tracks.album_id) "
				+ "AS album_count "
				+ "FROM Streams "
				+ "JOIN Tracks ON Streams.spotify_track_id = Tracks.spotify_track_id "
				+ "WHERE Streams.ts BETWEEN :startDate AND :endDate;");
		

		String artists = ("SELECT "
				+ "COUNT(DISTINCT Tracks_Artists.artist_id) "
				+ "FROM Tracks_Artists "
				+ "JOIN Streams ON "
				+ "Streams.spotify_track_id = Tracks_Artists.spotify_track_id "
				+ "WHERE Streams.ts BETWEEN :startDate AND :endDate;");
		
		
		StatsDTO statsDTO =  jdbc.sql(sql)
				.param("startDate", startDate, Types.TIMESTAMP)
				.param("endDate", endDate, Types.TIMESTAMP)
				.query(StatsDTO.class)
				.single();
		
		Integer artistCount = jdbc.sql(artists)
				.param("startDate", startDate, Types.TIMESTAMP)
				.param("endDate", endDate, Types.TIMESTAMP)
				.query(Integer.class)
				.single();
		
		return new FullStats(statsDTO.minutes_streamed(),
				statsDTO.stream_count(),
				statsDTO.track_count(),
				statsDTO.album_count(),
				artistCount);
		
	}
	
}
