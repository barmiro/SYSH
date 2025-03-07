package com.github.barmiro.sysh_server.stats;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
public class StatsController {

	StatsRepository statsRepo;
	StatsCache statsCache;
	
	StatsController(StatsRepository statsRepo, StatsCache statsCache) {
		this.statsRepo = statsRepo;
		this.statsCache = statsCache;
	}
	
	@GetMapping("/all")
	FullStats statsAll() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		return statsRepo.streamStats(username, true);
	}
	
	@GetMapping("/range")
	StatsForRange stats(
			@RequestParam
			String start,
			@RequestParam
			String end) {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		Timestamp startDate = Timestamp.valueOf(start.replace("T", " "));
		Timestamp endDate = Timestamp.valueOf(end.replace("T", " "));
		
		try {
			System.out.println(statsRepo.addCachedStats(startDate, endDate, username));
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return statsRepo.streamStats(startDate, endDate, true, username);
	}
	
	@GetMapping("/year/{year}")
	StatsForRange yearStats(@PathVariable Integer year) {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		Timestamp startDate = Timestamp.valueOf(year + "-01-01 00:00:00");
		Timestamp endDate = Timestamp.valueOf(year + "-12-31 23:59:59");
		return statsRepo.streamStats(startDate, endDate, true, username);
	}
	
//	TODO: This will have to be changed, but this way it'll work for now
	@GetMapping("/startup")
	Timestamp startup() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		statsCache.cacheGenerator(username);
		return statsRepo.getFirstStreamDate(username).orElse(Timestamp.valueOf(LocalDateTime.now()));
	}

}
