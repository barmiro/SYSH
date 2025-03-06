package com.github.barmiro.sysh_server.dataintake.recent;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.github.barmiro.sysh_server.catalog.AddToCatalog;
import com.github.barmiro.sysh_server.catalog.streams.SongStream;
import com.github.barmiro.sysh_server.catalog.streams.StreamRepository;
import com.github.barmiro.sysh_server.common.utils.ConvertDTOs;
import com.github.barmiro.sysh_server.spotifyauthorization.SpotifyTokenService;

@RestController
public class RecentController {
	
	SpotifyTokenService tkn;
	RestClient apiClient;
	StreamRepository streamService;
	AddToCatalog addToCatalog;
	
	public RecentController(SpotifyTokenService tkn,
			RestClient apiClient,
			StreamRepository streamService,
			AddToCatalog addToCatalog) {
		this.tkn = tkn;
		this.apiClient = apiClient;
		this.streamService = streamService;
		this.addToCatalog = addToCatalog;
	}
	


	@GetMapping("/recent")
	public String recent() {
		
		tkn.refresh();
		
		ResponseEntity<String> response = apiClient
				.get()
				.uri("me/player/recently-played?limit=50")
				.header("Authorization", "Bearer " + tkn.getToken())
				.retrieve()
				.toEntity(String.class);
		

		
		List<SongStream> previous = streamService.find(50);
		List<SongStream> streams = ConvertDTOs.streamsRecent(response, previous);

		String result;

		result = addToCatalog.adder(streams);

		return result;
	}
}
