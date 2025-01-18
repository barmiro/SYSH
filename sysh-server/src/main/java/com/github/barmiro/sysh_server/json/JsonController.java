package com.github.barmiro.sysh_server.json;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.barmiro.sysh_server.catalog.tracks.TrackService;
import com.github.barmiro.sysh_server.streams.Stream;
import com.github.barmiro.sysh_server.streams.StreamService;

@RestController
public class JsonController {
	
	private final StreamService streamService;
	private final TrackService trackService;
	
	JsonController(StreamService streamService, TrackService trackService) {
		this.streamService= streamService;
		this.trackService= trackService;
	}
	
	@PostMapping("/addJson")
	String addJson(@RequestBody List<StreamDTO> streamDTOs) {
		Integer streamsAdded = 0;
		Integer tracksAdded = 0;
		
		for (StreamDTO streamDTO:streamDTOs) {
			if (streamDTO.spotify_track_uri() != null) {
				Stream stream = new Stream(streamDTO.ts(),
						streamDTO.ms_played(),
						streamDTO.spotify_track_uri().replace("spotify:track:", ""));
				streamsAdded += streamService.addNew(stream);
				tracksAdded += trackService.addTrack(stream);
			}
		}
		
		return streamsAdded + " streams added.\n" + tracksAdded + " tracks added or updated.\n";
	}
}
