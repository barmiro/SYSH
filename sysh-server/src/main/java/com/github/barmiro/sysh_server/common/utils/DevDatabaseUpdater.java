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
	
	
//	@Value("${test.env:false}")
//	private boolean isTestEnv;
//	
//	@PostConstruct
//	public void updateDatabase() {
////		this is a dev-environment database updater, so that I don't have to reinitialize the database
//		if (!isTestEnv) {
//			String sql = ("ALTER TABLE Top_Albums_Cache ADD COLUMN image_url VARCHAR; ALTER TABLE Top_Tracks_Cache ADD COLUMN image_url VARCHAR;"
//					);
//			jdbc.sql(sql)
//			.update();			
//		}
//	}
}
