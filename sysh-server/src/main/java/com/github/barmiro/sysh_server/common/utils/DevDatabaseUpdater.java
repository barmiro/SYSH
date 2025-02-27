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
	
//	@PostConstruct
//	public void updateDatabase() {
////		this is a dev-environment database updater, so that I don't have to reinitialize the database
//		if (!isTestEnv) {
//			jdbc.sql("CREATE TABLE IF NOT EXISTS User_Data ("
//					+ "id SERIAL PRIMARY KEY,"
//					+ "display_name VARCHAR,"
//					+ "CONSTRAINT only_one_user_data CHECK (id = 1)"
//					+ "); "
//					+ "INSERT INTO User_Data(display_name) VALUES ('username unknown') ON CONFLICT DO NOTHING;")
//			.update();			
//		}
//	}
}
