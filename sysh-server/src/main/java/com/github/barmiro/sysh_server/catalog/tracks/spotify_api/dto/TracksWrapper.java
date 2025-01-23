package com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto;

import java.util.List;

import com.github.barmiro.sysh_server.catalog.interfaces.ApiWrapper;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.ApiTrack;

public record TracksWrapper(
		List<ApiTrack> tracks
		) implements ApiWrapper<ApiTrack> {
	
	@Override
	public List<ApiTrack> unwrap() {
		return tracks;
	}
	

}
