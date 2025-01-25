package com.github.barmiro.sysh_server.catalog.albums;

import com.github.barmiro.sysh_server.catalog.interfaces.CatalogEntity;

public record Album(
		String id,
		String name,
		Integer total_tracks,
		String release_date,
		String image_url,
		String thumbnail_url
		) implements CatalogEntity {
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String getName() {
		return name;
	}

}
