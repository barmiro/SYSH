package com.github.barmiro.sysh_server.integration.recent.dto;

import java.util.List;

import com.github.barmiro.sysh_server.integration.recent.dto.recentstream.RecentStream;

public record ItemsWrapper(
		List<RecentStream> items
){
}
