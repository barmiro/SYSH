package com.github.barmiro.sysh_server;

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

import com.github.barmiro.sysh_server.catalog.tracks.Track;
import com.github.barmiro.sysh_server.catalog.tracks.TrackRepository;





@Testcontainers
@SpringBootTest
class TrackServiceTest {

	@Autowired
	private TrackRepository tr;

	

	@Container
	@ServiceConnection
	public static JdbcDatabaseContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
														.withDatabaseName("sysh_db")
														.withUsername("user")
														.withPassword("test")
														.withInitScript("schema.sql");

	
	List<Track> tracks = new ArrayList<>();

	@BeforeEach
	void init() {		
		for (int i = 0; i < 5; i++) {
			tracks.add(new Track(
					"track id " + i,
					"track name " + i,
					i * 1000,
					"album id " + i,
					i,
					i));
		}
	}
	
	@Test void testValidTracks() {
		assertEquals(5, tracks.size());
		tr.addTracks(tracks);
		
		List<Track> retrieved = tr.allTracks();
		
		for (Track track:tracks) {
			assertTrue(retrieved.contains(track));
		}
	}
	
	@Test void testEmptyTrack() {
		Track empty = new Track(null, null, null, null, null, null);
		tracks.add(empty);
		assertEquals(6, tracks.size());
		tr.addTracks(tracks);
		
		List<Track> retrieved = tr.allTracks();
		
		assertFalse(retrieved.contains(empty));
		assertEquals(5, retrieved.size());

	}
	
	

}


