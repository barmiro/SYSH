package com.github.barmiro.sysh_server.catalog.albums;

import java.sql.Types;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import com.github.barmiro.sysh_server.catalog.CatalogService;

@Service
public class AlbumService implements CatalogService {
	private final JdbcClient jdbc;
	
	AlbumService(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}
	
	public List<Album> allAlbums() {
		return jdbc.sql("SELECT * FROM Albums")
				.query(Album.class)
				.list();
	}
	
	
	public Integer addAlbum(Album album) {
		String newAlbum = ("INSERT INTO Albums("
				+ "id,"
				+ "name,"
				+ "total_tracks,"
				+ "release_date"
				+ ") VALUES ("
				+ ":id,"
				+ ":name,"
				+ ":total_tracks,"
				+ ":release_date)");
		
		Integer albumsAdded = 0;
		
		try {
			albumsAdded = jdbc.sql(newAlbum)
					.param("id", album.id(), Types.VARCHAR)
					.param("name", album.name(), Types.VARCHAR)
					.param("total_tracks", album.total_tracks(), Types.INTEGER)
					.param("release_date", album.release_date(), Types.VARCHAR)
					.update();
		} catch (DuplicateKeyException e) {
			return 100000;
		}
		
		return albumsAdded;
	}
	
	
	
}
