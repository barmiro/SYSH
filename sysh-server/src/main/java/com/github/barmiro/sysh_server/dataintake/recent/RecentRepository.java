package com.github.barmiro.sysh_server.dataintake.recent;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.barmiro.sysh_server.catalog.streams.Stream;
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
	
	public List<Stream> convertRecentStreams(ResponseEntity<String> response) {
		
		List<RecentStream> items;
		try {
			items = objectMapper
					.readValue(response.getBody(), ItemsWrapper.class)
					.items();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new ArrayList<Stream>();
		}
		
		List<Stream> streams = new ArrayList<>();
		
		List<Stream> previous = streamRepository.find(20);
		
		for (RecentStream item:items) {
			
			Stream stream = new Stream(
					item.played_at(),
					item.track().duration_ms(),
					item.track().id());
			
			if (!previous.contains(stream)) {
				streams.add(stream);
			}
		}
		
		return streams;
		
	}
}
