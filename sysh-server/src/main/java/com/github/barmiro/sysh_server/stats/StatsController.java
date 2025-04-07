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

import com.github.barmiro.sysh_server.catalog.albums.AlbumRepository;
import com.github.barmiro.sysh_server.catalog.albums.AlbumStats;
import com.github.barmiro.sysh_server.catalog.artists.ArtistRepository;
import com.github.barmiro.sysh_server.catalog.artists.ArtistStats;
import com.github.barmiro.sysh_server.catalog.tracks.TrackRepository;
import com.github.barmiro.sysh_server.catalog.tracks.TrackStats;
import com.github.barmiro.sysh_server.common.records.OffsetDateTimeRange;
import com.github.barmiro.sysh_server.common.utils.TimeUtils;
import com.github.barmiro.sysh_server.stats.dto.FirstStreamDateDTO;
import com.github.barmiro.sysh_server.stats.dto.FullStats;
import com.github.barmiro.sysh_server.stats.dto.HomeStats;
import com.github.barmiro.sysh_server.stats.dto.HourlyStatsDTO;
import com.github.barmiro.sysh_server.stats.dto.StatsForRange;
import com.github.barmiro.sysh_server.stats.dto.StatsSeriesChunk;
import com.github.barmiro.sysh_server.stats.dto.StreamsMinutesPair;
import com.github.barmiro.sysh_server.users.SyshUserRepository;

@RestController
@RequestMapping("/stats")
public class StatsController {

	StatsRepository statsRepo;
	ArtistRepository artistRepo;
	AlbumRepository albumRepo;
	TrackRepository trackRepo;
	StatsCache statsCache;
	SyshUserRepository userRepository;
	
	StatsController(StatsRepository statsRepo,
			ArtistRepository artistRepo,
			AlbumRepository albumRepo,
			TrackRepository trackRepo,
			StatsCache statsCache, 
			SyshUserRepository userRepository) {
		this.statsRepo = statsRepo;
		this.artistRepo = artistRepo;
		this.albumRepo = albumRepo;
		this.trackRepo = trackRepo;
		this.statsCache = statsCache;
		this.userRepository = userRepository;
	}
	
	@GetMapping("/all")
	FullStats statsAll() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		return statsRepo.streamStats(username, true);
	}
	
	@GetMapping("/hourly")
	List<HourlyStatsDTO> hourly(
			@RequestParam(required = false)
			Optional<LocalDateTime> start,
			@RequestParam(required = false)
			Optional<LocalDateTime> end) {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		ZoneId userTimeZone = userRepository.getUserTimezone(username);
		
		OffsetDateTime startDate = start
				.map(startInput -> startInput.atZone(userTimeZone))
				.orElse(startup().firstStreamDate())
				.toOffsetDateTime();
		
		OffsetDateTime endDate = end
				.map(endInput -> endInput.atZone(userTimeZone))
				.orElse(Instant.now().atZone(userTimeZone))
				.toOffsetDateTime();
		
		List<HourlyStatsDTO> hourlyStats = statsRepo.getStatsByHour(startDate, endDate, username);
//		System.out.println(hourlyStats);
		return hourlyStats;
		
	}
	
	
	@GetMapping("/series")
	List<StatsSeriesChunk> series(
			@RequestParam(required = false)
			Optional<LocalDateTime> start,
			@RequestParam(required = false)
			Optional<LocalDateTime> end,
			@RequestParam
			Optional<String> step) {
		
		long startTime = System.currentTimeMillis();
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		ZoneId userTimeZone = userRepository.getUserTimezone(username);
		
		ZonedDateTime startValue = start
				.map(startInput -> startInput.atZone(userTimeZone))
				.orElse(startup().firstStreamDate());
		
		ZonedDateTime endValue = end
				.map(endInput -> endInput.atZone(userTimeZone))
				.orElse(Instant.now().atZone(userTimeZone));
		
		System.out.println("START = " + startValue);
		System.out.println("END = " + endValue);
	
		List<OffsetDateTimeRange> ranges = TimeUtils.generateOffsetDateTimeRangeSeries(startValue, endValue, step);

		List<StatsSeriesChunk> statsList = new ArrayList<>();
		
		for (OffsetDateTimeRange range:ranges) {
			statsList.add(statsRepo.streamStatsSeries(
					range.start(),
					range.end(),
					username
				)
			);
		}
		
		long endTime = System.currentTimeMillis();
		long time = (endTime - startTime);
		System.out.println("Time elapsed: " + time);
		
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
	
	@GetMapping("/home")
	HomeStats homeStats(@RequestParam(required = false)
			Optional<String> sort
			) {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		ZoneId userTimeZone = userRepository.getUserTimezone(username);
		
		ZonedDateTime userDateTime = ZonedDateTime.now(userTimeZone);
		Integer year = userDateTime.getYear();
		
		String sortBy = sort.orElse("stream_count");
		
		OffsetDateTime dayStart = userDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0).toOffsetDateTime();
//		could be simpler, but would introduce bugs on daylight savings days
		OffsetDateTime dayEnd = userDateTime.withHour(23).withMinute(59).withSecond(59).withNano(0).toOffsetDateTime();
		
		OffsetDateTime yearStart = ZonedDateTime.of( year , 1, 1, 0, 0, 0, 0, userTimeZone).toOffsetDateTime();
		OffsetDateTime yearEnd = ZonedDateTime.of(year + 1, 1, 1, 0, 0, 0, 0, userTimeZone).minusSeconds(1).toOffsetDateTime();
		
		StreamsMinutesPair streamsMinutesDay = statsRepo.homeStats(dayStart, dayEnd, username);
		StreamsMinutesPair streamsMinutesYear = statsRepo.homeStats(yearStart, yearEnd, username);
		
		ArtistStats topArtist = artistRepo.topArtists(sortBy, yearStart, yearEnd, 0, "1", username).getFirst();
		AlbumStats topAlbum = albumRepo.topAlbums(sortBy, yearStart, yearEnd, 0, "1", username).getFirst();
		TrackStats topTrack = trackRepo.topTracks(sortBy, yearStart, yearEnd, 0, "1", username).getFirst();
		
		return new HomeStats(streamsMinutesDay.minutes(),
				streamsMinutesDay.streams(),
				streamsMinutesYear.minutes(),
				streamsMinutesYear.streams(),
				topArtist,
				topAlbum,
				topTrack);
	}
	

	@GetMapping("/startup")
	FirstStreamDateDTO startup() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		ZoneId userTimeZone = userRepository.getUserTimezone(username);

//		statsCache.cacheGenerator(username);

		return new FirstStreamDateDTO(
				statsRepo.getFirstStreamInstant(username)
				.orElse(Instant.now())
				.atZone(userTimeZone));
	}

}
