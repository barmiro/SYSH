package com.github.barmiro.sysh_server.catalog.artists;

public record ArtistStats(
		String id,
		String name,
		String thumbnail_url,
		Integer total_ms_played,
		Integer stream_count)  {

}
