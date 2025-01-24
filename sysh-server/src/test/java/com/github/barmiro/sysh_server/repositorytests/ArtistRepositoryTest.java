package com.github.barmiro.sysh_server.repositorytests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.github.barmiro.sysh_server.catalog.artists.Artist;
import com.github.barmiro.sysh_server.catalog.artists.ArtistRepository;

@Testcontainers
@SpringBootTest
class ArtistRepositoryTest {

	@Autowired
	private ArtistRepository ar;
	
	@Container
	@ServiceConnection
	public static JdbcDatabaseContainer<?> postgres 
						= new PostgreSQLContainer<>("postgres:latest")
										.withDatabaseName("sysh_db")
										.withUsername("user")
										.withPassword("test")
										.withInitScript("schema.sql");


	List<Artist> artists;
	int rowCount = 5;

	@BeforeEach
	void init() {
		artists = new ArrayList<>();
		for (int i = 0; i < rowCount; i++) {
			artists.add(new Artist (
					"artist id " + i,
					"artist name " + i));
		}
	}
	
	
	@Test void testValidArtists() {
		assertEquals(rowCount, artists.size());
		ar.addArtists(artists);
		
		List<Artist> retrieved = ar.findAll();
		
		for (Artist artist:artists) {
			assertTrue(retrieved.contains(artist));
		}
	}
	
	
	@Test void testEmptyArtist() {

		Artist empty = new Artist(null, null);
		artists.add(empty);
		assertEquals(rowCount + 1, artists.size());
		ar.addArtists(artists);
		
		List<Artist> retrieved = ar.findAll();
		
		assertFalse(retrieved.contains(empty));
		assertEquals(rowCount, retrieved.size());
		
	}

}
