package com.github.barmiro.sysh_server.common;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import org.springframework.stereotype.Repository;

import com.github.barmiro.sysh_server.common.records.RecordCompInfo;
import com.github.barmiro.sysh_server.common.records.stats.FullStats;
import com.github.barmiro.sysh_server.common.records.stats.StatsDTO;
import com.github.barmiro.sysh_server.common.utils.CompInfo;
import com.github.barmiro.sysh_server.common.utils.CompListToSql;

@Repository
public class StatsRepository {

	JdbcClient jdbc;
	
	StatsRepository(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}
	
	Logger log = LoggerFactory.getLogger(StatsRepository.class);
	
	public FullStats streamStats(Timestamp startDate, Timestamp endDate) {
		
		Optional<FullStats> cached = getCachedStats(startDate, endDate);
		
		if (cached.isPresent()) {
			return cached.get();
		}
		
//		I will figure out how to do all of this in a single query one day.
		String sql = ("SELECT "
				+ "COUNT(Streams.*) "
				+ "AS stream_count,"
				+ "COUNT(DISTINCT CASE WHEN NOT EXISTS ("
				+ "SELECT 1 FROM Track_Duplicates WHERE Tracks.spotify_track_id = Track_Duplicates.secondary_id"
				+ ") THEN Tracks.spotify_track_id END) "
				+ "AS track_count,"
				+ "COUNT(DISTINCT Tracks.album_id) "
				+ "AS album_count "
				+ "FROM Streams "
				+ "JOIN Tracks ON Streams.spotify_track_id = Tracks.spotify_track_id "
				+ "WHERE Streams.ms_played > 30000 "
				+ "AND Streams.ts BETWEEN :startDate AND :endDate;");
		
//		This is a separate query because I believe even streams below 30s
//		should count towards streaming time
		String minutes = ("SELECT "
				+ "SUM(ms_played) / 60000 AS minutes_streamed "
				+ "FROM Streams "
				+ "WHERE Streams.ts BETWEEN :startDate AND :endDate;");
		
		String artists = ("SELECT "
				+ "COUNT(DISTINCT Tracks_Artists.artist_id) "
				+ "FROM Tracks_Artists "
				+ "JOIN Streams ON "
				+ "Streams.spotify_track_id = Tracks_Artists.spotify_track_id "
				+ "WHERE Streams.ms_played > 30000 "
				+ "AND Streams.ts BETWEEN :startDate AND :endDate;");
		
		
		StatsDTO statsDTO =  jdbc.sql(sql)
				.param("startDate", startDate, Types.TIMESTAMP)
				.param("endDate", endDate, Types.TIMESTAMP)
				.query(StatsDTO.class)
				.single();
		
		Integer minutesStreamed = jdbc.sql(minutes)
				.param("startDate", startDate, Types.TIMESTAMP)
				.param("endDate", endDate, Types.TIMESTAMP)
				.query(Integer.class)
				.single();
		
		Integer artistCount = jdbc.sql(artists)
				.param("startDate", startDate, Types.TIMESTAMP)
				.param("endDate", endDate, Types.TIMESTAMP)
				.query(Integer.class)
				.single();
		
		return new FullStats(startDate,
				endDate,
				minutesStreamed,
				statsDTO.stream_count(),
				statsDTO.track_count(),
				statsDTO.album_count(),
				artistCount);
		
	}
	
	public Optional<FullStats> getCachedStats(Timestamp startDate, Timestamp endDate) {
		String sql = ("SELECT * FROM Stats_Cache "
				+ "WHERE  start_date = :startDate "
				+ "AND end_date = :endDate "
				+ "LIMIT 1;");
		
		return jdbc.sql(sql)
				.param("startDate", startDate, Types.TIMESTAMP)
				.param("endDate", endDate, Types.TIMESTAMP)
				.query(FullStats.class)
				.optional();
	}
	
	
	public int addCachedStats(Timestamp startDate, Timestamp endDate
			) throws IllegalAccessException, InvocationTargetException {
			
		FullStats stats = streamStats(startDate, endDate);

		List<RecordCompInfo> recordComps = CompInfo.get(stats);
		
		String sql = CompListToSql.insertCache(recordComps);
		
		StatementSpec jdbcCall = jdbc.sql(sql);
		
		for (RecordCompInfo comp:recordComps) {
			jdbcCall = jdbcCall.param(
					comp.compName(),
					comp.compValue(),
					comp.sqlType());
		}
		
		int added = 0;
		
		try {
			added = jdbcCall.update();		
			
		} catch(DataIntegrityViolationException e) {
			log.error(
					"Cached stats for "
					+ startDate
					+ " : "
					+ endDate 
					+ " contain invalid values.");
			return 0;
		}
		
		return added;
	}	
}
