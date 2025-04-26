package com.github.barmiro.demo_data_generator.dto.albums;

import com.github.barmiro.demo_data_generator.dto.albums.tracks.AlbumTracksWrapper;

public record ApiAlbum(
		String id,
		String name,
		Integer total_tracks,
		String release_date,
		AlbumTracksWrapper tracks
		) {
}
