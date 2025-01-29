package com.github.barmiro.sysh_server.catalog.artists;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.github.barmiro.sysh_server.catalog.interfaces.CatalogRepository;

@Repository
public class ArtistRepository extends CatalogRepository<Artist> {
	
	ArtistRepository(JdbcClient jdbc) {
		super(jdbc);
	}
	
	private static final Logger log = LoggerFactory.getLogger(ArtistRepository.class);
	

	public int addArtists(List<Artist> artists) {
		int added = 0;
		for (Artist artist:artists) {
			try {
				added += addNew(artist, Artist.class);
			} catch (IllegalAccessException | InvocationTargetException e) {
				log.error(e.getMessage());
			}
		}
		log.info("Added " + added + " new artists");
		return added;
	}
	
	
	
public List<ArtistStats> topArtistsCount(Timestamp startDate, Timestamp endDate) {
		
		String sql = ("SELECT Artists.*, COUNT(Streams.ts) as sort_param "
				+ "FROM Artists "
				+ "LEFT JOIN Tracks_Artists ON Artists.id = Tracks_Artists.artist_id "
				+ "LEFT JOIN Streams ON Tracks_Artists.spotify_track_id = Streams.spotify_track_id "
				+ "WHERE Streams.ts BETWEEN :startDate AND :endDate "
				+ "AND Streams.ms_played >= 30000 "
				+ "GROUP By "
				+ "Artists.id,"
				+ "Artists.name "
				+ "ORDER BY sort_param DESC;");
		
		return jdbc.sql(sql)
				.param("startDate", startDate, Types.TIMESTAMP)
				.param("endDate", endDate, Types.TIMESTAMP)
				.query(ArtistStats.class)
				.list();
	}
	
	public List<ArtistStats> topArtistsTime(Timestamp startDate, Timestamp endDate) {
		String sql = ("SELECT Artists.*, SUM(Streams.ms_played) / 60000 as sort_param "
				+ "FROM Artists "
				+ "LEFT JOIN Tracks_Artists ON Artists.id = Tracks_Artists.artist_id "
				+ "LEFT JOIN Streams ON Tracks_Artists.spotify_track_id = Streams.spotify_track_id "
				+ "WHERE Streams.ts BETWEEN :startDate AND :endDate "
				+ "GROUP By "
				+ "Artists.id,"
				+ "Artists.name "
				+ "ORDER BY sort_param DESC;");
		
		return jdbc.sql(sql)
				.param("startDate", startDate, Types.TIMESTAMP)
				.param("endDate", endDate, Types.TIMESTAMP)
				.query(ArtistStats.class)
				.list();
	}

}
