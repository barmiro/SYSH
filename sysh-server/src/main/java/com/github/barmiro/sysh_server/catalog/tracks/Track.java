package com.github.barmiro.sysh_server.catalog.tracks;

import com.github.barmiro.sysh_server.catalog.interfaces.CatalogEntity;

public record Track(
		String spotify_track_id,
		String name,
		Integer duration_ms,
		String album_id,
		Integer disc_number,
		Integer track_number
		) implements CatalogEntity {
	
	@Override
	public String getId() {
		return spotify_track_id;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getIdFieldName() {
		return "spotify_track_id";
	}

}
