package com.github.barmiro.sysh_server.catalog.streams;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StreamController {
	
	private final StreamRepository streamRepository;

	
	StreamController(StreamRepository streamRepository) {
		this.streamRepository = streamRepository;
	}
	
	
	@GetMapping("/getStreams")
	List<SongStream> getStreams() {
		return streamRepository.findAll();
	}
	
}
