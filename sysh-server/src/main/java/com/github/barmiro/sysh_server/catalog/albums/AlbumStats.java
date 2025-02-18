package com.github.barmiro.sysh_server.catalog.albums;

public record AlbumStats(
		String id,
		String name,
		String thumbnail_url,
		String primary_artist_name,
		Integer total_ms_played,
		Integer stream_count) {

}
