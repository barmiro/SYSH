package com.github.barmiro.sysh_server.catalog.albums.spotify_api.dto.albums;

import java.util.List;

import com.github.barmiro.sysh_server.catalog.albums.spotify_api.dto.albums.images.AlbumImage;
import com.github.barmiro.sysh_server.catalog.albums.spotify_api.dto.albums.tracks.AlbumTracksWrapper;
import com.github.barmiro.sysh_server.catalog.interfaces.ApiEntity;

public record ApiAlbum(
		String id,
		String name,
		Integer total_tracks,
		String release_date,
		AlbumTracksWrapper tracks,
		List<AlbumImage> images
		) implements ApiEntity {
}
