package com.github.barmiro.sysh_server.catalog.artists.spotifyapi.dto.artists;

import java.util.List;

import com.github.barmiro.sysh_server.catalog.artists.spotifyapi.dto.artists.images.ArtistImage;
import com.github.barmiro.sysh_server.catalog.interfaces.ApiEntity;

public record ApiArtist(
		String id,
		String name,
		List<ArtistImage> images
		) implements ApiEntity {

}
