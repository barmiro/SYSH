package com.github.barmiro.sysh_server.integration.recent;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.github.barmiro.sysh_server.auth.TokenService;
import com.github.barmiro.sysh_server.catalog.AddToCatalog;
import com.github.barmiro.sysh_server.catalog.streams.Stream;
import com.github.barmiro.sysh_server.catalog.streams.StreamRepository;
import com.github.barmiro.sysh_server.integration.json.ConvertDTOs;

@RestController
public class RecentController {
	
	TokenService tkn;
	RestClient apiClient;
	StreamRepository streamService;
	AddToCatalog addToCatalog;
	
	public RecentController(TokenService tkn,
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
		ResponseEntity<String> response = apiClient
				.get()
				.uri("me/player/recently-played")
				.header("Authorization", "Bearer " + tkn.getToken())
				.retrieve()
				.toEntity(String.class);
		
		List<Stream> previous = streamService.find(20);
		List<Stream> streams = ConvertDTOs.streamsRecent(response, previous);

		String result;

		result = addToCatalog.adder(streams);

		return result;
	}
}
