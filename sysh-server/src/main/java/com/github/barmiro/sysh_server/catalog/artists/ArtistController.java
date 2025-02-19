package com.github.barmiro.sysh_server.catalog.artists;

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
public class ArtistController {
	
	private final ArtistRepository artistRepository;
	
	public ArtistController (ArtistRepository artistRepository) {
		this.artistRepository = artistRepository;
	}
	
	
	@GetMapping("/getArtists")
	List<Artist> getArtists() {
		return artistRepository.findAll();
	}

	@GetMapping("/artists")
	public List<ArtistStats> topArtists(
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
			return artistRepository.topArtists("total_ms_played", startDate, endDate);
		} else {
			return artistRepository.topArtists("stream_count", startDate, endDate);
		} 
		
	}
	
	
}