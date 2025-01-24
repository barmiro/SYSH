package com.github.barmiro.sysh_server.catalog.artists;

import com.github.barmiro.sysh_server.catalog.interfaces.CatalogEntity;

public record Artist(
		String id,
		String name) implements CatalogEntity {
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String getName() {
		return name;
	}
	

}
