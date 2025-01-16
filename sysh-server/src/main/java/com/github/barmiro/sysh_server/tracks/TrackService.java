package com.github.barmiro.sysh_server.tracks;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import com.github.barmiro.sysh_server.streams.Stream;

@Service
public class TrackService {

	private final JdbcClient jdbcClient;
	
	TrackService(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}
	
	public List<Track> allTracks() {
		return jdbcClient.sql("SELECT * FROM Tracks")
				.query(Track.class)
				.list();
	}
	
	List<Track> topTracks() {
		return jdbcClient.sql("SELECT * FROM Tracks ORDER BY stream_number DESC")
				.query(Track.class)
				.list();
	}
	
	List<Track> topTracksNew() {
		
		String sql = ("SELECT Tracks.*, COUNT(Streams.spotify_track_uri) AS stream_count "
				+ "FROM Tracks "
				+ "LEFT JOIN Streams ON Tracks.spotify_track_uri = Streams.spotify_track_uri "
				+ "GROUP BY "
				+ "Tracks.spotify_track_uri,"
				+ "Tracks.master_metadata_track_name,"
				+ "Tracks.master_metadata_album_artist_name,"
				+ "Tracks.master_metadata_album_album_name,"
				+ "Tracks.stream_number,"
				+ "Tracks.total_ms_played,"
				+ "Tracks.first_played "
				+ "ORDER BY stream_count DESC;");
		return jdbcClient.sql(sql)
				.query(Track.class)
				.list();

	}
	
	public Integer addTracks(Stream stream) {
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
		
		}
}
