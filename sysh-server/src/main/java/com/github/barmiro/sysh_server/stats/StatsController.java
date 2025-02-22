package com.github.barmiro.sysh_server.stats;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
public class StatsController {

	StatsRepository statsRepo;
	
	StatsController(StatsRepository statsRepo) {
		this.statsRepo = statsRepo;
	}
	
	@GetMapping("/all")
	FullStats statsAll() {
		return statsRepo.streamStats(true);
	}
	
	@GetMapping("/range")
	StatsForRange stats(
			@RequestParam
			String start,
			@RequestParam
			String end) {
		
		Timestamp startDate = Timestamp.valueOf(start.replace("T", " "));
		Timestamp endDate = Timestamp.valueOf(end.replace("T", " "));
		
		try {
			System.out.println(statsRepo.addCachedStats(startDate, endDate));
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return statsRepo.streamStats(startDate, endDate, true);
	}
	
	@GetMapping("/year/{year}")
	StatsForRange yearStats(@PathVariable Integer year) {
		Timestamp startDate = Timestamp.valueOf(year + "-01-01 00:00:00");
		Timestamp endDate = Timestamp.valueOf(year + "-12-31 23:59:59");
		return statsRepo.streamStats(startDate, endDate, true);
	}
	
//	TODO: This will have to be changed, but this way it'll work for now
	@GetMapping("/startup")
	Timestamp startup() {
		return statsRepo.getFirstStreamDate().orElse(Timestamp.valueOf(LocalDateTime.now()));
	}

}
