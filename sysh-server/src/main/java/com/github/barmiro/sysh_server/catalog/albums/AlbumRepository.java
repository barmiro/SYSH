package com.github.barmiro.sysh_server.catalog.albums;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.github.barmiro.sysh_server.catalog.interfaces.CatalogRepository;

@Repository
public class AlbumRepository extends CatalogRepository<Album> {

	AlbumRepository(JdbcClient jdbc) {
		super(jdbc);
	}
	
	
	public List<Album> allAlbums() {
		return jdbc.sql("SELECT * FROM Albums")
				.query(Album.class)
				.list();
	}
	
	
//	public Integer addAlbum(Album album
//			) throws IllegalAccessException, InvocationTargetException {
//		
//		List<RecordCompInfo> recordComps = CompInfo.get(album);
//		
//		String sql = CompListToSql.insert(recordComps, Album.class);
//		StatementSpec jdbcCall = jdbc.sql(sql);
//		
//		for (RecordCompInfo comp:recordComps) {
//			jdbcCall = jdbcCall.param(
//					comp.compName(),
//					comp.compValue(),
//					comp.sqlType());
//		}
//		
//		Integer added = 0;
//		try {
//			added = jdbcCall.update();		
//			
//		} catch (DuplicateKeyException e){
//			System.out.println(
//					album.name() 
//					+ " : "
//					+ album.getId() 
//					+ " already exists.");
//			return 0;
//			
//		} catch(DataIntegrityViolationException e) {
//			System.out.println(
//					album.name() 
//					+ " : "
//					+ album.getId() 
//					+ "contains invalid values.");
//			return 0;
//		}
//		
//		return added;
//	}
	

	public Integer addAlbums(List<Album> albums) {
		Integer added = 0;
		for (Album album:albums) {
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
	
}
