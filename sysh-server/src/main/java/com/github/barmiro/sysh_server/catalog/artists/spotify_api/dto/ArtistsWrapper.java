package com.github.barmiro.sysh_server.catalog.artists.spotify_api.dto;

import java.util.List;

import com.github.barmiro.sysh_server.catalog.artists.spotify_api.dto.artists.ApiArtist;
import com.github.barmiro.sysh_server.catalog.interfaces.ApiWrapper;

public record ArtistsWrapper(
		List<ApiArtist> artists
		) implements ApiWrapper<ApiArtist> {
	
	@Override
	public List<ApiArtist> unwrap() {
		return artists;
	}

}
