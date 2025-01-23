package com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks;

import java.util.List;

import com.github.barmiro.sysh_server.catalog.interfaces.ApiEntity;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.album.ApiTrackAlbum;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.artists.ApiTrackArtist;

public record ApiTrack(
		String id,
		String name,
		ApiTrackAlbum album,
		List<ApiTrackArtist> artists,
		Integer duration_ms,
		Integer disc_number,
		Integer track_number
		) implements ApiEntity {

}
