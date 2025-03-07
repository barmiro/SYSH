package com.github.barmiro.sysh_server.dataintake.recent;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.barmiro.sysh_server.catalog.streams.SongStream;
import com.github.barmiro.sysh_server.catalog.streams.StreamRepository;
import com.github.barmiro.sysh_server.dataintake.recent.dto.ItemsWrapper;
import com.github.barmiro.sysh_server.dataintake.recent.dto.recentstream.RecentStream;

@Service
public class RecentRepository {

	private final StreamRepository streamRepository;
	
	RecentRepository(StreamRepository streamRepository) {
		this.streamRepository = streamRepository;
	}
	
	ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature
					.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	public List<SongStream> convertRecentStreams(ResponseEntity<String> response, String username) {
		
		List<RecentStream> items;
		try {
			items = objectMapper
					.readValue(response.getBody(), ItemsWrapper.class)
					.items();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new ArrayList<SongStream>();
		}
		
		List<SongStream> streams = new ArrayList<>();
		
		List<SongStream> previous = streamRepository.find(50);
		
		for (RecentStream item:items) {
			
			SongStream stream = new SongStream(
					item.played_at(),
					username,
					item.track().duration_ms(),
					item.track().id());
			
			if (!previous.contains(stream)) {
				streams.add(stream);
			}
		}
		
		return streams;
		
	}
}
