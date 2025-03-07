package com.github.barmiro.sysh_server.catalog.tracks;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
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
			@RequestParam
			String start,
			@RequestParam
			String end) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		Timestamp startDate = Timestamp.valueOf(start.replace("T", " "));
		Timestamp endDate = Timestamp.valueOf(end.replace("T", " "));
		
		String sortBy = sort.orElse("");
		if (sortBy.equals("time")) {
			return trackRepository.topTracks("total_ms_played", startDate, endDate, username);
		} else {
			return trackRepository.topTracks("stream_count", startDate, endDate, username);
		} 
	}
	
	@GetMapping("/tracks/all")
	public List<TrackStats> topTracksAll(
			@RequestParam(required = false)
			Optional<String> sort) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		String sortBy = sort.orElse("count");
		if (sortBy.equals("time")) {
			return trackRepository.topTracks("total_ms_played", username, true);
		} else {
			return trackRepository.topTracks("stream_count", username, true);
		}
	}
	
}
