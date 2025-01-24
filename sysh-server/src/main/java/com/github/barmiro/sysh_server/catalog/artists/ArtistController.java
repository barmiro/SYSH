package com.github.barmiro.sysh_server.catalog.artists;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

}