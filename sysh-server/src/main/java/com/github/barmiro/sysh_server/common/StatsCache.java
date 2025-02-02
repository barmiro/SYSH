package com.github.barmiro.sysh_server.common;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

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
		
		Optional<Timestamp> oldestStream = jdbc.sql(
				"SELECT MIN(ts) FROM Streams")
				.query(Timestamp.class)
				.optional();
		
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
}
