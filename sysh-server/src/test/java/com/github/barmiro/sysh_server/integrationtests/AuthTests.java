package com.github.barmiro.sysh_server.integrationtests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.MockServerRestClientCustomizer;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import com.github.barmiro.sysh_server.auth.TokenService;

@SpringBootTest
class AuthTests {
	
	
	private final String clientId = System.getenv("SPOTIFY_CLIENT_ID");
	private final String clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");
	private final String base64 = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
	
	
	@Test
	void tokenTest() {
		
		MockServerRestClientCustomizer customizer = new MockServerRestClientCustomizer();
		RestClient.Builder builder = RestClient.builder()
		.baseUrl("https://accounts.spotify.com/api/token");
		
		customizer.customize(builder);
		TokenService tkn = new TokenService(builder.build());
		
		customizer.getServer().expect(requestTo("https://accounts.spotify.com/api/token"))
		.andExpect(content().formData(SampleResponseBodies.tokenRequest()))
		.andExpect(header("Authorization", "Basic " + base64))
		.andRespond(withSuccess(SampleResponseBodies.tokenResponse(), MediaType.APPLICATION_JSON));
		
		
		customizer.getServer().expect(requestTo("https://accounts.spotify.com/api/token"))
		.andExpect(content().formData(SampleResponseBodies.refreshRequest()))
		.andExpect(header("Authorization", "Basic " + base64))
		.andRespond(withSuccess(SampleResponseBodies.refreshResponse(), MediaType.APPLICATION_JSON));
		
		
		tkn.getNewToken("randomcode");
		assertTrue(tkn.refresh());
		assertFalse(tkn.refresh());
		
		
		
	}

}
