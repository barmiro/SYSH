package com.github.barmiro.sysh_server.catalog.albums;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AlbumController {
	
	private final AlbumService albumService;
	
	public AlbumController (AlbumService albumService) {
		this.albumService = albumService;
	}
	
	
	@GetMapping("/getAlbums")
	List<Album> getAlbums() {
		return albumService.allAlbums();
	}

}
