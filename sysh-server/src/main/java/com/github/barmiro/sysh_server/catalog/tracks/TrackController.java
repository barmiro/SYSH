package com.github.barmiro.sysh_server.catalog.tracks;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	List<TrackStats> topTracksNew(
			@RequestParam(required = false)
			Optional<String> startDate,
			@RequestParam(required = false)
			Optional<String> endDate) {
		
		return trackRepository.topTracksNew(startDate, endDate);
	}
	
	@GetMapping("/topTracksTime")
	List<TrackStats> topTracksTime() {
		return trackRepository.topTracksTime();
	}
	
}
