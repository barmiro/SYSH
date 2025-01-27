package com.github.barmiro.sysh_server.catalog.albums;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.github.barmiro.sysh_server.catalog.interfaces.CatalogRepository;

@Repository
public class AlbumRepository extends CatalogRepository<Album> {

	AlbumRepository(JdbcClient jdbc) {
		super(jdbc);
	}
	
	
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
		System.out.println("Added " + added + " new albums");
		
		
		return added;
	}
	
	
	public List<AlbumStats> topAlbumsCount(Timestamp startDate, Timestamp endDate) {
		
		String sql = ("SELECT Albums.*, COUNT(Streams.spotify_track_id) as sort_param "
				+ "FROM Albums "
				+ "LEFT JOIN Albums_Tracks ON Albums.id = Albums_Tracks.album_id "
				+ "LEFT JOIN Streams ON Albums_Tracks.spotify_track_id = Streams.spotify_track_id "
				+ "WHERE Streams.ts BETWEEN :startDate AND :endDate "
				+ "AND Streams.ms_played >= 30000 "
				+ "GROUP By "
				+ "Albums.id,"
				+ "Albums.name "
				+ "ORDER BY sort_param DESC;");
		
		return jdbc.sql(sql)
				.param("startDate", startDate, Types.TIMESTAMP)
				.param("endDate", endDate, Types.TIMESTAMP)
				.query(AlbumStats.class)
				.list();
	}
	
	public List<AlbumStats> topAlbumsTime(Timestamp startDate, Timestamp endDate) {
		String sql = ("SELECT Albums.*, SUM(Streams.ms_played) / 60000 as sort_param "
				+ "FROM Albums "
				+ "LEFT JOIN Albums_Tracks ON Albums.id = Albums_Tracks.album_id "
				+ "LEFT JOIN Streams ON Albums_Tracks.spotify_track_id = Streams.spotify_track_id "
				+ "WHERE Streams.ts BETWEEN :startDate AND :endDate "
				+ "GROUP By "
				+ "Albums.id,"
				+ "Albums.name "
				+ "ORDER BY sort_param DESC;");
		
		return jdbc.sql(sql)
				.param("startDate", startDate, Types.TIMESTAMP)
				.param("endDate", endDate, Types.TIMESTAMP)
				.query(AlbumStats.class)
				.list();
	}
	
}
