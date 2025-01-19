package com.github.barmiro.sysh_server.catalog.albums.spotify_api.dto.albums;

import com.github.barmiro.sysh_server.catalog.albums.spotify_api.dto.albums.tracks.AlbumTracksWrapper;

public record ApiAlbum(
		String id,
		String name,
		Integer total_tracks,
		String release_date,
		AlbumTracksWrapper tracks) {

}
