package com.github.barmiro.sysh_server.catalog.albums.spotifyapideprecated.dto;

import java.util.List;

import com.github.barmiro.sysh_server.catalog.albums.spotifyapideprecated.dto.albums.ApiAlbum;
import com.github.barmiro.sysh_server.catalog.interfaces.ApiWrapper;

public record AlbumsWrapper (
		List<ApiAlbum> albums
		) implements ApiWrapper<ApiAlbum> {
	
	@Override
	public List<ApiAlbum> unwrap() {
		return albums;
	}
}
