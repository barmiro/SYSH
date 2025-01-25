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

import com.github.barmiro.sysh_server.catalog.albums.Album;
import com.github.barmiro.sysh_server.catalog.albums.AlbumRepository;

@Testcontainers
@SpringBootTest
class AlbumRepositoryTest {

	@Autowired
	private AlbumRepository ar;
	
	@Container
	@ServiceConnection
	public static JdbcDatabaseContainer<?> postgres 
						= new PostgreSQLContainer<>("postgres:latest")
										.withDatabaseName("sysh_db")
										.withUsername("user")
										.withPassword("test")
										.withInitScript("schema.sql");


	List<Album> albums;
	int rowCount = 5;

	@BeforeEach
	void init() {
		albums = new ArrayList<>();
		for (int i = 0; i < rowCount; i++) {
			albums.add(new Album (
					"album id " + i,
					"album name " + i,
					i,
					"2024-01-" + (10 + i),
					"randomurl"));
		}
	}
	
	
	@Test void testValidAlbums() {
		assertEquals(rowCount, albums.size());
		ar.addAlbums(albums);
		
		List<Album> retrieved = ar.findAll();
		
		for (Album album:albums) {
			assertTrue(retrieved.contains(album));
		}
	}
	
	
	@Test void testEmptyAlbum() {

		Album empty = new Album(null, null, null, null, null);
		albums.add(empty);
		assertEquals(rowCount + 1, albums.size());
		ar.addAlbums(albums);
		
		List<Album> retrieved = ar.findAll();
		
		assertFalse(retrieved.contains(empty));
		assertEquals(rowCount, retrieved.size());
		
	}

}
