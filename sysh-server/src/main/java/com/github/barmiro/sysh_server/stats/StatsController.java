package com.github.barmiro.sysh_server.stats;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;

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

		//6 years before Spotify was founded, safe minimum
		Timestamp startDate = Timestamp.valueOf("2000-01-01 00:00:00");
		
		//This will be the placeholder for the upper limit for now;
		//considered 2100, but I'm erring on the side of caution
		//in terms of 32-bit epoch integer overflow
		Timestamp endDate = Timestamp.valueOf("2038-01-01 00:00:00");
		
		//Might change to direct cache fetch? keeping as is for now to keep it abstracted away
		return statsRepo.streamStats(startDate, endDate);
	}
	
	@GetMapping("/range")
	FullStats stats(
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
		return statsRepo.streamStats(startDate, endDate);
	}
	
	@GetMapping("/year/{year}")
	FullStats yearStats(@PathVariable Integer year) {
		Timestamp startDate = Timestamp.valueOf(year + "-01-01 00:00:00");
		Timestamp endDate = Timestamp.valueOf(year + "-12-31 23:59:59");
		return statsRepo.streamStats(startDate, endDate);
	}

}
