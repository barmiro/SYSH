package com.github.barmiro.sysh_server.catalog.streams;

import java.sql.Types;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import com.github.barmiro.sysh_server.common.records.TimestampRange;
import com.github.barmiro.sysh_server.common.utils.GetTimestampRange;

@Repository
public class StreamRepository {
	
	private final JdbcClient jdbc;
	StreamRepository(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}
	
	private static final Logger log = LoggerFactory.getLogger(StreamRepository.class);
	
	public List<SongStream> findAll() {
		return jdbc.sql("SELECT * FROM SongStreams "
				+ "ORDER BY ts DESC")
				.query(SongStream.class)
				.list();
	}
	
	public List<SongStream> find(int limit, String username) {
		return jdbc.sql("SELECT * FROM SongStreams "
				+ "WHERE username = :username "
				+ "ORDER BY ts DESC LIMIT :limit")
				.param("username", username, Types.VARCHAR)
				.param("limit", limit, Types.INTEGER)
				.query(SongStream.class)
				.list();
	}
	
	int wipe() {
		return this.jdbc.sql("DELETE FROM SongStreams").update();
	}
	
	
	
//	There are some possible edge cases where this could lead to tracks with no streams
//	but in normal usage only streams from the recent endpoint should get deleted and replaced with json data
	public int wipeStreamsInRange(TimestampRange timestampRange, String username) {
		
		String sql = ("DELETE FROM SongStreams "
				+ "WHERE username = :username "
				+ "AND ts BETWEEN :startTimestamp AND :endTimestamp");
		
		return jdbc.sql(sql)
				.param("username", username, Types.VARCHAR)
				.param("startTimestamp", timestampRange.startTimestamp(), Types.TIMESTAMP)
				.param("endTimestamp", timestampRange.endTimestamp(), Types.TIMESTAMP)
				.update();
	}

	
	public int addNew(SongStream stream) {
		String sql = ("INSERT INTO SongStreams("
				+ "ts,"
				+ "username,"
				+ "ms_played,"
				+ "spotify_track_id"
				+ ") VALUES ("
				+ ":ts,"
				+ ":username,"
				+ ":ms_played,"
				+ ":spotify_track_id) "
				//this filters out duplicates, and accurately adds quick double-plays
				//(ignoring the first, extremely-short-but-technically-there plays)
				//in practice, it's only ~0.1% of streaming time, but might matter for a song's stats
				+ "ON CONFLICT (ts, spotify_track_id, username) DO "
				+ "UPDATE SET ms_played = EXCLUDED.ms_played "
				+ "WHERE SongStreams.ms_played < EXCLUDED.ms_played");
		
		return this.jdbc.sql(sql)
				.param("ts", stream.ts(), Types.TIMESTAMP)
				.param("username", stream.username(), Types.VARCHAR)
				.param("ms_played", stream.ms_played(), Types.INTEGER)
				.param("spotify_track_id", stream.spotify_track_id(), Types.VARCHAR)
				.update();
	}
	
	public int addAll(List<SongStream> streams) {
		
		if(streams.isEmpty()) {
			log.info("No new streams found.");
			return 0;
		}
		TimestampRange timestampRange = GetTimestampRange.fromSongStreamList(streams);
		
		log.info("Found " + streams.size() + " streams "
				+ "in time period from "
				+ timestampRange.startTimestamp()
				+ " to "
				+ timestampRange.endTimestamp());
		
		String username = streams.getFirst().username();
		
		int deletedStreams = wipeStreamsInRange(
				timestampRange,
				username);
		
//		This should always be 0 when adding recent streams, except sometimes right after importing json data
//		Watching the logs from this might tell us more about why stats.fm has some duplicates slipping through
		log.info("Wiped " + deletedStreams + " streams to prevent duplicates.");
		
		
		int added = 0;
		for (SongStream stream:streams) {
			added += addNew(stream);
		}
		log.info("Added " + added + " new streams.");
		return added;
	}
	
	@Async
	public Future<Integer> addAllAsync(List<SongStream> streams) {
		log.info("Found " + streams.size() + " streams.");
		int added = 0;
		for (SongStream stream:streams) {
			added += addNew(stream);
		}
		
		CompletableFuture<Integer> result = new CompletableFuture<>();
		result.complete(added);
		log.info("Added " + added + " streams.");
		return result;
	}
}
