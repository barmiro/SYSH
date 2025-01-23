package com.github.barmiro.sysh_server.catalog.albums.spotify_api.dto.albums;

import com.github.barmiro.sysh_server.catalog.albums.spotify_api.dto.albums.tracks.AlbumTracksWrapper;
import com.github.barmiro.sysh_server.catalog.interfaces.ApiEntity;

public record ApiAlbum(
		String id,
		String name,
		Integer total_tracks,
		String release_date,
		AlbumTracksWrapper tracks
		) implements ApiEntity {
}
