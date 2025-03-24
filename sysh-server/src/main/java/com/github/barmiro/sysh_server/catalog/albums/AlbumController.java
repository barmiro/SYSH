package com.github.barmiro.sysh_server.catalog.albums;

import java.lang.reflect.InvocationTargetException;
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
public class AlbumController {
	
	private final AlbumRepository albumRepository;
	SyshUserRepository userRepository;
	
	public AlbumController (AlbumRepository albumRepository, SyshUserRepository userRepository) {
		this.albumRepository = albumRepository;
		this.userRepository = userRepository;
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
		
		String sortBy = sort.orElse("count");
		if (sortBy.equals("time")) {
			return albumRepository.topAlbums("total_ms_played", startDate, endDate, offsetValue, sizeString, username);
		} else {
			return albumRepository.topAlbums("stream_count", startDate, endDate, offsetValue, sizeString, username);
		}
	}
	
	@GetMapping("/albums/all")
	public List<AlbumStats> topAlbumsAll(
			@RequestParam(required = false)
			Optional<String> sort,
			@RequestParam(required = false)
			Optional<Integer> offset,
			@RequestParam(required = false)
			Optional<String> size) throws IllegalAccessException, InvocationTargetException {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		Integer offsetValue = offset.orElse(0);
		String sizeString = size.orElse("ALL");
		
		String sortBy = sort.orElse("count");
		if (sortBy.equals("time")) {
			return albumRepository.topAlbums("total_ms_played", username, offsetValue, sizeString, true);
		} else {
			return albumRepository.topAlbums("stream_count", username, offsetValue, sizeString, true);
		}
	}

}
