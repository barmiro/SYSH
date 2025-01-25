package com.github.barmiro.sysh_server.catalog.tracks;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
	
//	@GetMapping("/topTracksNew")
//	List<TrackStats> topTracksNew(
//			@RequestParam(required = false)
//			Optional<String> startDate,
//			@RequestParam(required = false)
//			Optional<String> endDate) {
//		
//		return trackRepository.topTracksCount(startDate, endDate);
//	}
//	
//	@GetMapping("/topTracksTime")
//	List<TrackStats> topTracksTime() {
//		return trackRepository.topTracksTime();
//	}
	
	@GetMapping("/topTracks")
	List<TrackStats> topTracks(
			@RequestParam
			String sort,
			@RequestParam(required = false)
			Optional<String> startDateOpt,
			@RequestParam(required = false)
			Optional<String> endDateOpt) {
		
		Timestamp startDate = Timestamp.valueOf(startDateOpt
				.orElse("2000-01-01T00:00:00")
				.replace("T", " "));
		Timestamp endDate = Timestamp.valueOf(endDateOpt
				.orElse(LocalDateTime.now().toString())
				.replace("T", " "));
		
		if (sort.equals("time")) {
			return trackRepository.topTracksCount(startDate, endDate);
		} else if (sort.equals("count")) {
			return trackRepository.topTracksTime(startDate, endDate);
		} else {
			throw new InvalidSortParameterException();
		}
		
	}
	
}
