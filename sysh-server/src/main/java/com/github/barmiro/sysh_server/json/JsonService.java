package com.github.barmiro.sysh_server.json;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.github.barmiro.sysh_server.streams.Stream;

@Service
public class JsonService {

	
	List<Stream> convertStreamDTOs(List<StreamDTO> streamDTOs) {
		
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
	
}
