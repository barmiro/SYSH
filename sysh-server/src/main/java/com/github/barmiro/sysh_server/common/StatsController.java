package com.github.barmiro.sysh_server.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.barmiro.sysh_server.common.records.StatsDTO;

@RestController
public class StatsController {

	StatsRepository statsRepo;
	
	StatsController(StatsRepository statsRepo) {
		this.statsRepo = statsRepo;
	}
	
	
	@GetMapping("/stats")
	StatsDTO stats() {
		return statsRepo.streamStats();
	}
}
