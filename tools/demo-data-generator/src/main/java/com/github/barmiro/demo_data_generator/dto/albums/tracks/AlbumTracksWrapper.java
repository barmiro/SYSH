package com.github.barmiro.demo_data_generator.dto.albums.tracks;

import java.util.List;

import com.github.barmiro.demo_data_generator.dto.albums.tracks.items.ApiAlbumTrack;

public record AlbumTracksWrapper(
		List<ApiAlbumTrack> items,
		Integer limit) {

}
