package com.github.barmiro.sysh_server.catalog.albums;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import org.springframework.stereotype.Repository;

import com.github.barmiro.sysh_server.catalog.interfaces.CatalogRepository;
import com.github.barmiro.sysh_server.common.records.RecordCompInfo;
import com.github.barmiro.sysh_server.common.utils.CompInfo;
import com.github.barmiro.sysh_server.common.utils.CompListToSql;

@Repository
public class AlbumRepository extends CatalogRepository<Album> {

	AlbumRepository(JdbcClient jdbc) {
		super(jdbc);
	}
	
	private static final Logger log = LoggerFactory.getLogger(AlbumRepository.class);
	protected List<Album> getNewAlbums(List<Album> albums) {
		
		List<Album> newAlbums = new ArrayList<>();
		
		for(Album album:albums) {
			
			int exists = jdbc.sql("SELECT * FROM Albums "
					+ "WHERE id = :albumID "
					+ "LIMIT 1")
					.param("albumID", album.id(), Types.VARCHAR)
					.query(Album.class)
					.list()
					.size();
			
			if (exists == 0 && !newAlbums.contains(album)) {
				newAlbums.add(album);
			}
		}
		return newAlbums;
	}

	public int addAlbums(List<Album> albums) {
		int added = 0;
		for (Album album:getNewAlbums(albums)) {
			try {
				added += addNew(album, Album.class);
			} catch (IllegalAccessException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		log.info("Added " + added + " new albums");
		
//		TODO: call this somewhere else, this method should be user-agnostic
//		try {
//			updateTopAlbumsCache(username);
//		} catch (IllegalAccessException | InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		return added;
	}
	
	List<AlbumStats> topAlbums(String sort,
			Timestamp startDate,
			Timestamp endDate,
			String username) {
		
		String sql = ("SELECT Albums.*,"
				+ ":username AS username,"
				+ "COUNT("
				+ "CASE WHEN SongStreams.ms_played >= 30000 THEN SongStreams.spotify_track_id END"
				+ ") AS stream_count,"
				+ "COALESCE(SUM(SongStreams.ms_played), 0) AS total_ms_played,"
				+ "(SELECT Artists.name "
				+ "FROM Artists "
				+ "JOIN Tracks_Artists ON Tracks_Artists.artist_id = Artists.id "
				+ "WHERE Tracks_Artists.spotify_track_id = Albums_Tracks.spotify_track_id "
				+ "AND Tracks_Artists.artist_order = 0 "
				+ "LIMIT 1) AS primary_artist_name "
				+ "FROM Albums "
				+ "LEFT JOIN Albums_Tracks ON Albums.id = Albums_Tracks.album_id "
				+ "LEFT JOIN SongStreams ON Albums_Tracks.spotify_track_id = SongStreams.spotify_track_id "
				+ "WHERE SongStreams.username = :username "
				+ "AND SongStreams.ts BETWEEN :startDate AND :endDate "
				+ "GROUP BY "
				+ "Albums.id,"
				+ "Albums.name,"
				+ "Albums.thumbnail_url,"
				+ "primary_artist_name "
				+ "ORDER BY "
				+ sort
				+ " DESC;");
		return jdbc.sql(sql)
				.param("username", username, Types.VARCHAR)
				.param("startDate", startDate, Types.TIMESTAMP)
				.param("endDate", endDate, Types.TIMESTAMP)
				.query(AlbumStats.class)
				.list();
	}
	
	
	List<AlbumStats> topAlbums(String sort,
			String username,
			Boolean checkForCache) {
		
		String sql;
		
		if (checkForCache) {
			sql = ("SELECT username,"
					+ "id,"
					+ "name,"
					+ "thumbnail_url,"
//					TODO: figure out why a null happened here
					+ "COALESCE(primary_artist_name, 'Unknown Artist') AS primary_artist_name,"
					+ "COALESCE(stream_count, 0) AS stream_count, "
					+ "COALESCE(total_ms_played, 0) AS total_ms_played "
					+ "FROM Top_Albums_Cache "
					+ "WHERE username = :username "
					+ "ORDER BY "
					+ sort
					+ " DESC;");
		} else {
			sql = ("SELECT Albums.*,"
					+ ":username AS username,"
					+ "COUNT("
					+ "CASE WHEN SongStreams.ms_played >= 30000 THEN SongStreams.spotify_track_id END"
					+ ") AS stream_count,"
					+ "SUM(SongStreams.ms_played) AS total_ms_played,"
					+ "(SELECT Artists.name "
					+ "FROM Artists "
					+ "JOIN Tracks_Artists ON Tracks_Artists.artist_id = Artists.id "
					+ "WHERE Tracks_Artists.spotify_track_id = Albums_Tracks.spotify_track_id "
					+ "AND Tracks_Artists.artist_order = 0 "
					+ "LIMIT 1) AS primary_artist_name "
					+ "FROM Albums "
					+ "LEFT JOIN Albums_Tracks ON Albums.id = Albums_Tracks.album_id "
					+ "LEFT JOIN SongStreams ON Albums_Tracks.spotify_track_id = SongStreams.spotify_track_id "
					+ "WHERE SongStreams.username = :username "
					+ "GROUP BY "
					+ "Albums.id,"
					+ "Albums.name,"
					+ "Albums.thumbnail_url,"
					+ "primary_artist_name "
					+ "ORDER BY "
					+ sort
					+ " DESC;");
		}
		
		List<AlbumStats> rawList = jdbc.sql(sql)
				.param("username", username, Types.VARCHAR)
				.query(AlbumStats.class)
				.list();
		
		if (checkForCache && rawList.isEmpty()) {
			System.out.println("empty result");
			updateTopAlbumsCache(username);

		}
		
		return jdbc.sql(sql)
				.param("username", username, Types.VARCHAR)
				.query(AlbumStats.class)
				.list();
	}
	

	public int updateTopAlbumsCache(String username
			) {
//		Doesn't have to be sorted, but I don't feel like overloading the constructor again
		List<AlbumStats> albumStatsList = topAlbums("stream_count", username, false);
		
		
		String wipeCache = ("DELETE FROM Top_Albums_Cache WHERE username = :username");
		
		int deletedRows = jdbc.sql(wipeCache)
				.param("username", username, Types.VARCHAR)
				.update();
		
		int rowsAdded = 0;
		
		for (AlbumStats album:albumStatsList) {
			List<RecordCompInfo> recordComps;
			try {
				recordComps = CompInfo.get(album);
			} catch (IllegalAccessException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
			
			String addAlbumStats = CompListToSql.insertTopItemsCache(recordComps, "Album");
			StatementSpec jdbcCall = jdbc.sql(addAlbumStats + " ON CONFLICT (username, id) DO NOTHING");
			
			for (RecordCompInfo comp:recordComps) {
				jdbcCall = jdbcCall.param(
						comp.compName(),
						comp.compValue(),
						comp.sqlType());
			}
			rowsAdded += jdbcCall.update();
		}
		
		log.info("Deleted " + deletedRows + " rows from top albums cache");
		log.info("Added " + rowsAdded + " rows to top albums cache");
		return rowsAdded;
	}
	
}
