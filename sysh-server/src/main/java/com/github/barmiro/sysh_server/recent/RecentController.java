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
import com.github.barmiro.sysh_server.catalog.tracks.TrackService;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.TrackApiService;
import com.github.barmiro.sysh_server.recent.dto.ItemsWrapper;
import com.github.barmiro.sysh_server.recent.dto.recentstream.RecentStream;
import com.github.barmiro.sysh_server.streams.Stream;
import com.github.barmiro.sysh_server.streams.StreamService;

@RestController
public class RecentController {
	
	TokenService tkn;
	RestClient recentClient;
	StreamService strs;
	TrackService trs;
	TrackApiService trApi;
	
	public RecentController(TokenService tkn,
			RestClient recentClient,
			StreamService strs,
			TrackService trs,
			TrackApiService trApi) {
		this.tkn = tkn;
		this.recentClient = recentClient;
		this.strs = strs;
		this.trs = trs;
		this.trApi = trApi;
	}
	
	ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature
					.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	@GetMapping("/recent")
	public String recent() throws Exception {
		ResponseEntity<String> response = recentClient
				.get()
				.uri("me/player/recently-played")
				.header("Authorization", "Bearer " + tkn.getToken())
				.retrieve()
				.toEntity(String.class);
		
		List<Stream> previous = strs.find(20);
		int streamsAdded = 0;
		int tracksAdded = 0;
		
		List<RecentStream> items = objectMapper
				.readValue(response.getBody(), ItemsWrapper.class)
				.items();
		
		for (RecentStream item:items) {
			
			Timestamp ts = item.played_at();
			Integer ms_played = item.track().duration_ms();
			String spotify_track_id = item.track().uri().replace("spotify:track:", "");
			
			Stream stream = new Stream(ts, ms_played, spotify_track_id);
			if (!previous.contains(stream)) {
				streamsAdded += strs.addNew(stream);
				tracksAdded += trApi.addNewTracks(stream.spotify_track_id());
			}
		}
		return streamsAdded + " streams added.\n" + tracksAdded + " new tracks added.";
	}
}
