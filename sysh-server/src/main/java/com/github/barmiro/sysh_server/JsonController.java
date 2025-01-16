package com.github.barmiro.sysh_server;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.barmiro.sysh_server.streams.Stream;
import com.github.barmiro.sysh_server.streams.StreamService;
import com.github.barmiro.sysh_server.tracks.TrackService;

@RestController
public class JsonController {
	
	private final StreamService streamService;
	private final TrackService trackService;
	
	JsonController(StreamService streamService, TrackService trackService) {
		this.streamService= streamService;
		this.trackService= trackService;
	}
	
	@PostMapping("/addJson")
	String addJson(@RequestBody List<Stream> streams) {
		Integer streamsAdded = 0;
		Integer tracksAdded = 0;
		for (Stream stream:streams) {
			if (stream.spotify_track_uri() != null) {
				streamsAdded += streamService.addNew(stream);
				tracksAdded += trackService.addTracks(stream);
			}
		}
		
		return streamsAdded + " streams added.\n" + tracksAdded + " tracks added or updated.\n";
	}
}
