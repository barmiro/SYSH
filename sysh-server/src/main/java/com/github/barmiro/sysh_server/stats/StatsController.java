package com.github.barmiro.sysh_server.stats;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.barmiro.sysh_server.common.records.OffsetDateTimeRange;
import com.github.barmiro.sysh_server.common.utils.TimeUtils;
import com.github.barmiro.sysh_server.users.SyshUserRepository;

@RestController
@RequestMapping("/stats")
public class StatsController {

	StatsRepository statsRepo;
	StatsCache statsCache;
	SyshUserRepository userRepository;
	
	StatsController(StatsRepository statsRepo,
			StatsCache statsCache, 
			SyshUserRepository userRepository) {
		this.statsRepo = statsRepo;
		this.statsCache = statsCache;
		this.userRepository = userRepository;
	}
	
	@GetMapping("/all")
	FullStats statsAll() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		return statsRepo.streamStats(username, true);
	}
	
	
	@GetMapping("/series")
	List<StatsForRange> series(
			@RequestParam(required = false)
			Optional<LocalDateTime> start,
			@RequestParam(required = false)
			Optional<LocalDateTime> end,
			@RequestParam
			String step) {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		ZoneId userTimeZone = userRepository.getUserTimezone(username);
		
		ZonedDateTime startValue = start
				.map(startInput -> startInput.atZone(userTimeZone))
				.orElse(startup());
		
		ZonedDateTime endValue = end
				.map(endInput -> endInput.atZone(userTimeZone))
				.orElse(Instant.now().atZone(userTimeZone));
	
		List<OffsetDateTimeRange> ranges = TimeUtils.generateOffsetDateTimeRangeSeries(startValue, endValue, step);

		List<StatsForRange> statsList = new ArrayList<>();
		
		for (OffsetDateTimeRange range:ranges) {
			statsList.add(statsRepo.streamStats(
					range.start(),
					range.end(),
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
			LocalDateTime start,
			@RequestParam
			LocalDateTime end) throws IllegalAccessException, InvocationTargetException {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		ZoneId userTimeZone = userRepository.getUserTimezone(username);
		OffsetDateTime startDate = start.atZone(userTimeZone).toOffsetDateTime();
		OffsetDateTime endDate = end.atZone(userTimeZone).toOffsetDateTime();
		
		statsRepo.addCachedStats(startDate, endDate, username);
		
		return statsRepo.streamStats(startDate, endDate, false, username);
	}
	
	@GetMapping("/year/{year}")
	StatsForRange yearStats(@PathVariable Integer year) {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		ZoneId userTimeZone = userRepository.getUserTimezone(username);
		
		
		OffsetDateTime startDate = ZonedDateTime.of( year , 1, 1, 0, 0, 0, 0, userTimeZone).toOffsetDateTime();
		OffsetDateTime endDate = ZonedDateTime.of(year + 1, 1, 1, 0, 0, 0, 0, userTimeZone).minusSeconds(1).toOffsetDateTime();
		
		return statsRepo.streamStats(startDate, endDate, true, username);
	}
	

	@GetMapping("/startup")
	ZonedDateTime startup() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		ZoneId userTimeZone = userRepository.getUserTimezone(username);

//		statsCache.cacheGenerator(username);

		return statsRepo.getFirstStreamInstant(username).orElse(Instant.now()).atZone(userTimeZone);
	}

}
