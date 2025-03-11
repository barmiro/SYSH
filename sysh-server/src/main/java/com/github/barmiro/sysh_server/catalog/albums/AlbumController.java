package com.github.barmiro.sysh_server.catalog.albums;

import java.lang.reflect.InvocationTargetException;
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
			@RequestParam
			String start,
			@RequestParam
			String end) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		Timestamp startDate = Timestamp.valueOf(start.replace("T", " "));
		Timestamp endDate = Timestamp.valueOf(end.replace("T", " "));
		
		String sortBy = sort.orElse("count");
		if (sortBy.equals("time")) {
			return albumRepository.topAlbums("total_ms_played", startDate, endDate, username);
		} else {
			return albumRepository.topAlbums("stream_count", startDate, endDate, username);
		}
	}
	
	@GetMapping("/albums/all")
	public List<AlbumStats> topAlbumsAll(
			@RequestParam(required = false)
			Optional<String> sort) throws IllegalAccessException, InvocationTargetException {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		String sortBy = sort.orElse("count");
		if (sortBy.equals("time")) {
			return albumRepository.topAlbums("total_ms_played", username, true);
		} else {
			return albumRepository.topAlbums("stream_count", username, true);
		}
	}

}
