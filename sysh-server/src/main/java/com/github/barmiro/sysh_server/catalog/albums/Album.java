package com.github.barmiro.sysh_server.catalog.albums;

import com.github.barmiro.sysh_server.catalog.CatalogEntity;

public record Album(
		String id,
		String name,
		Integer total_tracks,
		String release_date
		) implements CatalogEntity {
	
	@Override
	public String getId() {
		return id;
	}

}
