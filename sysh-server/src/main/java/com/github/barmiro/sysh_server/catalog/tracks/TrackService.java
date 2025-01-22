package com.github.barmiro.sysh_server.catalog.tracks;

import java.lang.reflect.InvocationTargetException;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

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
	
	
	private String checkForDuplicates(Track track) {
		String getDupe = ("SELECT spotify_track_id "
				+ "FROM Tracks "
				+ "WHERE duration_ms = :duration_ms "
				+ "AND name = :name "
				+ "LIMIT 1");
	
		
		Optional<String> trackOptional = jdbc.sql(getDupe)
				.param("duration_ms", track.duration_ms(), Types.INTEGER)
				.param("name", track.name(), Types.VARCHAR)
				.query(String.class)
				.optional();
		
		if (trackOptional.isEmpty()) {
			return null;
		}

		String existingID = trackOptional.get();
		
		String primaryID;
		
		 Optional<String> primaryOptional = jdbc.sql(
				"SELECT primary_id "
				+ "FROM Track_Duplicates "
				+ "WHERE secondary_id = :existingID "
				+ "LIMIT 1")
				 .param("existingID", existingID, Types.VARCHAR)
				.query(String.class)
				.optional();
		
		 if (primaryOptional.isEmpty()) {
			 primaryID = existingID;
		 } else {
			 primaryID = primaryOptional.get();
		 }
		 
		 String makeJoin = ("INSERT INTO Track_Duplicates("
				 + "primary_id,"
				 + "secondary_id"
				 + ") VALUES ("
				 + "'" + primaryID + "',"
				 + "'" + track.spotify_track_id() + "')");
		 return makeJoin;
		 
	}

	public Integer addNewTrack(Track track
			) throws IllegalAccessException, InvocationTargetException {
		
		String duplicate = checkForDuplicates(track);
		
		
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
		
		if (duplicate != null) {
			Integer rows = jdbc.sql(duplicate).update();
			System.out.println("Duplicate found for "
					+ track.name()
					+ ". "
					+ rows
					+ " rows affected.");
		}
		
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
