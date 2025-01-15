package com.example.sysh_server.streams;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StreamController {
	
	private final StreamService streamService;
	
	StreamController(StreamService streamService) {
		this.streamService = streamService;
	}
	
	@GetMapping("/hello")
	String hello() {
		return "hello";
	}
	
	@GetMapping("/getStreams")
	List<Stream> getStreams() {
		return streamService.findAll();
	}
	
	@GetMapping("/getTracks")
	List<Track> getTracks() {
		return streamService.allTracks();
	}
	
	@GetMapping("/topTracks")
	List<Track> topTracks() {
		return streamService.topTracks();
	}
	
	@PostMapping("/addJson")
	String addJson(@RequestBody List<Stream> streams) {
		Integer streamsAdded = 0;
		Integer tracksAdded = 0;
		for (Stream stream:streams) {
			if (stream.spotify_track_uri() != null) {
				streamsAdded += streamService.addNew(stream);
				tracksAdded += streamService.addTracks(stream);
			}
		}
		
		return streamsAdded + " streams added.\n" + tracksAdded + " tracks added or updated.\n";
	}
	
	@DeleteMapping("/wipe")
	String wipe() {
		return streamService.wipe() + " rows affected.\n";
	}
}
