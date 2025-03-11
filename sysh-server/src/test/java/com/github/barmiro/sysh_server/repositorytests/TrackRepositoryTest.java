package com.github.barmiro.sysh_server.repositorytests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
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

	@SuppressWarnings("resource")
	@Container
	@ServiceConnection
	public static JdbcDatabaseContainer<?> postgres 
						= new PostgreSQLContainer<>("postgres:latest")
										.withDatabaseName("sysh_db")
										.withUsername("user")
										.withPassword("test")
										.withInitScript("schema.sql");

	
	List<Track> tracks;
	int rowCount = 5;
	
	@BeforeEach
	void init() {		
		tracks = new ArrayList<>();
		for (int i = 0; i < rowCount; i++) {
			tracks.add(new Track(
					"track id " + i,
					"track name " + i,
					i * 1000,
					"album id " + i,
					i,
					i));
		}	
	}
	
	
	@Test void testValidTracks() throws IllegalAccessException, InvocationTargetException {
		assertEquals(rowCount, tracks.size());
		tr.addTracks(tracks);
		
		List<Track> retrieved = tr.findAll();
		
		for (Track track:tracks) {
			assertTrue(retrieved.contains(track));
		}
	}
	
	
	@Test void testDuplicates() throws IllegalAccessException, InvocationTargetException {
		
		List<Track> duplicates = new ArrayList<>();
		
		assertEquals(0, tr.getDuplicatesFor(tracks.get(0)).size());

		for (Track track:tracks) {
			int index = tracks.indexOf(track);
			
			 duplicates.add(new Track(
					"track id duplicate " + index,
					track.name(),
					track.duration_ms(),
					"album id duplicate " + index,
					index,
					index));
		}
		
		
		tr.addTracks(duplicates);
		List<Track> retrieved = new ArrayList<>();
		
		for (Track track:tracks) {
			retrieved.addAll(tr.getDuplicatesFor(track));
		}
		
		for (Track dupe:retrieved) {
			assertTrue(duplicates.contains(dupe));
		}
		
		retrieved.clear();
		
		for (Track duplicate:duplicates) {
			retrieved.addAll(tr.getDuplicatesFor(duplicate));			
		}
		
		for (Track track:retrieved) {
			assertTrue(tracks.contains(track));
		}
		
	}
	
	@Test void testEmptyTrack() throws IllegalAccessException, InvocationTargetException {
		int tableSize = rowCount * 2;

		Track empty = new Track(null, null, null, null, null, null);
		tracks.add(empty);
		assertEquals(rowCount + 1, tracks.size());
		tr.addTracks(tracks);
		
		List<Track> retrieved = tr.findAll();
		
		assertFalse(retrieved.contains(empty));
		assertEquals(tableSize, retrieved.size());
		
	}

}


