package com.github.barmiro.demo_data_generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.barmiro.demo_data_generator.dto.FilterStreamDTO;
import com.github.barmiro.demo_data_generator.dto.StreamDTO;

@RestController
public class ConvertController {

	
	List<StreamDTO> rawStreams = new ArrayList<>();
	Map<String, String> idSwitchMap = new HashMap<>();
	
	@PostMapping("/json")
	int jsonToList(@RequestBody List<StreamDTO> streams) {
		rawStreams.addAll(streams);
		return rawStreams.size();
	}
	
	@PostMapping("/convert")
	List<StreamDTO> convertStreams(@RequestBody List<String> newIDs) {
		int index = 0;
		List<StreamDTO> convertedStreams = new ArrayList<>();
		for (StreamDTO rawStream:rawStreams) {
			if (!idSwitchMap.containsKey(rawStream.spotify_track_uri()) && index < newIDs.size()) {
				idSwitchMap.put(rawStream.spotify_track_uri(), "spotify:track:" + newIDs.get(index));
				index++;
			}
		}
		for (StreamDTO rawStream:rawStreams) {
			convertedStreams.add(
					new StreamDTO(
							rawStream.ts(),
							rawStream.ms_played(),
							idSwitchMap.get(rawStream.spotify_track_uri())
							)
					);
		}
		
		System.out.println(index);
		return convertedStreams;
	}
	
	@PostMapping("/filter")
	List<StreamDTO> filterStreams(@RequestBody List<FilterStreamDTO> unfilteredStreams) {
		List<StreamDTO> filteredStreams = new ArrayList<>();
		
		for (FilterStreamDTO stream:unfilteredStreams) {
			if (!stream.conn_country().equals("A1") 
					&& !stream.conn_country().equals("ZZ")) {
				filteredStreams.add(
						new StreamDTO(
								stream.ts(),
								stream.ms_played(),
								stream.spotify_track_uri()
						)
				);
			}
		}
		
		return filteredStreams;
		
	}
	
	
}
