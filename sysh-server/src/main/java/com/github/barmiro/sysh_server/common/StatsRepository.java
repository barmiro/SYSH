package com.github.barmiro.sysh_server.common;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.github.barmiro.sysh_server.common.records.StatsDTO;

@Repository
public class StatsRepository {

	JdbcClient jdbc;
	
	StatsRepository(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}
	
	public StatsDTO streamStats() {
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
				+ "AS album_count, "
				+ "COUNT(DISTINCT CASE WHEN "
				+ "Tracks_Artists.spotify_track_id = Tracks.spotify_track_id THEN Tracks_Artists.artist_id END) "
				+ "AS artist_count "
				+ "FROM Streams "
				+ "JOIN Tracks ON Streams.spotify_track_id = Tracks.spotify_track_id "
				+ "JOIN Tracks_Artists ON Tracks.spotify_track_id = Tracks_Artists.spotify_track_id;");
		
		
		return jdbc.sql(sql)
				.query(StatsDTO.class)
				.single();
	}
}
