package com.github.barmiro.sysh_server.recent;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.barmiro.sysh_server.auth.TokenService;
import com.github.barmiro.sysh_server.recent.dto.ItemsWrapper;
import com.github.barmiro.sysh_server.recent.dto.recentstream.RecentStream;
import com.github.barmiro.sysh_server.streams.Stream;
import com.github.barmiro.sysh_server.streams.StreamService;

@RestController
public class RecentController {
	
	TokenService tkn;
	RestClient recentClient;
	StreamService ss;
	
	public RecentController(TokenService tkn, RestClient recentClient, StreamService ss) {
		this.tkn = tkn;
		this.recentClient = recentClient;
		this.ss = ss;
	}
	
	ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	@GetMapping("/recent")
	public String recent() throws Exception {
		ResponseEntity<String> response = recentClient
				.get()
				.uri("me/player/recently-played")
				.header("Authorization", "Bearer " + tkn.getToken())
				.retrieve()
				.toEntity(String.class);
		
		List<Stream> previous = ss.find(20);
		int streamsAdded = 0;
		
		List<RecentStream> items = objectMapper.readValue(response.getBody(), ItemsWrapper.class).items();
		for (RecentStream item:items) {
			
			Timestamp ts = item.played_at();
			Integer ms_played = item.track().duration_ms();
			String master_metadata_track_name = item.track().name();
			String master_metadata_album_artist_name = item.track().artists().get(0).name();	// THIS IS VERY WRONG, BUT TEMPORARY FOR TESTING
			String master_metadata_album_album_name = item.track().album().name();
			String spotify_track_uri = item.track().uri();
			
			Stream stream = new Stream(ts, ms_played, master_metadata_track_name, master_metadata_album_artist_name, master_metadata_album_album_name, spotify_track_uri);
			if (!previous.contains(stream)) {
				streamsAdded += ss.addNew(stream);				
			}
		}
		return streamsAdded + "streams added.";
	}
}
