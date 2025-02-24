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
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import org.springframework.stereotype.Repository;

import com.github.barmiro.sysh_server.catalog.interfaces.CatalogRepository;
import com.github.barmiro.sysh_server.common.records.RecordCompInfo;
import com.github.barmiro.sysh_server.common.utils.CompInfo;
import com.github.barmiro.sysh_server.common.utils.CompListToSql;



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
		try {
			updateTopTracksCache();
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	
	List<TrackStats> topTracks(String sort, Timestamp startDate, Timestamp endDate) {
		
		String sql = ("SELECT t2.spotify_track_id, t2.name,"
				+ "COUNT("
				+ "CASE WHEN SongStreams.ms_played >= 30000 THEN SongStreams.spotify_track_id END"
				+ ") AS stream_count,"
				+ "COALESCE(SUM(SongStreams.ms_played), 0) AS total_ms_played,"
				+ "Albums.name AS album_name,"
				+ "Albums.thumbnail_url,"
				+ "Artists.name AS primary_artist_name "
				+ "FROM Tracks t "
				+ "LEFT JOIN Track_Duplicates ON Track_Duplicates.secondary_id = t.spotify_track_id "
				+ "LEFT JOIN Tracks t2 ON t2.spotify_track_id = COALESCE(Track_Duplicates.primary_id, t.spotify_track_id) "
				+ "LEFT JOIN SongStreams ON t.spotify_track_id = SongStreams.spotify_track_id "
				+ "LEFT JOIN Albums ON t2.album_id = Albums.id "
				+ "LEFT JOIN Tracks_Artists ON t2.spotify_track_id = Tracks_Artists.spotify_track_id "
				+ "LEFT JOIN Artists ON Tracks_Artists.artist_id = Artists.id "
				+ "WHERE SongStreams.ts BETWEEN :startDate AND :endDate "
				+ "AND Tracks_Artists.artist_order = 0 "
				+ "GROUP BY "
				+ "t2.spotify_track_id,"
				+ "t2.name,"
				+ "album_name,"
				+ "Albums.thumbnail_url,"
				+ "primary_artist_name "
				+ "ORDER BY "
				+ sort
				+ " DESC;");
		return jdbc.sql(sql)
				.param("startDate", startDate, Types.TIMESTAMP)
				.param("endDate", endDate, Types.TIMESTAMP)
				.query(TrackStats.class)
				.list();
	}
	
	List<TrackStats> topTracks(String sort, Boolean checkForCache) {
		
		String sql;
		
		if(checkForCache) {
			sql = ("SELECT spotify_track_id,"
					+ "name,"
					+ "album_name,"
					+ "thumbnail_url,"
					+ "primary_artist_name,"
					+ "COALESCE(stream_count, 0) AS stream_count, "
					+ "COALESCE(total_ms_played, 0) AS total_ms_played "
					+ "FROM Top_Tracks_Cache "
					+ "ORDER BY "
					+ sort
					+ " DESC;");
		} else {
			sql = ("SELECT t2.spotify_track_id, t2.name,"
					+ "COUNT("
					+ "CASE WHEN SongStreams.ms_played >= 30000 THEN SongStreams.spotify_track_id END"
					+ ") AS stream_count,"
					+ "SUM(SongStreams.ms_played) AS total_ms_played,"
					+ "Albums.name AS album_name,"
					+ "Albums.thumbnail_url,"
					+ "Artists.name AS primary_artist_name "
					+ "FROM Tracks t "
					+ "LEFT JOIN Track_Duplicates ON Track_Duplicates.secondary_id = t.spotify_track_id "
					+ "LEFT JOIN Tracks t2 ON t2.spotify_track_id = COALESCE(Track_Duplicates.primary_id, t.spotify_track_id) "
					+ "LEFT JOIN SongStreams ON t.spotify_track_id = SongStreams.spotify_track_id "
					+ "LEFT JOIN Albums ON t2.album_id = Albums.id "
					+ "LEFT JOIN Tracks_Artists ON t2.spotify_track_id = Tracks_Artists.spotify_track_id "
					+ "LEFT JOIN Artists ON Tracks_Artists.artist_id = Artists.id "
					+ "WHERE Tracks_Artists.artist_order = 0 "
					+ "GROUP BY "
					+ "t2.spotify_track_id,"
					+ "t2.name,"
					+ "album_name,"
					+ "Albums.thumbnail_url,"
					+ "primary_artist_name "
					+ "ORDER BY "
					+ sort
					+ " DESC;");
		}
		
		return jdbc.sql(sql)
				.query(TrackStats.class)
				.list();
	}
	
	int updateTopTracksCache(
			) throws IllegalAccessException, InvocationTargetException {
//		Doesn't have to be sorted, but I don't feel like overloading the constructor again
		List<TrackStats> trackStatsList = topTracks("stream_count", false);
		
		
		String wipeCache = ("DELETE FROM Top_Tracks_Cache;");
		
		int deletedRows = jdbc.sql(wipeCache).update();
		
		int rowsAdded = 0;
		
		for (TrackStats track:trackStatsList) {
			List<RecordCompInfo> recordComps = CompInfo.get(track);
			
			String addTrackStats = CompListToSql.insertTopItemsCache(recordComps, "Track");
			StatementSpec jdbcCall = jdbc.sql(addTrackStats);
			
			for (RecordCompInfo comp:recordComps) {
				jdbcCall = jdbcCall.param(
						comp.compName(),
						comp.compValue(),
						comp.sqlType());
			}
			rowsAdded += jdbcCall.update();
		}
		
		log.info("Deleted " + deletedRows + " rows from top tracks cache");
		log.info("Added " + rowsAdded + " rows to top tracks cache");
		return rowsAdded;
	}
	
}
