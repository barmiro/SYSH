package com.github.barmiro.sysh_server.catalog.tracks;

import java.sql.Types;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import com.github.barmiro.sysh_server.catalog.CatalogService;

@Service
public class TrackService implements CatalogService {

	private final JdbcClient jdbc;
	
	TrackService(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}
	
	public List<Track> allTracks() {
		return jdbc.sql("SELECT * FROM Tracks")
				.query(Track.class)
				.list();
	}

	
	List<TrackStats> topTracksNew() {
		
		String sql = ("SELECT Tracks.*, COUNT(Streams.spotify_track_id) AS stream_count "
				+ "FROM Tracks "
				+ "LEFT JOIN Streams ON Tracks.spotify_track_id = Streams.spotify_track_id "
				+ "GROUP BY "
				+ "Tracks.spotify_track_id,"
				+ "Tracks.name,"
				+ "ORDER BY stream_count DESC;");
		return jdbc.sql(sql)
				.query(TrackStats.class)
				.list();

	}
	

	
	public Integer addNewTrack(Track track) {
		String newTrack = ("INSERT INTO Tracks("
				+ "spotify_track_id,"
				+ "name,"
				+ "duration_ms,"
				+ "album_id"
				+ ") VALUES ("
				+ ":spotify_track_id,"
				+ ":name,"
				+ ":duration_ms,"
				+ ":album_id)");
		
		Integer tracksAdded = 0;
		
		try {
			tracksAdded = jdbc.sql(newTrack)
				.param("spotify_track_id", track.spotify_track_id(), Types.VARCHAR)
				.param("name", track.name(), Types.VARCHAR)
				.param("duration_ms", track.duration_ms(), Types.INTEGER)
				.param("album_id", track.album_id(), Types.VARCHAR)
				.update();
		} catch (DuplicateKeyException e) {
			return 100000;
		}
		
		return tracksAdded;
	}
	
//	public Integer addTrack(Stream stream) {
//		String newTrack = ("INSERT INTO Tracks("
//				+ "spotify_track_id,"
//				+ "stream_number,"
//				+ "total_ms_played,"
//				+ "first_played"
//				+ ") VALUES ("
//				+ ":spotify_track_id,"
//				+ "1,"
//				+ ":ms_played,"
//				+ ":ts)"
//				+ "ON CONFLICT(spotify_track_id)"
//				+ "DO UPDATE SET ("
//				+ "stream_number,"
//				+ "total_ms_played"
//				+ ") = ("
//				+ "Tracks.stream_number + 1,"
//				+ "Tracks.total_ms_played + :ms_played);");
//
//		return jdbc.sql(newTrack)
//				.param("ts", stream.ts(), Types.TIMESTAMP)
//				.param("ms_played", stream.ms_played(), Types.INTEGER)
//				.param("spotify_track_id", stream.spotify_track_id(), Types.VARCHAR)
//				.update();
//		
//		}
}
