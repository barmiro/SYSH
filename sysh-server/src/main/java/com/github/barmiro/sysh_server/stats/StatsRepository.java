package com.github.barmiro.sysh_server.stats;

import java.lang.reflect.InvocationTargetException;
import java.sql.Types;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import org.springframework.stereotype.Repository;

import com.github.barmiro.sysh_server.common.records.RecordCompInfo;
import com.github.barmiro.sysh_server.common.utils.CompInfo;
import com.github.barmiro.sysh_server.common.utils.CompListToSql;

@Repository
public class StatsRepository {

	JdbcClient jdbc;
	
	StatsRepository(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}
	
	Logger log = LoggerFactory.getLogger(StatsRepository.class);
	
	
	public StatsSeriesChunk streamStatsSeries(OffsetDateTime startDate, OffsetDateTime endDate, String username) {
		
		String streamCountSql = ("SELECT "
				+ "COUNT(id) as stream_count "
				+ "FROM SongStreams "
				+ "WHERE ms_played >= 30000 "
				+ "AND username = :username "
				+ "AND ts BETWEEN :startDate AND :endDate;");
		
		String minutesStreamedSql = ("SELECT "
				+ "COALESCE(SUM(SongStreams.ms_played), 0) / 60000 AS minutes_played "
				+ "FROM SongStreams "
				+ "WHERE username = :username "
				+ "AND SongStreams.ts BETWEEN :startDate AND :endDate;");
		
		Integer streamCount = jdbc.sql(streamCountSql)
				.param("username", username, Types.VARCHAR)
				.param("startDate", startDate, Types.TIMESTAMP_WITH_TIMEZONE)
				.param("endDate", endDate, Types.TIMESTAMP_WITH_TIMEZONE)
				.query(Integer.class)
				.single();
		
		Integer minutesStreamed = jdbc.sql(minutesStreamedSql)
				.param("username", username, Types.VARCHAR)
				.param("startDate", startDate, Types.TIMESTAMP_WITH_TIMEZONE)
				.param("endDate", endDate, Types.TIMESTAMP_WITH_TIMEZONE)
				.query(Integer.class)
				.single();
		
		return new StatsSeriesChunk(username, startDate, endDate, minutesStreamed, streamCount);
	}
		
	public StatsForRange streamStats(OffsetDateTime startDate, OffsetDateTime endDate, Boolean checkForCache, String username) {
		
		if (checkForCache) {
			Optional<StatsForRange> cached = getCachedStats(startDate, endDate, username);
			if (cached.isPresent()) {
				return cached.get();
			}			
		}
		
//		I will figure out how to do all of this in a single query one day.
		String sql = ("SELECT "
				+ "COUNT(SongStreams.*) "
				+ "AS stream_count,"
				+ "COUNT(DISTINCT COALESCE("
				+ "Track_Duplicates.primary_id, Tracks.spotify_track_id)) "
				+ "AS track_count,"
				+ "COUNT(DISTINCT Tracks.album_id) "
				+ "AS album_count "
				+ "FROM SongStreams "
				+ "JOIN Tracks ON SongStreams.spotify_track_id = Tracks.spotify_track_id "
				+ "LEFT JOIN Track_Duplicates ON Tracks.spotify_track_id = Track_Duplicates.secondary_id "
				+ "WHERE SongStreams.ms_played > 30000 "
				+ "AND SongStreams.username = :username "
				+ "AND SongStreams.ts BETWEEN :startDate AND :endDate;");
		
//		This is a separate query because I believe even streams below 30s
//		should count towards streaming time
		String minutes = ("SELECT "
				+ "COALESCE(SUM(ms_played) / 60000, 0) AS minutes_streamed "
				+ "FROM SongStreams "
				+ "WHERE SongStreams.username = :username "
				+ "AND SongStreams.ts BETWEEN :startDate AND :endDate;");
		
		String artists = ("SELECT "
				+ "COUNT(DISTINCT Tracks_Artists.artist_id) "
				+ "FROM Tracks_Artists "
				+ "JOIN SongStreams ON "
				+ "SongStreams.spotify_track_id = Tracks_Artists.spotify_track_id "
				+ "WHERE SongStreams.ms_played > 30000 "
				+ "AND SongStreams.username = :username "
				+ "AND SongStreams.ts BETWEEN :startDate AND :endDate;");
		
		
		StatsDTO statsDTO =  jdbc.sql(sql)
				.param("username", username, Types.VARCHAR)
				.param("startDate", startDate, Types.TIMESTAMP_WITH_TIMEZONE)
				.param("endDate", endDate, Types.TIMESTAMP_WITH_TIMEZONE)
				.query(StatsDTO.class)
				.single();
		
		Integer minutesStreamed = jdbc.sql(minutes)
				.param("username", username, Types.VARCHAR)
				.param("startDate", startDate, Types.TIMESTAMP_WITH_TIMEZONE)
				.param("endDate", endDate, Types.TIMESTAMP_WITH_TIMEZONE)
				.query(Integer.class)
				.single();
		
		Integer artistCount = jdbc.sql(artists)
				.param("username", username, Types.VARCHAR)
				.param("startDate", startDate, Types.TIMESTAMP_WITH_TIMEZONE)
				.param("endDate", endDate, Types.TIMESTAMP_WITH_TIMEZONE)
				.query(Integer.class)
				.single();
		
		return new StatsForRange(username,
				startDate,
				endDate,
				minutesStreamed,
				statsDTO.stream_count(),
				statsDTO.track_count(),
				statsDTO.album_count(),
				artistCount);
		
	}
	
	
	
	
public FullStats streamStats(String username, Boolean checkForCache) {
		
	
		if(checkForCache) {
			return getCachedStats(username);
		}
		
		String sql = ("SELECT "
				+ "COUNT(SongStreams.*) "
				+ "AS stream_count,"
				+ "COUNT(DISTINCT COALESCE("
				+ "Track_Duplicates.primary_id, Tracks.spotify_track_id)) "
				+ "AS track_count,"
				+ "COUNT(DISTINCT Tracks.album_id) "
				+ "AS album_count "
				+ "FROM SongStreams "
				+ "JOIN Tracks ON SongStreams.spotify_track_id = Tracks.spotify_track_id "
				+ "LEFT JOIN Track_Duplicates ON Tracks.spotify_track_id = Track_Duplicates.secondary_id "
				+ "WHERE username = :username "
				+ "AND SongStreams.ms_played > 30000;");
		
		
		String minutes = ("SELECT "
				+ "COALESCE(SUM(ms_played) / 60000, 0) AS minutes_streamed "
				+ "FROM SongStreams "
				+ "WHERE username = :username;");
		
		String artists = ("SELECT "
				+ "COUNT(DISTINCT Tracks_Artists.artist_id) "
				+ "FROM Tracks_Artists "
				+ "JOIN SongStreams ON "
				+ "SongStreams.spotify_track_id = Tracks_Artists.spotify_track_id "
				+ "WHERE username = :username "
				+ "AND SongStreams.ms_played > 30000;");
		
		
		StatsDTO statsDTO =  jdbc.sql(sql)
				.param("username", username, Types.VARCHAR)
				.query(StatsDTO.class)
				.single();
		
		Integer minutesStreamed = jdbc.sql(minutes)
				.param("username", username, Types.VARCHAR)
				.query(Integer.class)
				.single();
		
		Integer artistCount = jdbc.sql(artists)
				.param("username", username, Types.VARCHAR)
				.query(Integer.class)
				.single();
		
		return new FullStats(username,
				minutesStreamed,
				statsDTO.stream_count(),
				statsDTO.track_count(),
				statsDTO.album_count(),
				artistCount);
		
	}
	
	
	
	public FullStats getCachedStats(String username) {

		return jdbc.sql("SELECT * FROM Stats_Cache_Full "
				+ "WHERE username = :username")
				.param("username", username, Types.VARCHAR)
				.query(FullStats.class)
				.single();
	}
	
	public Optional<StatsForRange> getCachedStats(OffsetDateTime startDate, OffsetDateTime endDate, String username) {
		String sql = ("SELECT * FROM Stats_Cache_Range "
				+ "WHERE username = :username "
				+ "AND start_date = :startDate "
				+ "AND end_date = :endDate "
				+ "LIMIT 1;");
		
		return jdbc.sql(sql)
				.param("username", username, Types.VARCHAR)
				.param("startDate", startDate, Types.TIMESTAMP_WITH_TIMEZONE)
				.param("endDate", endDate, Types.TIMESTAMP_WITH_TIMEZONE)
				.query(StatsForRange.class)
				.optional();
	}
	
	
	public int addCachedStats(String username
			) throws IllegalAccessException, InvocationTargetException {
			
		FullStats stats = streamStats(username, false);

		List<RecordCompInfo> recordComps = CompInfo.get(stats);
		
		String sql = CompListToSql.updateFullCache(recordComps);
		
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
					"Cached lifetime stats contain invalid values.");
			return 0;
		}
		return added;
	}

	
	public int addCachedStats(OffsetDateTime startDate, OffsetDateTime endDate, String username
			) throws IllegalAccessException, InvocationTargetException {
			
		StatsForRange stats = streamStats(startDate, endDate, false, username);

		List<RecordCompInfo> recordComps = CompInfo.get(stats);
		
		String sql = CompListToSql.insertRangeCache(recordComps);
		
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
					"Cached stats for user "
					+ username
					+ " in range "
					+ startDate
					+ " : "
					+ endDate 
					+ " contain invalid values.");
			return 0;
		}
		
		return added;
	}
	
	
	public Optional<Instant> getFirstStreamInstant(String username) {
		return jdbc.sql("SELECT MIN(ts) FROM SongStreams "
				+ "WHERE username = :username")
				.param("username", username, Types.VARCHAR)
				.query(Instant.class)
				.optional();
	}
}
