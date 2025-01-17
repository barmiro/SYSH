package com.github.barmiro.sysh_server.streams;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	
	@DeleteMapping("/wipe")
	String wipe() {
		return streamService.wipe() + " rows affected.\n";
	}
}
