package com.github.barmiro.sysh_server.catalog.jointables;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.github.barmiro.sysh_server.catalog.tracks.Track;

@Repository
public class AlbumsTracks {
	
	private final JdbcClient jdbc;
	
	AlbumsTracks(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}
	
	public void join(List<Track> tracks) {
		System.out.println("Updating the Albums_Tracks join table...");
		String sql = ("INSERT INTO Albums_Tracks ("
				+ "album_id,"
				+ "spotify_track_id,"
				+ "disc_number,"
				+ "track_number"
				+ ") VALUES ("
				+ ":album_id,"
				+ ":spotify_track_id,"
				+ ":disc_number,"
				+ ":track_number)");
		
		int added = 0;
		for (Track track:tracks) {
			added += jdbc.sql(sql)
				.param("album_id", track.album_id(), Types.VARCHAR)
				.param("spotify_track_id", track.spotify_track_id(), Types.VARCHAR)
				.param("disc_number", track.disc_number(), Types.INTEGER)
				.param("track_number", track.track_number(), Types.INTEGER)
				.update();	
		}
		System.out.println("Albums_Tracks join table updated, new entries: " + added);
	}
}
