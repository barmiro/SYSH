package com.github.barmiro.sysh_server.stats;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.barmiro.sysh_server.common.records.TimestampRange;
import com.github.barmiro.sysh_server.common.utils.TimeUtils;

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
	
	
	@GetMapping("/series")
	List<StatsForRange> series(
			@RequestParam(required = false)
			Optional<String> start,
			@RequestParam(required = false)
			Optional<String> end,
			@RequestParam
			String step) {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
//		TODO: this is bad, but will have to stay for consistency; I'll have to deal with this when overhauling timezone behaviour
		LocalDateTime startValue = start
				.map(startString -> Timestamp.valueOf(startString.replace("T", " ")).toLocalDateTime())
				.orElse(startup().toLocalDateTime());
		
		LocalDateTime endValue = end
				.map(endString -> Timestamp.valueOf(endString.replace("T", " ")).toLocalDateTime())
				.orElse(LocalDateTime.now());
		
		
		List<TimestampRange> ranges = TimeUtils.generateDateRangeSeries(startValue, endValue, step);
		List<StatsForRange> statsList = new ArrayList<>();
		
		for (TimestampRange range:ranges) {
			statsList.add(statsRepo.streamStats(
					range.startTimestamp(),
					range.endTimestamp(),
					false,
					username
				)
			);
		}
		
		return statsList;
	}
	
	
	@GetMapping("/range")
	StatsForRange stats(
			@RequestParam
			String start,
			@RequestParam
			String end) throws IllegalAccessException, InvocationTargetException {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		Timestamp startDate = Timestamp.valueOf(start.replace("T", " "));
		Timestamp endDate = Timestamp.valueOf(end.replace("T", " "));
		
		
		statsRepo.addCachedStats(startDate, endDate, username);
		
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

//		statsCache.cacheGenerator(username);
		return statsRepo.getFirstStreamDate(username).orElse(Timestamp.valueOf(LocalDateTime.now()));
	}

}
