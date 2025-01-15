package com.example.sysh_server.streams;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StreamController {
	
	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}
}
