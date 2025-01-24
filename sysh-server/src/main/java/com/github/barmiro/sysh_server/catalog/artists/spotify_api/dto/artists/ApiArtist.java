package com.github.barmiro.sysh_server.catalog.artists.spotify_api.dto.artists;

import com.github.barmiro.sysh_server.catalog.interfaces.ApiEntity;

public record ApiArtist(
		String id,
		String name
		) implements ApiEntity {

}
