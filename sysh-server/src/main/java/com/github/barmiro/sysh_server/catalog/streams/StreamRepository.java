package com.github.barmiro.sysh_server.catalog.streams;

import java.sql.Types;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

@Repository
public class StreamRepository {
	
	private final JdbcClient jdbc;
	
	StreamRepository(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}
	
	
	public List<Stream> findAll() {
		return jdbc.sql("SELECT * FROM Streams"
				+ "ORDER BY ts DESC")
				.query(Stream.class)
				.list();
	}
	
	public List<Stream> find(int limit) {
		return jdbc.sql("SELECT * FROM Streams LIMIT :limit")
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
				+ ":spotify_track_id)");
		
		return this.jdbc.sql(sql)
				.param("ts", stream.ts(), Types.TIMESTAMP)
				.param("ms_played", stream.ms_played(), Types.INTEGER)
				.param("spotify_track_id", stream.spotify_track_id(), Types.VARCHAR)
				.update();
	}
	
	public int addAll(List<Stream> streams) {
		int added = 0;
		for (Stream stream:streams) {
			added += addNew(stream);
		}
		
		return added;
	}
	
	@Async
	public Future<Integer> addAllAsync(List<Stream> streams) {
		System.out.println("Found " + streams.size() + " streams.");
		int added = 0;
		for (Stream stream:streams) {
			added += addNew(stream);
		}
		
		CompletableFuture<Integer> result = new CompletableFuture<>();
		result.complete(added);
		System.out.println("Added " + added + " streams.");
		return result;
	}
}
