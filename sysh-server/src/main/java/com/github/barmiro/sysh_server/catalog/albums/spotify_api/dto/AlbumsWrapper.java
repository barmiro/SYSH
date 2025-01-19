package com.github.barmiro.sysh_server.catalog.albums.spotify_api.dto;

import java.util.List;

import com.github.barmiro.sysh_server.catalog.albums.spotify_api.dto.albums.ApiAlbum;

public record AlbumsWrapper(
		List<ApiAlbum> albums) {

}
