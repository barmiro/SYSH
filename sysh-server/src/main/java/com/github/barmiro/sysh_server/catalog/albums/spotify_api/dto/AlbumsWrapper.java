package com.github.barmiro.sysh_server.catalog.albums.spotify_api.dto;

import java.util.List;

import com.github.barmiro.sysh_server.catalog.albums.spotify_api.dto.albums.ApiAlbum;
import com.github.barmiro.sysh_server.catalog.tracks.ApiWrapper;

public record AlbumsWrapper (
		List<ApiAlbum> albums
		) implements ApiWrapper<ApiAlbum> {
	
	@Override
	public List<ApiAlbum> unwrap() {
		return albums;
	}
}
