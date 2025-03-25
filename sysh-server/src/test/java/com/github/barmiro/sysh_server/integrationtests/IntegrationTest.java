package com.github.barmiro.sysh_server.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.MockServerRestClientCustomizer;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.barmiro.sysh_server.catalog.AddToCatalog;
import com.github.barmiro.sysh_server.catalog.albums.AlbumController;
import com.github.barmiro.sysh_server.catalog.albums.AlbumRepository;
import com.github.barmiro.sysh_server.catalog.albums.AlbumStats;
import com.github.barmiro.sysh_server.catalog.artists.ArtistController;
import com.github.barmiro.sysh_server.catalog.artists.ArtistRepository;
import com.github.barmiro.sysh_server.catalog.artists.ArtistStats;
import com.github.barmiro.sysh_server.catalog.artists.spotifyapi.ArtistApiRepository;
import com.github.barmiro.sysh_server.catalog.jointables.AlbumsTracks;
import com.github.barmiro.sysh_server.catalog.jointables.TracksArtists;
import com.github.barmiro.sysh_server.catalog.streams.StreamRepository;
import com.github.barmiro.sysh_server.catalog.tracks.TrackRepository;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.TrackApiRepository;
import com.github.barmiro.sysh_server.dataintake.recent.RecentController;
import com.github.barmiro.sysh_server.security.SyshUser;
import com.github.barmiro.sysh_server.security.SyshUserDetails;
import com.github.barmiro.sysh_server.spotifyauthorization.SpotifyTokenService;
import com.github.barmiro.sysh_server.stats.StatsCache;
import com.github.barmiro.sysh_server.stats.StatsForRange;
import com.github.barmiro.sysh_server.stats.StatsRepository;
import com.github.barmiro.sysh_server.users.SyshUserManager;

@Testcontainers
@SpringBootTest
@WithMockUser(username = "user", roles = {"USER"}, password = "password")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationTest {

	@Test
	void contextLoads() {
	}

//	I hate this so much. This is the only way I've figured out how to implement testing for both this and auth logic
	@Autowired
	private JdbcClient jdbc;
	
	@Autowired
	private SpotifyTokenService tkn;
	
	@Autowired
	private TrackRepository trackRepo;
	
	@Autowired
	private AlbumRepository albumRepo;

	@Autowired
	private StreamRepository sr;
	
	@Autowired
	private ArtistRepository artistRepo;
	
	@Autowired
	private AlbumController albc;
	
	@Autowired
	private ArtistController artc;
	
	@Autowired
	private AlbumsTracks albTra;
	
	@Autowired
	private TracksArtists traArt;
	
	@Autowired
	private StatsRepository stats;
	
	@Autowired
	private StatsCache statsCache;
	
	@Autowired
	SyshUserManager userManager;
	

	@SuppressWarnings("resource")
	@Container
	@ServiceConnection
	public static JdbcDatabaseContainer<?> postgres 
						= new PostgreSQLContainer<>("postgres:latest")
										.withDatabaseName("sysh_db")
										.withUsername("user")
										.withPassword("test")
										.withInitScript("schema.sql");

	
	
	

	@Sql(statements = {"DELETE FROM SongStreams",
			"DELETE FROM Tracks",
			"DELETE FROM Track_Duplicates",
			"DELETE FROM Albums",
			"DELETE FROM Album_Tracklist",
			"DELETE FROM Artists",
			"DELETE FROM Users"})
	@Test
	@Order(1)
	void recentTest() throws JsonProcessingException, ClassCastException, IllegalAccessException, InvocationTargetException {
		
		userManager.createUser(new SyshUserDetails(
				new SyshUser("user", "password")));
		
		userManager.updateUserSpotifyRefreshToken("user", "refresh");
		userManager.updateUserTokenExpirationTime("user", Timestamp.valueOf(LocalDateTime.now().plusHours(1)));
		
		tkn.setToken("user", "abcde");
		tkn.expTimeFromExpiresIn("user", 3600);
		
		MockServerRestClientCustomizer customizer = new MockServerRestClientCustomizer();
		RestClient.Builder builder = RestClient.builder()
				.baseUrl("https://api.spotify.com/v1/");
		
		customizer.customize(builder);

		TrackApiRepository trApi = new TrackApiRepository(jdbc, builder.build(), tkn, trackRepo);
		ArtistApiRepository arApi = new ArtistApiRepository(jdbc, builder.build(), tkn, artistRepo);
		AddToCatalog add = new AddToCatalog(trackRepo, trApi, albumRepo, arApi, sr, albTra, traArt, tkn, statsCache);
		RecentController rc = new RecentController(tkn, builder.build(), sr, add);
		
		
		customizer.getServer().expect(requestTo("https://api.spotify.com/v1/me/player/recently-played?limit=50"))
			.andRespond(withSuccess(
					SampleResponseBodies.recent(),
					MediaType.APPLICATION_JSON));
		
		customizer.getServer().expect(requestTo("https://api.spotify.com/v1/tracks?ids=7o2AeQZzfCERsRmOM86EcB,536rHxlVFXGJBO2xWE7HsV,6TeKbncyK62smlAvPy1dNa,1TF3L6npXn08LjwRdQGBww,0eGpcLG96GWVCG4Ix3qLCp"))
		.andRespond(withSuccess(
				SampleResponseBodies.tracks(),
				MediaType.APPLICATION_JSON));

		
		customizer.getServer().expect(requestTo("https://api.spotify.com/v1/artists?ids=6kBDZFXuLrZgHnvmPu9NsG,6nB0iY1cjSY1KyhYyuIIKH,7guDJrEfX3qb6FEbdPA5qi"))
		.andRespond(withSuccess(
				SampleResponseBodies.artists(),
				MediaType.APPLICATION_JSON));

		
		String result = rc.recent();
		assertEquals("5", result);
	}
	
	@Test
	@Order(2)
	void topAlbumsTest() {
		List<AlbumStats> albumStats = SampleResponseBodies.albumStats();
		
		List<AlbumStats> response = albc.topAlbums(
				Optional.empty(),
				LocalDateTime.of(2000, 1, 1, 0, 0),
				LocalDateTime.of(2038, 1, 1, 0, 0),
				Optional.empty(),
				Optional.empty());

				
		assertEquals(albumStats, response);
		
	}
	
	@Test
	@Order(3)
	void topArtistsTest() {
		List<ArtistStats> artistStats = SampleResponseBodies.artistStats();
		
		List<ArtistStats> response = artc.topArtists(
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty());
		
		assertEquals(artistStats, response);
		
	}
	
	@Test
	@Order(4)
	void statsRepositoryTest() {
		OffsetDateTime startDate = OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(0));
		OffsetDateTime endDate = OffsetDateTime.of(2025, 2, 1, 0, 0, 0, 0, ZoneOffset.ofHours(0));
		
		StatsForRange expectedStats = new StatsForRange(
				"user",
				startDate,
				endDate,
				23,
				5,
				5,
				3,
				3);
		assertEquals(expectedStats, stats.streamStats(startDate, endDate, true, "user"));
	}

}
