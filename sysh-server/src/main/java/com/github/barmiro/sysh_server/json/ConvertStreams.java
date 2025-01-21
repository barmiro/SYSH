package com.github.barmiro.sysh_server.json;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.barmiro.sysh_server.catalog.streams.Stream;
import com.github.barmiro.sysh_server.catalog.streams.StreamService;
import com.github.barmiro.sysh_server.recent.dto.ItemsWrapper;
import com.github.barmiro.sysh_server.recent.dto.recentstream.RecentStream;

public class ConvertStreams {
	
	public static List<Stream> json(List<StreamDTO> streamDTOs) {
		List<Stream> streams = new ArrayList<>();
		
		for (StreamDTO streamDTO:streamDTOs) {
			if (streamDTO.spotify_track_uri() != null) {
				Stream stream = new Stream(streamDTO.ts(),
						streamDTO.ms_played(),
						streamDTO.spotify_track_uri().replace("spotify:track:", ""));
				
				streams.add(stream);
			}
		}
		return streams;
	}
	
	
	public static List<Stream> recent(
			ResponseEntity<String> response,
			List<Stream> previous) {
		ObjectMapper objectMapper = new ObjectMapper()
				.configure(DeserializationFeature
						.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
	
	
//	List<Stream> recent(List<RecentStream> )
	
}
