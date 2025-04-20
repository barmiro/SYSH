package com.github.barmiro.sysh_server.dataintake.recent;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static final Logger log = LoggerFactory.getLogger(RecentController.class);

	@GetMapping("/recent")
	public String recent()
			throws JsonProcessingException,
			ClassCastException,
			IllegalAccessException,
			InvocationTargetException
	{
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

//		I'll leave it as a string for now, this will be an endpoint and I'll have to decide what it returns later
		result = addToCatalog.adder(streams, username).toString();
		
//		to make sure cache is always regenerated on manual call
		if (streams.size() == 0) {
			addToCatalog.updateCache(username);
		}

		return result;
	}
	
	public Integer recentByUsername(String username)
			throws JsonProcessingException,
			ClassCastException,
			IllegalAccessException,
			InvocationTargetException
	{
		log.info("Fetching recent streams for user " + username);
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
			log.info("No new streams found");
			return 0;
		}

		Integer result;

		result = addToCatalog.adder(streams, username);
		
		return result;
	}
}
