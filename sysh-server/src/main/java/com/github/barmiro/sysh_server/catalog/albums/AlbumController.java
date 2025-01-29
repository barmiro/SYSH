package com.github.barmiro.sysh_server.catalog.albums;

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
public class AlbumController {
	
	private final AlbumRepository albumRepository;
	
	public AlbumController (AlbumRepository albumRepository) {
		this.albumRepository = albumRepository;
	}
	
	
	@GetMapping("/getAlbums")
	List<Album> getAlbums() {
		return albumRepository.findAll();
	}
	
	@GetMapping("/albums")
	public List<AlbumStats> topAlbums(
			@RequestParam(required = false)
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
		
		String sortBy = sort.orElse("count");
		if (sortBy.equals("time")) {
			return albumRepository.topAlbumsTime(startDate, endDate);
		} else {
			return albumRepository.topAlbumsCount(startDate, endDate);
		}
	}

}
