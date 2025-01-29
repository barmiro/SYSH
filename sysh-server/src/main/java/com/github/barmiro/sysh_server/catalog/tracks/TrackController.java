package com.github.barmiro.sysh_server.catalog.tracks;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/top")
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
	
	@GetMapping("/tracks")
	public List<TrackStats> topTracks(
			@RequestParam
			Optional<String> sort,
			@RequestParam(required = false)
			Optional<String> start,
			@RequestParam(required = false)
			Optional<String> end) {
		
		Timestamp startDate = Timestamp.valueOf(start
				.orElse("2000-01-01T00:00:00")
				.replace("T", " "));
		Timestamp endDate = Timestamp.valueOf(end
				.orElse(LocalDateTime.now().toString())
				.replace("T", " "));
		String sortBy = sort.orElse("");
		if (sortBy.equals("time")) {
			return trackRepository.topTracksCount(startDate, endDate);
		} else {
			return trackRepository.topTracksTime(startDate, endDate);
		} 
		
	}
	
}
