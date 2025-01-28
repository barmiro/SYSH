package com.github.barmiro.sysh_server.catalog.tracks;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.github.barmiro.sysh_server.catalog.interfaces.CatalogRepository;



@Repository
public class TrackRepository extends CatalogRepository<Track> {

	TrackRepository(JdbcClient jdbc) {
		super(jdbc);
	}

	private static final Logger log = LoggerFactory.getLogger(TrackRepository.class);
	
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


	
	public int addTracks(List<Track> tracks) {
		int added = 0;
		int duplicates = 0;
		for (Track track:tracks) {
			
			String duplicate = checkForDuplicates(track);
			
			try {
				added += addNew(track, Track.class);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
			
			if (duplicate != null) {
				try {
					duplicates += jdbc.sql(duplicate).update();					
				} catch (DuplicateKeyException e) {
					log.error(e.getMessage());
				}
			}
		}
		
		log.info("Added " + added + " new tracks.");
		
		if (duplicates > 0) {
			log.info(duplicates + " tracks appear on multiple albums.");			
		}
		
		return added;
	}
	
	public List<Track> getDuplicatesFor(Track track) {
		List<Track> duplicates = new ArrayList<>();
		
		String sql = ("SELECT * FROM Tracks "
				+ "WHERE spotify_track_id IN ("
				+ "SELECT secondary_id FROM Track_Duplicates "
				+ "WHERE primary_id = :trackID) "
				+ "OR spotify_track_id IN ("
				+ "SELECT primary_id FROM Track_Duplicates "
				+ "WHERE secondary_id = :trackID)");
		
		duplicates.addAll(jdbc.sql(sql)
				.param("trackID", track.spotify_track_id(), Types.VARCHAR)
				.query(Track.class)
				.list());
		
		
		return duplicates;
	}
	
	
	
	
	List<TrackStats> topTracksCount(Timestamp startDate, Timestamp endDate) {
		

		
		String sql = ("SELECT Tracks.*, COUNT(Streams.spotify_track_id) AS sort_param "
				+ "FROM Tracks "
				+ "LEFT JOIN Streams ON Tracks.spotify_track_id = Streams.spotify_track_id "
				+ "WHERE Streams.ts BETWEEN :startDate AND :endDate "
				+ "AND Streams.ms_played >= 30000 "
				+ "GROUP BY "
				+ "Tracks.spotify_track_id,"
				+ "Tracks.name "
				+ "ORDER BY sort_param DESC;");
		return jdbc.sql(sql)
				.param("startDate", startDate, Types.TIMESTAMP)
				.param("endDate", endDate, Types.TIMESTAMP)
				.query(TrackStats.class)
				.list();
		
	}
	
	List<TrackStats> topTracksTime(Timestamp startDate, Timestamp endDate) {
		
		String sql = ("SELECT Tracks.*, SUM(Streams.ms_played) / 60000 AS sort_param "
				+ "FROM Tracks "
				+ "LEFT JOIN Streams ON Tracks.spotify_track_id = Streams.spotify_track_id "
				+ "WHERE Streams.ts BETWEEN :startDate AND :endDate "
				+ "GROUP BY "
				+ "Tracks.spotify_track_id,"
				+ "Tracks.name "
				+ "ORDER BY sort_param DESC;");
		return jdbc.sql(sql)
				.param("startDate", startDate, Types.TIMESTAMP)
				.param("endDate", endDate, Types.TIMESTAMP)
				.query(TrackStats.class)
				.list();
		
	}
}
