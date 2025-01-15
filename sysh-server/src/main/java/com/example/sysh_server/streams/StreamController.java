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
	
	@PostMapping("/addJson")
	String addJson(@RequestBody List<Stream> streams) {
		Integer rowsAffected = 0;
		for (Stream stream:streams) {
			if (stream.spotify_track_uri() != null) {
				rowsAffected += streamService.addNew(stream);
			}
		}
		
		return rowsAffected + "rows affected.\n";
	}
	
	@DeleteMapping("/wipe")
	String wipe() {
		return streamService.wipe() + "rows affected.\n";
	}
}
