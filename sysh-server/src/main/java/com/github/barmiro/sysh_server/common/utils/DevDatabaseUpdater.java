package com.github.barmiro.sysh_server.common.utils;

import java.sql.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;

@Service
public class DevDatabaseUpdater {

	JdbcClient jdbc;
	public DevDatabaseUpdater(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}
	
	private static final Logger log = LoggerFactory.getLogger(DevDatabaseUpdater.class);
	
	
	@Value("${test.env:false}")
	private boolean isTestEnv;
	
	@PostConstruct
	@Transactional
	public void updateDatabase() {
		if (!isTestEnv) {
			
//			!!! CHANGE THIS WHEN ADDING A NEW UPDATE !!!
			final String TARGET_DATABASE_VERSION = "0.0.1";
			
			updateLegacyDatabase();
			
			String currentVersion = getDBVersion();
			System.out.println(currentVersion);
			
			if (!currentVersion.equals(TARGET_DATABASE_VERSION)) {
				log.info("Updating database from version "
						+ currentVersion
						+ " to version "
						+ TARGET_DATABASE_VERSION
						+ "...");
				
				updater("0.0.0",
						"0.0.1",
						"ALTER TABLE Users ADD COLUMN has_imported_data BOOLEAN DEFAULT false; "
						+ "UPDATE Users SET timezone = 'UTC' WHERE timezone IS NULL; "
						+ "ALTER TABLE Users ALTER COLUMN timezone SET NOT NULL;"
						);
				
				
			} else {
				log.info("Database is up to date (v"
						+ currentVersion
						+ ")");
			}
		}
	}
	
	void updater(String expectedVersion,
			String targetVersion,
			String sql) {
		
		String currentVersion = getDBVersion();
		
		if (currentVersion.equals(expectedVersion)) {
			int updated = jdbc.sql(sql
					+ "UPDATE db_info SET version = :targetVersion WHERE id = true;")
					.param("targetVersion", targetVersion, Types.VARCHAR)
					.update();
			if (updated > 0) {
				log.info("Updated database from v"
						+ currentVersion
						+ " to v"
						+ targetVersion);
			} else {
				log.error("Database update from version "
						+ currentVersion
						+ " to version "
						+ targetVersion
						+ " failed");
			}
		}
	}
	
	private String getDBVersion() {
		return jdbc
				.sql("SELECT version FROM db_info LIMIT 1")
				.query(String.class)
				.single();
	}
	
	private void updateLegacyDatabase() {
		int updated = jdbc.sql("CREATE TABLE IF NOT EXISTS db_info ("
					+ "id BOOLEAN PRIMARY KEY DEFAULT TRUE CHECK (id),"
					+ "version VARCHAR NOT NULL);"
					+ "INSERT INTO db_info (version) VALUES ('0.0.0') "
					+ "ON CONFLICT (id) DO NOTHING;")
		.update();
		
		if (updated > 0) {
			log.info("Updated database from legacy to numbered versioning system");
		}
	}
}
