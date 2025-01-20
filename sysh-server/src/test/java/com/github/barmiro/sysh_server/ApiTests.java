package com.github.barmiro.sysh_server;

import java.net.http.HttpClient;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import com.github.barmiro.sysh_server.utils.GetRandom;

@SpringBootTest
class ApiTests {
	
	private static String token;
	private final static String clientId = System.getenv("SPOTIFY_CLIENT_ID");
	private final static String clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");
	private final static String base64 = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
	private final static String state = GetRandom.alphaNumeric(16);
	
	
	@BeforeAll
	static void setup() {
		String url = "https://accounts.spotify.com/authorize?"
				+"response_type=code&"
				+"client_id=" + clientId + "&"
				+"scope=user-read-recently-played%20"
				+"user-read-currently-playing%20"
				+"user-read-playback-state%20"
				+"user-modify-playback-state&"
				+"redirect_uri=http://localhost:8080/callback&"
				+"state=" + state;
		
		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("username", System.getenv("SPOTIFY_USER"));
		requestBody.put("password", System.getenv("SPOTIFY_PASS"));
		
		HttpClient httpClient = HttpClient
				.newBuilder()
				.followRedirects(HttpClient.Redirect.NORMAL)
				.build();
		
		 ResponseEntity<String> authClient = RestClient
				.builder()
				.baseUrl(url)
				.requestFactory(new JdkClientHttpRequestFactory(httpClient))
				.build()
				.post()
				.body(requestBody)
				.retrieve()
				.toEntity(String.class);
				
		System.out.println(authClient);
		
		
		
	}
}
