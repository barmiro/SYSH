package com.github.barmiro.sysh_server.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.github.barmiro.sysh_server.auth.TokenService;
import com.github.barmiro.sysh_server.catalog.albums.AlbumController;
import com.github.barmiro.sysh_server.catalog.albums.AlbumStats;
import com.github.barmiro.sysh_server.catalog.artists.ArtistController;
import com.github.barmiro.sysh_server.catalog.artists.ArtistStats;
import com.github.barmiro.sysh_server.dataintake.recent.RecentController;

@Testcontainers
@SpringBootTest
@AutoConfigureMockRestServiceServer
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationTest {

	@Test
	void contextLoads() {
	}
	
	@Autowired
	TokenService ts;
	
	@Autowired
	MockRestServiceServer server;
	
	
	@Autowired
	RestClient apiClient;
	
	@Autowired
	private RecentController rc;
	
	@Autowired
	private AlbumController albc;
	
	@Autowired
	private ArtistController artc;
	
	@SuppressWarnings("resource")
	@Container
	@ServiceConnection
	public static JdbcDatabaseContainer<?> postgres 
						= new PostgreSQLContainer<>("postgres:latest")
										.withDatabaseName("sysh_db")
										.withUsername("user")
										.withPassword("test")
										.withInitScript("schema.sql");

	
	
	@Sql(statements = {"DELETE FROM Streams",
			"DELETE FROM Tracks",
			"DELETE FROM Track_Duplicates",
			"DELETE FROM Albums",
			"DELETE FROM Album_Tracklist",
			"DELETE FROM Artists"})
	@Test
	@Order(1)
	void recentTest() {
		ts.setToken("abcde");
		
		server.expect(requestTo("https://api.spotify.com/v1/me/player/recently-played"))
			.andRespond(withSuccess(
					SampleResponseBodies.recent(),
					MediaType.APPLICATION_JSON));
		
		server.expect(requestTo("https://api.spotify.com/v1/tracks?ids=7o2AeQZzfCERsRmOM86EcB,536rHxlVFXGJBO2xWE7HsV,6TeKbncyK62smlAvPy1dNa,1TF3L6npXn08LjwRdQGBww,0eGpcLG96GWVCG4Ix3qLCp"))
		.andRespond(withSuccess(
				SampleResponseBodies.tracks(),
				MediaType.APPLICATION_JSON));
		
//		server.expect(requestTo("https://api.spotify.com/v1/albums?ids=7aNclGRxTysfh6z0d8671k,3o1TOhMkU5FFMSJMDhXfdF,6YUCc2RiXcEKS9ibuZxjt0"))
//			.andRespond(withSuccess(
//					SampleResponseBodies.albums(),
//					MediaType.APPLICATION_JSON));
		
		server.expect(requestTo("https://api.spotify.com/v1/artists?ids=6kBDZFXuLrZgHnvmPu9NsG,6nB0iY1cjSY1KyhYyuIIKH,7guDJrEfX3qb6FEbdPA5qi"))
		.andRespond(withSuccess(
				SampleResponseBodies.artists(),
				MediaType.APPLICATION_JSON));
		
		
		String result = rc.recent();
		assertEquals("5 streams added.\n5 tracks added.\n3 albums added.\n3 artists added.\n", result);
	}
	
	@Test
	@Order(2)
	void topAlbumsTest() {
		List<AlbumStats> albumStats = SampleResponseBodies.albumStats();
		
		List<AlbumStats> response = albc.topAlbums(Optional.empty(), Optional.empty(), Optional.empty());

				
		assertEquals(albumStats, response);
		
	}
	
	@Test
	@Order(3)
	void topArtistsTest() {
		List<ArtistStats> artistStats = SampleResponseBodies.artistStats();
		
		List<ArtistStats> response = artc.topArtists(Optional.empty(), Optional.empty(), Optional.empty());
		
		
		assertEquals(artistStats, response);
		
	}

}
