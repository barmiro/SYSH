package com.github.barmiro.sysh_server.catalog.albums;

public record Album(
		String id,
		String name,
		Integer total_tracks,
		String release_date) {

}
