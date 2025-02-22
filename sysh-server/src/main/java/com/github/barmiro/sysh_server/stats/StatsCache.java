package com.github.barmiro.sysh_server.stats;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import com.github.barmiro.sysh_server.catalog.streams.SongStream;

import jakarta.annotation.PostConstruct;

@Service
public class StatsCache {

	JdbcClient jdbc;
	StatsRepository statsRepo;
	
	StatsCache(JdbcClient jdbc, StatsRepository statsRepo) {
		this.jdbc = jdbc;
		this.statsRepo = statsRepo;
	}
	
	private static final Logger log = LoggerFactory.getLogger(StatsCache.class);
	
	@Value("${test.env:false}")
	private boolean isTestEnv;
	
	@PostConstruct
	void cacheGenerator() {
		
		if (isTestEnv) {
			return;
		}
		
		Optional<Timestamp> oldestStream = statsRepo.getFirstStreamDate();
		
		if(oldestStream.isEmpty()) {
			log.error("Cache generator didn't find any streams.");
			return;
		}
		
		int startYear = oldestStream.get()
				.toLocalDateTime()
				.getYear();
		
		int endYear = LocalDateTime.now()
				.getYear();
		
		int addedYears = 0;
		try {
			statsRepo.addCachedStats();
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = startYear; i <= endYear; i++) {
			Timestamp start = Timestamp.valueOf(i + "-01-01 00:00:00");
			Timestamp end = Timestamp.valueOf(i + "-12-31 23:59:59");
			try {
				addedYears += statsRepo.addCachedStats(start, end);
			} catch (IllegalAccessException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		log.info("Added " 
		+ addedYears 
		+ " years in range " 
		+ startYear 
		+ " - " 
		+ endYear);
		
	}
	
	

	
	public void updateCache(
			List<SongStream> streams,
			int tracksAdded,
			int albumsAdded,
			int artistsAdded) {
		
		int baseYear = streams.get(0).ts()
				.toLocalDateTime()
				.getYear();
		
		
		int milisAdded = 0;
//		this handles the edge case of recent streams being from two different years;
//		introduces unnecessary cache re-gens at the beginning of the year,
//		TODO: rethink this approach
		for (SongStream stream:streams) {
			
			int streamYear = stream.ts()
					.toLocalDateTime()
					.getYear();
			
			if (streamYear != baseYear) {
				cacheGenerator();
				return;
			}
			
			milisAdded += stream.ms_played();
		}
		
		int minutesAdded = milisAdded / 60000;
		
		Timestamp start = Timestamp.valueOf(baseYear + "-01-01 00:00:00");
		Timestamp end = Timestamp.valueOf(baseYear + "-12-31 23:59:59");
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
				+ "WHERE start_date <= :start "
				+ "AND end_date >= :end")
		.param("minutesAdded", minutesAdded, Types.INTEGER)
		.param("streamsAdded", streams.size(), Types.INTEGER)
		.param("tracksAdded", tracksAdded, Types.INTEGER)
		.param("albumsAdded", albumsAdded, Types.INTEGER)
		.param("artistsAdded", artistsAdded, Types.INTEGER)
		.param("start", start, Types.TIMESTAMP)
		.param("end", end, Types.TIMESTAMP)
		.update();
		
		int updatedFullCache = jdbc.sql("UPDATE Stats_Cache_Full "
				+ baseSql
				+ " WHERE id = 1")
		.param("minutesAdded", minutesAdded, Types.INTEGER)
		.param("streamsAdded", streams.size(), Types.INTEGER)
		.param("tracksAdded", tracksAdded, Types.INTEGER)
		.param("albumsAdded", albumsAdded, Types.INTEGER)
		.param("artistsAdded", artistsAdded, Types.INTEGER)
		.update();
		
		log.info(updatedRangeCaches + " range stats caches have been updated.");
		if (updatedFullCache == 1) {
			log.info("Updated all time stats cache");
		} else {
			log.error("Full cache update error, affected rows: " + updatedFullCache);
		}
	}
	
}




