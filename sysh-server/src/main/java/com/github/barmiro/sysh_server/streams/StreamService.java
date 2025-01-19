package com.github.barmiro.sysh_server.streams;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

@Service
public class StreamService {
	
	private final JdbcClient jdbc;
	
	StreamService(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}
	
	
	public List<Stream> findAll() {
		return jdbc.sql("SELECT * FROM Streams")
				.query(Stream.class)
				.list();
	}
	
	public List<Stream> find(Integer limit) {
		return jdbc.sql("SELECT * FROM Streams LIMIT :limit")
				.param("limit", limit, Types.INTEGER)
				.query(Stream.class)
				.list();
	}
	
	Integer wipe() {
		return this.jdbc.sql("DELETE FROM Streams").update();
	}

	
	public Integer addNew(Stream stream) {
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
}
