package com.github.barmiro.sysh_server.catalog.artists;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
public class ArtistController {
	
	private final ArtistRepository artistRepository;
	SyshUserRepository userRepository;
	
	public ArtistController (ArtistRepository artistRepository, SyshUserRepository userRepository) {
		this.artistRepository = artistRepository;
		this.userRepository = userRepository;
	}

//	TODO: split into separate /all

	@GetMapping("/artists")
	public List<ArtistStats> topArtists(
			@RequestParam
			Optional<String> sort,
			@RequestParam(required = false)
			Optional<LocalDateTime> start,
			@RequestParam(required = false)
			Optional<LocalDateTime> end,
			@RequestParam(required = false)
			Optional<Integer> offset,
			@RequestParam(required = false)
			Optional<String> size) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		
		ZoneId userTimeZone = userRepository.getUserTimezone(username);
		OffsetDateTime startDate = start
				.map(startValue -> startValue.atZone(userTimeZone).toOffsetDateTime())
				.orElse(ZonedDateTime.of(2006, 1, 1, 0, 0, 0, 0, userTimeZone).toOffsetDateTime());
		
		OffsetDateTime endDate = end
				.map(endValue -> endValue.atZone(userTimeZone).toOffsetDateTime())
				.orElse(Instant.now().atZone(userTimeZone).toOffsetDateTime());
		
		Integer offsetValue = offset.orElse(0);
		String sizeString = size.orElse("ALL");
		
		String sortBy = sort.orElse("");
		if (sortBy.equals("time")) {
			return artistRepository.topArtists("total_ms_played", startDate, endDate, offsetValue, sizeString, username);
		} else {
			return artistRepository.topArtists("stream_count", startDate, endDate, offsetValue, sizeString, username);
		} 
		
	}
	
	
}