package com.github.barmiro.sysh_server.catalog.artists;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.github.barmiro.sysh_server.catalog.interfaces.CatalogRepository;

@Repository
public class ArtistRepository extends CatalogRepository<Artist> {
	
	ArtistRepository(JdbcClient jdbc) {
		super(jdbc);
	}
	
	
	

	public int addArtists(List<Artist> artists) {
		int added = 0;
		for (Artist artist:artists) {
			try {
				added += addNew(artist, Artist.class);
			} catch (IllegalAccessException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Added " + added + " new artists");
		return added;
	}

}
