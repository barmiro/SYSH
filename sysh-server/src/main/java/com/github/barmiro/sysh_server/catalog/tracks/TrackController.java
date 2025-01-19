package com.github.barmiro.sysh_server.catalog.tracks;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrackController {

	private final TrackService trackService;
	
	TrackController (TrackService trackService) {
		this.trackService = trackService;
	}
	@GetMapping("/getTracks")
	List<Track> getTracks() {
		return trackService.allTracks();
	}
	
	@GetMapping("/topTracksNew")
	List<TrackStats> topTracksNew() {
		return trackService.topTracksNew();
	}
	
}
