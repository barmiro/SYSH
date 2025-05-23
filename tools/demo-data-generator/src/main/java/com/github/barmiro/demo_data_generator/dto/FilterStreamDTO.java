package com.github.barmiro.demo_data_generator.dto;

import java.time.OffsetDateTime;

public record FilterStreamDTO(
		OffsetDateTime ts,
		Integer ms_played,
		String spotify_track_uri,
		String conn_country
		) {

}
