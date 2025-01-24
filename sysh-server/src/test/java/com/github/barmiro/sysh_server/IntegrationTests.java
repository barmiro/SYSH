package com.github.barmiro.sysh_server;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
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
import com.github.barmiro.sysh_server.integration.recent.RecentController;

@Testcontainers
@SpringBootTest
@AutoConfigureMockRestServiceServer
class IntegrationTests {

	@Autowired
	TokenService ts;
	
	@Autowired
	MockRestServiceServer server;
	
	@Autowired
	RestClient apiClient;
	
	@Autowired
	private RecentController rc;
	
	@Container
	@ServiceConnection
	public static JdbcDatabaseContainer<?> postgres 
						= new PostgreSQLContainer<>("postgres:latest")
										.withDatabaseName("sysh_db")
										.withUsername("user")
										.withPassword("test")
										.withInitScript("schema.sql");

	
	@Sql(statements = {"DELETE FROM streams",
			"DELETE FROM Tracks",
			"DELETE FROM Track_Duplicates",
			"DELETE FROM Albums",
			"DELETE FROM Album_Tracklist",
			"DELETE FROM Artists",
			"DELETE FROM Artist_Discography"})
	@Test
	void test() {
		ts.setToken("abcde");
		
		server.expect(requestTo("https://api.spotify.com/v1/me/player/recently-played"))
			.andRespond(withSuccess(
					SampleResponseBodies.recent(),
					MediaType.APPLICATION_JSON));
		
		server.expect(requestTo("https://api.spotify.com/v1/tracks?ids=7o2AeQZzfCERsRmOM86EcB,536rHxlVFXGJBO2xWE7HsV,6TeKbncyK62smlAvPy1dNa,1TF3L6npXn08LjwRdQGBww,0eGpcLG96GWVCG4Ix3qLCp"))
		.andRespond(withSuccess(
				SampleResponseBodies.tracks(),
				MediaType.APPLICATION_JSON));
		
		server.expect(requestTo("https://api.spotify.com/v1/albums?ids=7aNclGRxTysfh6z0d8671k,3o1TOhMkU5FFMSJMDhXfdF,6YUCc2RiXcEKS9ibuZxjt0"))
			.andRespond(withSuccess(
					SampleResponseBodies.albums(),
					MediaType.APPLICATION_JSON));
		
		
		String result = rc.recent();
		assertEquals("5 5 3", result);
	}
	

}
