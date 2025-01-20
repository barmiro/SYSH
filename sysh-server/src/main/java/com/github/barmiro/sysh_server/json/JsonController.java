package com.github.barmiro.sysh_server.json;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.TrackApiService;
import com.github.barmiro.sysh_server.streams.Stream;
import com.github.barmiro.sysh_server.streams.StreamService;

@RestController
public class JsonController {
	
	private final StreamService streamService;
	private final TrackApiService trackApiService;
	
	JsonController(StreamService streamService,
			TrackApiService trackApiService) {
		this.streamService = streamService;
		this.trackApiService = trackApiService;
	}
	
	@PostMapping("/addJson")
	String addJson(@RequestBody List<StreamDTO> streamDTOs) {
		long start = System.currentTimeMillis();
		Integer streamsAdded = 0;
		Integer tracksAdded = 0;
		
		for (StreamDTO streamDTO:streamDTOs) {
			if (streamDTO.spotify_track_uri() != null) {
				Stream stream = new Stream(streamDTO.ts(),
						streamDTO.ms_played(),
						streamDTO.spotify_track_uri().replace("spotify:track:", ""));
				streamsAdded += streamService.addNew(stream);
				
				tracksAdded += trackApiService.addNewTracks(stream.spotify_track_id(), false);
			}
		}
		tracksAdded += trackApiService.addNewTracks("", true);
		long end = System.currentTimeMillis();
		long time = (end - start) / 1000;
		System.out.println("Time elapsed: " + time);
		return streamsAdded + " streams added.\n" + tracksAdded + " tracks added or updated.\n";
	}
}
