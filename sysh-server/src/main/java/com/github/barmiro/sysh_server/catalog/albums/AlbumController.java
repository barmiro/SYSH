package com.github.barmiro.sysh_server.catalog.albums;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AlbumController {
	
	private final AlbumRepository albumRepository;
	
	public AlbumController (AlbumRepository albumRepository) {
		this.albumRepository = albumRepository;
	}
	
	
	@GetMapping("/getAlbums")
	List<Album> getAlbums() {
		return albumRepository.allAlbums();
	}

}
