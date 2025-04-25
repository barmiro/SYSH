package com.github.barmiro.sysh_server.integrationtests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.MockServerRestClientCustomizer;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import com.github.barmiro.sysh_server.security.SyshUser;
import com.github.barmiro.sysh_server.security.UserRole;
import com.github.barmiro.sysh_server.spotifyauthorization.SpotifyTokenService;
import com.github.barmiro.sysh_server.users.SyshUserManager;

@SpringBootTest
class AuthTests {
	
	@Autowired
	SyshUserManager userManager;
	
	private final String clientId = System.getenv("SPOTIFY_CLIENT_ID");
	private final String clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");
	private final String base64 = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
	
	
	@SuppressWarnings("resource")
	@Container
	@ServiceConnection
	public static JdbcDatabaseContainer<?> postgres 
						= new PostgreSQLContainer<>("postgres:latest")
										.withDatabaseName("sysh_db")
										.withUsername("user")
										.withPassword("test")
										.withInitScript("schema.sql");
	
	
	@Test
	void tokenTest() {
		
		userManager.createUser(new SyshUser("user", "password", "UTC"));
		
		MockServerRestClientCustomizer customizer = new MockServerRestClientCustomizer();
		RestClient.Builder builder = RestClient.builder()
		.baseUrl("https://accounts.spotify.com/api/token");
		
		customizer.customize(builder);
		SpotifyTokenService tkn = new SpotifyTokenService(builder.build(), userManager);
		
		customizer.getServer().expect(requestTo("https://accounts.spotify.com/api/token"))
		.andExpect(content().formData(SampleResponseBodies.tokenRequest()))
		.andExpect(header("Authorization", "Basic " + base64))
		.andRespond(withSuccess(SampleResponseBodies.tokenResponse(), MediaType.APPLICATION_JSON));
		
		
		customizer.getServer().expect(requestTo("https://accounts.spotify.com/api/token"))
		.andExpect(content().formData(SampleResponseBodies.refreshRequest()))
		.andExpect(header("Authorization", "Basic " + base64))
		.andRespond(withSuccess(SampleResponseBodies.refreshResponse(), MediaType.APPLICATION_JSON));
		
		
		tkn.getNewToken("randomcode", "user");
		assertTrue(tkn.refresh("user"));
		assertFalse(tkn.refresh("user"));
		
		
		
	}

}