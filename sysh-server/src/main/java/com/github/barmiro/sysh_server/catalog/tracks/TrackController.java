package com.github.barmiro.sysh_server.catalog.tracks;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrackController {

	private final TrackRepository trackRepository;
	
	TrackController (TrackRepository trackService) {
		this.trackRepository = trackService;
	}
	@GetMapping("/getTracks")
	List<Track> getTracks() {
		return trackRepository.findAll();
	}
	
	@GetMapping("/topTracksNew")
	List<TrackStats> topTracksNew() {
		return trackRepository.topTracksNew();
	}
	
}
