package com.github.barmiro.sysh_server.dataintake.recent;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.barmiro.sysh_server.catalog.AddToCatalog;
import com.github.barmiro.sysh_server.catalog.streams.SongStream;
import com.github.barmiro.sysh_server.catalog.streams.StreamRepository;
import com.github.barmiro.sysh_server.common.utils.ConvertDTOs;
import com.github.barmiro.sysh_server.spotifyauthorization.SpotifyTokenService;

@RestController
public class RecentController {
	
	SpotifyTokenService tkn;
	RestClient apiClient;
	StreamRepository streamRepository;
	AddToCatalog addToCatalog;
	
	public RecentController(SpotifyTokenService tkn,
			RestClient apiClient,
			StreamRepository streamRepository,
			AddToCatalog addToCatalog) {
		this.tkn = tkn;
		this.apiClient = apiClient;
		this.streamRepository = streamRepository;
		this.addToCatalog = addToCatalog;
	}
	


	@GetMapping("/recent")
	public String recent(
			) throws JsonProcessingException, ClassCastException, IllegalAccessException, InvocationTargetException {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		tkn.refresh(username);
		
		ResponseEntity<String> response = apiClient
				.get()
				.uri("me/player/recently-played?limit=50")
				.header("Authorization", "Bearer " + tkn.getToken(username))
				.retrieve()
				.toEntity(String.class);
		


		
		List<SongStream> previous = streamRepository.find(50, username);
		List<SongStream> streams = ConvertDTOs.streamsRecent(username, response, previous);

		String result;

		result = addToCatalog.adder(streams, username);

		return result;
	}
	
	public String recentByUsername(String username) throws JsonProcessingException, ClassCastException, IllegalAccessException, InvocationTargetException {
		
		tkn.refresh(username);
		
		ResponseEntity<String> response = apiClient
				.get()
				.uri("me/player/recently-played?limit=50")
				.header("Authorization", "Bearer " + tkn.getToken(username))
				.retrieve()
				.toEntity(String.class);
	
		
		List<SongStream> previous = streamRepository.find(50, username);
		List<SongStream> streams = ConvertDTOs.streamsRecent(username, response, previous);
		
		if (streams.isEmpty()) {
			return "No new streams found";
		}

		String result;

		result = addToCatalog.adder(streams, username);
		
		

		return result;
	}
}
