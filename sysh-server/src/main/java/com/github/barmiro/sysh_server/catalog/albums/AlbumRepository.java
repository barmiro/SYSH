package com.github.barmiro.sysh_server.catalog.albums;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import org.springframework.stereotype.Repository;

import com.github.barmiro.sysh_server.catalog.CatalogRepository;
import com.github.barmiro.sysh_server.common.records.RecordCompInfo;
import com.github.barmiro.sysh_server.common.utils.CompInfo;
import com.github.barmiro.sysh_server.common.utils.CompListToSql;

@Repository
public class AlbumRepository implements CatalogRepository {
	private final JdbcClient jdbc;
	AlbumRepository(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}
	
	
	public List<Album> allAlbums() {
		return jdbc.sql("SELECT * FROM Albums")
				.query(Album.class)
				.list();
	}
	
	
	public Integer addAlbum(Album album
			) throws IllegalAccessException, InvocationTargetException {
		
		List<RecordCompInfo> recordComps = CompInfo.get(album);
		
		String sql = CompListToSql.insert(recordComps, Album.class);
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
	

	public Integer addAlbums(List<Album> albums) {
		Integer added = 0;
		for (Album album:albums) {
			try {
				added += addAlbum(album);
			} catch (IllegalAccessException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Added " + added + " new albums");
		return added;
	}
	
}
