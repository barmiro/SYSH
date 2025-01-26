package com.github.barmiro.sysh_server.catalog.jointables;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.ApiTrack;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.artists.ApiTrackArtist;

@Repository
public class TracksArtists {
	
	private final JdbcClient jdbc;
	
	TracksArtists(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}
	
	public void join(List<ApiTrack> apiTracks) {
		System.out.println("Updating the Tracks_Artists join table...");
		String sql = ("INSERT INTO Tracks_Artists ("
				+ "spotify_track_id,"
				+ "artist_id,"
				+ "artist_order"
				+ ") VALUES ("
				+ ":spotify_track_id,"
				+ ":artist_id,"
				+ ":artist_order)");
		
		int added = 0;
		for (ApiTrack apiTrack:apiTracks) {
			List<ApiTrackArtist> artists = apiTrack.artists();
			
			
			for(int i = 0; i < artists.size(); i++) {
				
				added += jdbc.sql(sql)
						.param("spotify_track_id", apiTrack.id(), Types.VARCHAR)
						.param("artist_id", artists.get(i).id(), Types.VARCHAR)
						.param("artist_order", i, Types.INTEGER)
						.update();
				
			}
		}
		
		System.out.println("Tracks_Artists join table updated, new entries: " + added);
	}
}
