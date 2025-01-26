package com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.album;

import java.util.List;

import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.album.images.AlbumImage;

public record ApiTrackAlbum(
		String id,
		String name,
		Integer total_tracks,
		String release_date,
		List<AlbumImage> images) {

}
