package com.github.barmiro.sysh_server.stats.dto;

public record HourlyStatsDTO(
		Integer hour,
		Integer minutes_streamed) {

}
