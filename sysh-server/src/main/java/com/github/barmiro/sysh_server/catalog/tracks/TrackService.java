package com.github.barmiro.sysh_server.catalog.tracks;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import org.springframework.stereotype.Service;

import com.github.barmiro.sysh_server.catalog.CatalogService;
import com.github.barmiro.sysh_server.common.records.RecordCompInfo;
import com.github.barmiro.sysh_server.common.utils.CompInfo;
import com.github.barmiro.sysh_server.common.utils.CompListToSql;

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
	

	public Integer addNewTrack(Track track
			) throws IllegalAccessException, InvocationTargetException {
		
		List<RecordCompInfo> recordComps = CompInfo.get(track);
		
		String sql = CompListToSql.insert(recordComps, Track.class);
		StatementSpec jdbcCall = jdbc.sql(sql);
		
		for (RecordCompInfo comp:recordComps) {
			jdbcCall = jdbcCall.param(
					comp.compName(),
					comp.compValue(),
					comp.sqlType());
		}
		
		Integer added = 0;
		added = jdbcCall.update();
		
		return added;
	}
	
	
	public Integer addTracks(List<Track> tracks) {
		Integer added = 0;
		for (Track track:tracks) {
			try {
				added += addNewTrack(track);
			} catch (IllegalAccessException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Added " + added + " new tracks");
		return added;
	}
	
	
	
//	TODO: change
	List<TrackStats> topTracksNew() {
		
		String sql = ("SELECT Tracks.*, COUNT(Streams.spotify_track_id) AS stream_count "
				+ "FROM Tracks "
				+ "LEFT JOIN Streams ON Tracks.spotify_track_id = Streams.spotify_track_id "
				+ "GROUP BY "
				+ "Tracks.spotify_track_id,"
				+ "Tracks.name "
				+ "ORDER BY stream_count DESC;");
		return jdbc.sql(sql)
				.query(TrackStats.class)
				.list();
		
	}
}
