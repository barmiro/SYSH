package com.github.barmiro.sysh_server.catalog.tracks;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.barmiro.sysh_server.users.SyshUserRepository;

@RequestMapping("/top")
@RestController
public class TrackController {

	private final TrackRepository trackRepository;
	SyshUserRepository userRepository;
	
	TrackController (TrackRepository trackRepository, SyshUserRepository userRepository) {
		this.trackRepository = trackRepository;
		this.userRepository = userRepository;
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
			LocalDateTime start,
			@RequestParam
			LocalDateTime end,
			@RequestParam(required = false)
			Optional<Integer> offset,
			@RequestParam(required = false)
			Optional<String> size) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		ZoneId userTimeZone = userRepository.getUserTimezone(username);
		OffsetDateTime startDate = start.atZone(userTimeZone).toOffsetDateTime();
		OffsetDateTime endDate = end.atZone(userTimeZone).toOffsetDateTime();
		
		Integer offsetValue = offset.orElse(0);
		String sizeString = size.orElse("ALL");
		
		String sortBy = sort.orElse("");
		if (sortBy.equals("time")) {
			return trackRepository.topTracks("total_ms_played", startDate, endDate, offsetValue, sizeString, username);
		} else {
			return trackRepository.topTracks("stream_count", startDate, endDate, offsetValue, sizeString, username);
		} 
	}
	
	@GetMapping("/tracks/all")
	public List<TrackStats> topTracksAll(
			@RequestParam(required = false)
			Optional<String> sort,
			@RequestParam(required = false)
			Optional<Integer> offset,
			@RequestParam(required = false)
			Optional<String> size) {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		Integer offsetValue = offset.orElse(0);
		String sizeString = size.orElse("ALL");
		
		String sortBy = sort.orElse("count");
		if (sortBy.equals("time")) {
			return trackRepository.topTracks("total_ms_played", username, offsetValue, sizeString, true);
		} else {
			return trackRepository.topTracks("stream_count", username, offsetValue, sizeString, true);
		}
	}
	
}
