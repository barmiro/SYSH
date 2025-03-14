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
				 + "'" + track.spotify_track_id() + "')"
				 		+ " ON CONFLICT (primary_id, secondary_id) DO NOTHING");
		 return makeJoin;
		 
	}

	
	public int addTracks(List<Track> tracks) throws IllegalAccessException, InvocationTargetException {
		int added = 0;
		int duplicates = 0;
		for (Track track:tracks) {
			
			String duplicate = checkForDuplicates(track);
			
			added += addNew(track, Track.class);

			
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
		
//		TODO: call this somewhere else, this method should be user-agnostic
//		try {
//			updateTopTracksCache(username);
//		} catch (IllegalAccessException | InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
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
	
	
	List<TrackStats> topTracks(String sort,
			Timestamp startDate,
			Timestamp endDate,
			String username) {
		
		String sql = ("SELECT t2.spotify_track_id, t2.name,"
				+ ":username AS username,"
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
				+ "WHERE SongStreams.username = :username "
				+ "AND SongStreams.ts BETWEEN :startDate AND :endDate "
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
				.param("username", username, Types.VARCHAR)
				.param("startDate", startDate, Types.TIMESTAMP)
				.param("endDate", endDate, Types.TIMESTAMP)
				.query(TrackStats.class)
				.list();
	}
	
	List<TrackStats> topTracks(String sort,
			String username,
			Boolean checkForCache) {
		
		String sql;
		
		if(checkForCache) {
			sql = ("SELECT spotify_track_id,"
					+ "name,"
					+ "album_name,"
					+ "thumbnail_url,"
					+ "primary_artist_name,"
					+ "username,"
					+ "COALESCE(stream_count, 0) AS stream_count, "
					+ "COALESCE(total_ms_played, 0) AS total_ms_played "
					+ "FROM Top_Tracks_Cache "
					+ "WHERE username = :username "
					+ "ORDER BY "
					+ sort
					+ " DESC;");
		} else {
			sql = ("SELECT t2.spotify_track_id, t2.name,"
					+ ":username AS username,"
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
					+ "WHERE SongStreams.username = :username "
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
		}
		
		List<TrackStats> rawList = jdbc.sql(sql)
				.param("username", username, Types.VARCHAR)
				.query(TrackStats.class)
				.list();
		
		if (checkForCache && rawList.isEmpty()) {
			System.out.println("empty result");
			updateTopTracksCache(username);

		}
		
		return jdbc.sql(sql)
				.param("username", username, Types.VARCHAR)
				.query(TrackStats.class)
				.list();
	}
	
	public int updateTopTracksCache(String username
			) {
//		Doesn't have to be sorted, but I don't feel like overloading the constructor again
		List<TrackStats> trackStatsList = topTracks("stream_count", username, false);
		
		
		String wipeCache = ("DELETE FROM Top_Tracks_Cache WHERE username = :username");
		
		int deletedRows = jdbc.sql(wipeCache)
				.param("username", username, Types.VARCHAR)
				.update();
		
		int rowsAdded = 0;
		
		for (TrackStats track:trackStatsList) {
			List<RecordCompInfo> recordComps;
			try {
				recordComps = CompInfo.get(track);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
				return 0;
			}
			
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
