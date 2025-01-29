package com.github.barmiro.sysh_server.common;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.barmiro.sysh_server.common.records.stats.FullStats;

@RestController
public class StatsController {

	StatsRepository statsRepo;
	
	StatsController(StatsRepository statsRepo) {
		this.statsRepo = statsRepo;
	}
	
	
	@GetMapping("/stats")
	FullStats stats(
			@RequestParam(required = false)
			Optional<String> start,
			@RequestParam(required = false)
			Optional<String> end) {
		
		Timestamp startDate = Timestamp.valueOf(start
				.orElse("2000-01-01T00:00:00")
				.replace("T", " "));
		Timestamp endDate = Timestamp.valueOf(end
				.orElse(LocalDateTime.now().toString())
				.replace("T", " "));
		
		return statsRepo.streamStats(startDate, endDate);
	}

}
