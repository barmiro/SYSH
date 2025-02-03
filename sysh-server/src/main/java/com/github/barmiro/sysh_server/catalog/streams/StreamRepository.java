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

@Repository
public class StreamRepository {
	
	private final JdbcClient jdbc;
	StreamRepository(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}
	
	private static final Logger log = LoggerFactory.getLogger(StreamRepository.class);
	
	public List<Stream> findAll() {
		return jdbc.sql("SELECT * FROM Streams "
				+ "ORDER BY ts DESC")
				.query(Stream.class)
				.list();
	}
	
	public List<Stream> find(int limit) {
		return jdbc.sql("SELECT * FROM Streams ORDER BY ts DESC LIMIT :limit")
				.param("limit", limit, Types.INTEGER)
				.query(Stream.class)
				.list();
	}
	
	int wipe() {
		return this.jdbc.sql("DELETE FROM Streams").update();
	}

	
	public int addNew(Stream stream) {
		String sql = ("INSERT INTO Streams("
				+ "ts,"
				+ "ms_played,"
				+ "spotify_track_id"
				+ ") VALUES ("
				+ ":ts,"
				+ ":ms_played,"
				+ ":spotify_track_id) "
				//this filters out duplicates, and accurately adds quick double-plays
				//(ignoring the first, extremely-short-but-technically-there plays)
				//in practice, it's only ~0.1% of streaming time, but might matter for a song's stats
				+ "ON CONFLICT (ts, spotify_track_id) DO "
				+ "UPDATE SET ms_played = EXCLUDED.ms_played "
				+ "WHERE Streams.ms_played < EXCLUDED.ms_played");
		
		return this.jdbc.sql(sql)
				.param("ts", stream.ts(), Types.TIMESTAMP)
				.param("ms_played", stream.ms_played(), Types.INTEGER)
				.param("spotify_track_id", stream.spotify_track_id(), Types.VARCHAR)
				.update();
	}
	
	public int addAll(List<Stream> streams) {
		log.info("Found " + streams.size() + " streams.");
		int added = 0;
		for (Stream stream:streams) {
			added += addNew(stream);
		}
		log.info("Added " + added + " new streams.");
		return added;
	}
	
	@Async
	public Future<Integer> addAllAsync(List<Stream> streams) {
		log.info("Found " + streams.size() + " streams.");
		int added = 0;
		for (Stream stream:streams) {
			added += addNew(stream);
		}
		
		CompletableFuture<Integer> result = new CompletableFuture<>();
		result.complete(added);
		log.info("Added " + added + " streams.");
		return result;
	}
}
