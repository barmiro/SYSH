package com.github.barmiro.sysh_server;

import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class TrackServiceTest {

//	private ImageFromDockerfile image = new ImageFromDockerfile()
//			.withFileFromPath("schema.sql", Paths.get(System.getenv("TESTCONTAINERS_PATH") + "schema.sql"))
//			.withDockerfile(Paths.get(System.getenv("TESTCONTAINERS_PATH") + "TestDockerfile"));
//	
//	@Container
//	public GenericContainer postgres = new GenericContainer(image)
//			.withExposedPorts(5432);
//	
//	
//	
//	@BeforeEach
//	void init() {
//		
//	}
//	
//	@Test
//	void testAllTracks() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testAddNewTrack() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testAddTracks() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testTopTracksNew() {
//		fail("Not yet implemented");
//	}

}
