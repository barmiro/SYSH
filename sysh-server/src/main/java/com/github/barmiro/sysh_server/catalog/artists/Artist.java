package com.github.barmiro.sysh_server.catalog.artists;

import com.github.barmiro.sysh_server.catalog.interfaces.CatalogEntity;

public record Artist(
		String id,
		String name,
		String image_url,
		String thumbnail_url) implements CatalogEntity {
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getIdFieldName() {
		return "id";
	}

}
