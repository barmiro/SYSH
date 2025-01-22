package com.github.barmiro.sysh_server.catalog.streams;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StreamController {
	
	private final StreamService streamService;

	
	StreamController(StreamService streamService) {
		this.streamService = streamService;
	}
	
	
	@GetMapping("/getStreams")
	List<Stream> getStreams() {
		return streamService.findAll();
	}
	
}
