package com.github.barmiro.sysh_server.stats;

import java.lang.reflect.InvocationTargetException;
import java.sql.Types;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import com.github.barmiro.sysh_server.catalog.streams.SongStream;
import com.github.barmiro.sysh_server.common.records.OffsetDateTimeRange;
import com.github.barmiro.sysh_server.common.utils.TimeUtils;
import com.github.barmiro.sysh_server.users.SyshUserRepository;

@Service
public class StatsCache {

	JdbcClient jdbc;
	StatsRepository statsRepo;
	SyshUserRepository userRepo;
	
	StatsCache(JdbcClient jdbc, StatsRepository statsRepo, SyshUserRepository userRepo) {
		this.jdbc = jdbc;
		this.statsRepo = statsRepo;
		this.userRepo = userRepo;
	}
	
	private static final Logger log = LoggerFactory.getLogger(StatsCache.class);
	
	@Value("${test.env:false}")
	private boolean isTestEnv;
	
//	@PostConstruct
	public void cacheGenerator(String username) throws IllegalAccessException, InvocationTargetException {
		
		if (isTestEnv) {
			return;
		}
		
		Optional<Instant> oldestStream = statsRepo.getFirstStreamInstant(username);
		ZoneId timeZoneId = userRepo.getUserTimezone(username);
		
		if(oldestStream.isEmpty()) {
			log.error("Cache generator didn't find any streams for user " + username);
			return;
		}
		
		int startYear = oldestStream.get()
				.atZone(timeZoneId)
				.getYear();
		
		int endYear = Instant.now()
				.atZone(timeZoneId)
				.getYear();
		
		int addedYears = 0;
		log.info("Generating full stats cache for user " + username);

		statsRepo.addCachedStats(username);
		log.info("Done");

		log.info("Generating yearly stats cache for user " + username);
		
		
		List<OffsetDateTimeRange> yearRangeList = TimeUtils.generateOffsetDateTimeRangeSeries(
				ZonedDateTime.of(startYear, 1, 1, 0, 0, 0, 0, timeZoneId),
				ZonedDateTime.of(endYear + 1, 1, 1, 0, 0, 0, 0, timeZoneId),
				"year"
		);
		
		for (OffsetDateTimeRange year: yearRangeList) {
			addedYears += statsRepo.addCachedStats(year.start(), year.end(), username);
		}
		
		
		log.info("Added " 
		+ addedYears 
		+ " years in range " 
		+ startYear
		+ " - " 
		+ endYear
		+ " for user "
		+ username);
		
	}
	
	

	
	public void updateCache(
			List<SongStream> streams,
			String username,
			ZoneId timeZoneId,
			int tracksAdded,
			int albumsAdded,
			int artistsAdded) throws IllegalAccessException, InvocationTargetException {
		
		int baseYear = streams.get(0).ts()
				.getYear();
		
		
		int milisAdded = 0;
//		this handles the edge case of recent streams being from two different years;
//		introduces unnecessary cache re-gens at the beginning of the year,
//		TODO: rethink this approach
		for (SongStream stream:streams) {
			
			int streamYear = stream.ts()
					.getYear();
			
			if (streamYear != baseYear) {
				cacheGenerator(username);
				return;
			}
			
			milisAdded += stream.ms_played();
		}
		
		int minutesAdded = milisAdded / 60000;
		
//		Timestamp start = Timestamp.valueOf(baseYear + "-01-01 00:00:00");
//		Timestamp end = Timestamp.valueOf(baseYear + "-12-31 23:59:59");
		
		OffsetDateTime start = ZonedDateTime.of( baseYear , 1, 1, 0, 0, 0, 0, timeZoneId).toOffsetDateTime();
		OffsetDateTime end = ZonedDateTime.of(baseYear + 1, 1, 1, 0, 0, 0, 0, timeZoneId).minusSeconds(1).toOffsetDateTime();
		String baseSql =  ("SET "
				+ "minutes_streamed = minutes_streamed + :minutesAdded,"
				+ "stream_count = stream_count + :streamsAdded,"
				+ "track_count = track_count + :tracksAdded,"
				+ "album_count = album_count + :albumsAdded,"
				+ "artist_count = artist_count + :artistsAdded ");
		
//		this will not add anything to not-yet-existing yearly caches, but that's not an issue;
//		a new yearly cache should be generated after it encounters two different years in a batch,
//		1 out of 50 times the cache won't be generated right away,
//		but you don't really need a cache for the first weeks of a year
		int updatedRangeCaches = jdbc.sql("UPDATE Stats_Cache_Range "
				+ baseSql
				+ "WHERE username = :username "
				+ "AND start_date <= :start "
				+ "AND end_date >= :end")
		.param("minutesAdded", minutesAdded, Types.INTEGER)
		.param("streamsAdded", streams.size(), Types.INTEGER)
		.param("tracksAdded", tracksAdded, Types.INTEGER)
		.param("albumsAdded", albumsAdded, Types.INTEGER)
		.param("artistsAdded", artistsAdded, Types.INTEGER)
		.param("username", username, Types.VARCHAR)
		.param("start", start, Types.TIMESTAMP_WITH_TIMEZONE)
		.param("end", end, Types.TIMESTAMP_WITH_TIMEZONE)
		.update();
		
		int updatedFullCache = jdbc.sql("UPDATE Stats_Cache_Full "
				+ baseSql
				+ " WHERE username = :username")
		.param("minutesAdded", minutesAdded, Types.INTEGER)
		.param("streamsAdded", streams.size(), Types.INTEGER)
		.param("tracksAdded", tracksAdded, Types.INTEGER)
		.param("albumsAdded", albumsAdded, Types.INTEGER)
		.param("artistsAdded", artistsAdded, Types.INTEGER)
		.param("username", username, Types.VARCHAR)
		.update();
		
		log.info(updatedRangeCaches + " range stats caches have been updated for user " + username);
		if (updatedFullCache == 1) {
			log.info("Updated all time stats cache for user " + username);
		} else {
			log.error("Full cache update error, affected rows: " + updatedFullCache);
			cacheGenerator(username);
		}
	}
	
}




