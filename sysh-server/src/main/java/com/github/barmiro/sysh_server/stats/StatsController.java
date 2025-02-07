package com.github.barmiro.sysh_server.stats;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
		
//		6 years before Spotify was founded, safe minimum
		Timestamp startDate = Timestamp.valueOf(start
				.orElse("2000-01-01T00:00:00")
				.replace("T", " "));
		
//		This will be the placeholder for the upper limit for now;
//		considered 2100, but I'm erring on the side of caution
//		in terms of 32-bit epoch integer overflow
		Timestamp endDate = Timestamp.valueOf(end
				.orElse("2038-01-01T00:00:00")
				.replace("T", " "));
		

		try {
			System.out.println(statsRepo.addCachedStats(startDate, endDate));
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return statsRepo.streamStats(startDate, endDate);
	}
	
	@GetMapping("/stats/year/{year}")
	FullStats yearStats(@PathVariable Integer year) {
		Timestamp startDate = Timestamp.valueOf(year + "-01-01 00:00:00");
		Timestamp endDate = Timestamp.valueOf(year + "-12-31 23:59:59");
		return statsRepo.streamStats(startDate, endDate);
	}

}
