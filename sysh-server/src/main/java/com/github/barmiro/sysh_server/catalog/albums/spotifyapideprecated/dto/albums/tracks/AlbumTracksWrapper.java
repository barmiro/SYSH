package com.github.barmiro.sysh_server.catalog.albums.spotifyapideprecated.dto.albums.tracks;

import java.util.List;

import com.github.barmiro.sysh_server.catalog.albums.spotifyapideprecated.dto.albums.tracks.items.ApiAlbumTrack;

public record AlbumTracksWrapper(
		List<ApiAlbumTrack> items,
		Integer limit) {

}
