package com.github.barmiro.sysh_server.catalog.artists;

import java.lang.reflect.InvocationTargetException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpServerErrorException;

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
				e.printStackTrace();
				throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		log.info("Added " + added + " new artists");
		return added;
	}
	
	
	
public List<ArtistStats> topArtists(String sort,
		OffsetDateTime startDate,
		OffsetDateTime endDate,
		Integer offset,
		String size,
		String username) {
	
	String listSpec = ("ORDER BY "
			+ sort
			+ " DESC "
			+ "LIMIT "
			+ size
			+ " OFFSET "
			+ offset);
		
	String sql = ("SELECT Artists.*,"
			+ "COUNT("
			+ "CASE WHEN SongStreams.ms_played >= 30000 THEN SongStreams.spotify_track_id END"
			+ ") AS stream_count,"
			+ "COALESCE(SUM(SongStreams.ms_played), 0) AS total_ms_played "
			+ "FROM Artists "
			+ "LEFT JOIN Tracks_Artists ON Artists.id = Tracks_Artists.artist_id "
			+ "LEFT JOIN SongStreams ON Tracks_Artists.spotify_track_id = SongStreams.spotify_track_id "
			+ "WHERE SongStreams.username = :username "
			+ "AND SongStreams.ts BETWEEN :startDate AND :endDate "
			+ "GROUP By "
			+ "Artists.id,"
			+ "Artists.name,"
			+ "Artists.thumbnail_url "
			+ listSpec);
	
	return jdbc.sql(sql)
			.param("username", username, Types.VARCHAR)
			.param("startDate", startDate, Types.TIMESTAMP_WITH_TIMEZONE)
			.param("endDate", endDate, Types.TIMESTAMP_WITH_TIMEZONE)
			.query(ArtistStats.class)
			.list();
	}
}
