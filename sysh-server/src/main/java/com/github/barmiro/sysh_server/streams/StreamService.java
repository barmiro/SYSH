package com.github.barmiro.sysh_server.streams;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

@Service
public class StreamService {
	
	private final JdbcClient jdbcClient;
	
	StreamService(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}
	
	public List<Stream> findAll() {
		return jdbcClient.sql("SELECT * FROM Streams")
				.query(Stream.class)
				.list();
	}
	
	public List<Track> allTracks() {
		return jdbcClient.sql("SELECT * FROM Tracks")
				.query(Track.class)
				.list();
	}
	
	Integer wipe() {
		return this.jdbcClient.sql("DELETE FROM Streams").update();
	}

	List<Track> topTracks() {
		return jdbcClient.sql("SELECT * FROM Tracks ORDER BY stream_number DESC")
				.query(Track.class)
				.list();
	}
	
	Integer addTracks(Stream stream) {
		String newTrack = ("INSERT INTO Tracks("
				+ "spotify_track_uri,"
				+ "master_metadata_track_name,"
				+ "master_metadata_album_artist_name,"
				+ "master_metadata_album_album_name,"
				+ "stream_number,"
				+ "total_ms_played,"
				+ "first_played"
				+ ") VALUES ("
				+ ":spotify_track_uri,"
				+ ":master_metadata_track_name,"
				+ ":master_metadata_album_artist_name,"
				+ ":master_metadata_album_album_name,"
				+ "1,"
				+ ":ms_played,"
				+ ":ts)"
				+ "ON CONFLICT(spotify_track_uri)"
				+ "DO UPDATE SET ("
				+ "stream_number,"
				+ "total_ms_played"
				+ ") = ("
				+ "Tracks.stream_number + 1,"
				+ "Tracks.total_ms_played + :ms_played);");

		return jdbcClient.sql(newTrack)
				.param("ts", stream.ts(), Types.TIMESTAMP)
				.param("ms_played", stream.ms_played(), Types.INTEGER)
				.param("master_metadata_track_name", stream.master_metadata_track_name(), Types.VARCHAR)
				.param("master_metadata_album_artist_name", stream.master_metadata_album_artist_name(), Types.VARCHAR)
				.param("master_metadata_album_album_name", stream.master_metadata_album_album_name(), Types.VARCHAR)
				.param("spotify_track_uri", stream.spotify_track_uri(), Types.VARCHAR)
				.update();
		
//		String newTrack = ("INSERT INTO Tracks("
//				+ "spotify_track_uri,"
//				+ "master_metadata_track_name,"
//				+ "master_metadata_album_artist_name,"
//				+ "master_metadata_album_album_name,"
//				+ "stream_number,"
//				+ "total_ms_played,"
//				+ "first_played"
//				+ ") SELECT "
//				+ "spotify_track_uri,"
//				+ "master_metadata_track_name,"
//				+ "master_metadata_album_artist_name,"
//				+ "master_metadata_album_album_name,"
//				+ "1,"
//				+ "ms_played,"
//				+ "ts "
//				+ "FROM Streams "
//				+ "ON CONFLICT (spotify_track_uri) "
//				+ "DO UPDATE SET "
//				+ "(stream_number,"
//				+ "total_ms_played"
//				+ ") = ("
//				+ "Tracks.stream_number + 1,"
//				+ "Tracks.total_ms_played + EXCLUDED.total_ms_played);");
//		return jdbcClient.sql(newTrack).update();
		
		}
	
	Integer addNew(Stream stream) {
		String sql = ("INSERT INTO Streams("
				+ "ts,"
				+ "ms_played,"
				+ "master_metadata_track_name,"
				+ "master_metadata_album_artist_name,"
				+ "master_metadata_album_album_name,"
				+ "spotify_track_uri"
				+ ") VALUES ("
				+ ":ts,"
				+ ":ms_played,"
				+ ":master_metadata_track_name,"
				+ ":master_metadata_album_artist_name,"
				+ ":master_metadata_album_album_name,"
				+ ":spotify_track_uri)");
		
		return this.jdbcClient.sql(sql)
				.param("ts", stream.ts(), Types.TIMESTAMP)
				.param("ms_played", stream.ms_played(), Types.INTEGER)
				.param("master_metadata_track_name", stream.master_metadata_track_name(), Types.VARCHAR)
				.param("master_metadata_album_artist_name", stream.master_metadata_album_artist_name(), Types.VARCHAR)
				.param("master_metadata_album_album_name", stream.master_metadata_album_album_name(), Types.VARCHAR)
				.param("spotify_track_uri", stream.spotify_track_uri(), Types.VARCHAR)
				.update();
	}
}
