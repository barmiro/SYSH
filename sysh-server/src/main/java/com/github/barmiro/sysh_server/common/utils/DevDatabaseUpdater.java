package com.github.barmiro.sysh_server.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class DevDatabaseUpdater {

	JdbcClient jdbc;
	public DevDatabaseUpdater(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}
	
	
	@Value("${test.env:false}")
	private boolean isTestEnv;
	
	@PostConstruct
	public void updateDatabase() {
//		this is a dev-environment database updater, so that I don't have to reinitialize the database
		if (!isTestEnv) {
			String sql = ("ALTER TABLE ONLY Stats_Cache_Full "
					+ "ALTER COLUMN minutes_streamed SET DEFAULT 0,"
					+ "ALTER COLUMN stream_count SET DEFAULT 0,"
					+ "ALTER COLUMN track_count SET DEFAULT 0,"
					+ "ALTER COLUMN album_count SET DEFAULT 0,"
					+ "ALTER COLUMN artist_count SET DEFAULT 0;"
					);
			jdbc.sql(sql)
			.update();			
		}
	}
}
