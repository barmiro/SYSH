package com.github.barmiro.sysh_server.dataintake.recent.dto;

import java.util.List;

import com.github.barmiro.sysh_server.dataintake.recent.dto.recentstream.RecentStream;

public record ItemsWrapper(
		List<RecentStream> items
){
}
