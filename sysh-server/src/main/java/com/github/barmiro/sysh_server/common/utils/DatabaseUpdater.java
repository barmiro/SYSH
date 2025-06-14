package com.github.barmiro.sysh_server.common.utils;

import java.sql.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;

@Service
@Transactional
public class DatabaseUpdater {

	JdbcClient jdbc;
	public DatabaseUpdater(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}
	
	private static final Logger log = LoggerFactory.getLogger(DatabaseUpdater.class);
	
	
	@Value("${test.env:false}")
	private boolean isTestEnv;
	
	@PostConstruct
	public void updateDatabase() {
		if (!isTestEnv) {
			
//			!!! CHANGE THIS WHEN ADDING A NEW UPDATE !!!
			final String TARGET_DATABASE_VERSION = "1.0.0";
			
			updateLegacyDatabase();
			
			String currentVersion = getDBVersion();
			
			if (!currentVersion.equals(TARGET_DATABASE_VERSION)) {
				log.info(
						"Updating database from v"
						+ currentVersion
						+ " to v"
						+ TARGET_DATABASE_VERSION
						+ "..."
				);
				
				updater(
					// expectedVersion
					"0.0.0",
					// targetVersion 
					"0.0.1",
					// updateSql
					"ALTER TABLE Users "
						+ "ADD COLUMN has_imported_data BOOLEAN DEFAULT false; "
					+ "UPDATE Users "
						+ "SET timezone = 'UTC' "
						+ "WHERE timezone IS NULL; "
					+ "ALTER TABLE Users "
						+ "ALTER COLUMN timezone "
						+ "SET NOT NULL;", 
					// verificationSql
					"SELECT 1 "
					+ "FROM information_schema.columns "
					+ "WHERE table_name = 'users' "
					+ "AND column_name = 'has_imported_data' "
					+ "AND data_type = 'boolean' "
					+ "LIMIT 1;",
					// verificationType
					Integer.class
				);
				
				updater(
					// expectedVersion
					"0.0.1",
					// targetVersion 
					"0.0.2",
					// updateSql
					"CREATE TYPE user_role AS ENUM ('ADMIN', 'USER');"
					+ "UPDATE Users "
						+ "SET role = 'USER';"
					+ "ALTER TABLE Users "
						+ "ALTER COLUMN role "
						+ "DROP DEFAULT;"
					+ "ALTER TABLE Users "
						+ "ALTER COLUMN role "
						+ "TYPE user_role USING role::user_role;"
					+ "ALTER TABLE Users "
						+ "ALTER COLUMN role "
						+ "SET NOT NULL, "
						+ "ALTER COLUMN role "
						+ "SET DEFAULT 'USER';",
					// verificationSql
					"SELECT 1 "
					+ "FROM information_schema.columns "
					+ "WHERE table_name = 'users' "
					+ "AND column_name = 'role' "
					+ "AND udt_name = 'user_role' "
					+ "LIMIT 1",
					// verificationType
					Integer.class
				);
				
				updater(
						// expectedVersion
						"0.0.2",
						// targetVersion 
						"0.0.3",
						// updateSql
						"ALTER TABLE Users "
							+ "ADD COLUMN image_url VARCHAR;",
						// verificationSql
						"SELECT 1 "
						+ "FROM information_schema.columns "
						+ "WHERE table_name = 'users' "
						+ "AND column_name = 'image_url' "
						+ "LIMIT 1",
						// verificationType
						Integer.class
					);
				
				updater(
						// expectedVersion
						"0.0.3",
						// targetVersion 
						"0.0.4",
						// updateSql
						"ALTER TABLE SongStreams "
							+ "DROP CONSTRAINT " + getSongStreamsUsernameConstraintName() + ";"
						+ "ALTER TABLE SongStreams "
							+ "ADD FOREIGN KEY (username) "
							+ "REFERENCES Users(username) "
							+ "ON DELETE CASCADE;"
						+ "CREATE INDEX songstreams_username ON SongStreams(username);",
						// verificationSql
						"SELECT 1 "
						+ "FROM pg_constraint "
						+ "WHERE conrelid = 'songstreams'::regclass "
						+ "AND contype = 'f' "
						+ "AND confdeltype = 'c' "
						+ "AND conkey @> ("
							+ "SELECT array_agg(attnum) "
							+ "FROM pg_attribute "
							+ "WHERE attrelid = 'songstreams'::regclass "
							+ "AND attname = 'username' "
						+ ")"
						+ "LIMIT 1;",
						// verificationType
						Integer.class
					);
				
				updater(
						// expectedVersion
						"0.0.4",
						// targetVersion 
						"0.0.5",
						// updateSql
						"ALTER TABLE Users "
							+ "ADD COLUMN must_change_password BOOLEAN DEFAULT false;",
						// verificationSql
						"SELECT 1 "
						+ "FROM information_schema.columns "
						+ "WHERE table_name = 'users' "
						+ "AND column_name = 'must_change_password' "
						+ "LIMIT 1",
						// verificationType
						Integer.class
					);
				
//				dummy update for first official release
				updater(
						// expectedVersion
						"0.0.5",
						// targetVersion 
						"1.0.0",
						// updateSql
						"",
						// verificationSql
						"SELECT 1;",
						// verificationType
						Integer.class
					);
				
			} else {
				log.info(
						"Database is up to date (v"
						+ currentVersion
						+ ")"
				);
			}
		}
	}
	
	/**
	 * verificationSql has to be a query which returns exactly 1 result after a successful update,
	 * and any other number of results (0, >1) before the update
	 * @param <T> type of value to be returned by verificationSql
	 */
	<T> void updater(String expectedVersion,
			String targetVersion,
			String updateSql,
			String verificationSql,
			Class<T> verificationType) {
		
		String currentVersion = getDBVersion();
		StatementSpec updateVersionNumber = jdbc.sql("UPDATE db_info SET version = :targetVersion WHERE id = true;")
		.param("targetVersion", targetVersion, Types.VARCHAR);
		
		if (currentVersion.equals(expectedVersion)) {
			
			jdbc.sql(updateSql).update();
			
			int verification = jdbc.sql(verificationSql).query(verificationType).list().size();
			
			if (verification == 1) {
				if (updateVersionNumber.update() == 1) {
					log.info("Updated database from v"
							+ currentVersion
							+ " to v"
							+ targetVersion);
				} else {
					throw new RuntimeException("Updating version number failed: from "
							+ currentVersion
							+ " to "
							+ targetVersion);
				}
			} else {
				throw new RuntimeException("Database update from version "
						+ currentVersion
						+ " to version "
						+ targetVersion
						+ " failed: "
						+ verification);
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
		jdbc.sql("CREATE TABLE IF NOT EXISTS db_info ("
					+ "id BOOLEAN PRIMARY KEY DEFAULT TRUE CHECK (id),"
					+ "version VARCHAR NOT NULL);")
		.update();
		
		int updated = jdbc.sql("INSERT INTO db_info ("
				+ "version"
				+ ") VALUES ("
				+ "'0.0.0'"
				+ ") ON CONFLICT (id) DO NOTHING;")
		.update();
		
		if (updated > 0) {
			log.info("Updated database from legacy to numbered versioning system");
		}
	}
	
	private String getSongStreamsUsernameConstraintName() {
		return jdbc.sql("SELECT conname "
				+ "FROM pg_constraint "
				+ "WHERE conrelid = 'songstreams'::regclass "
				+ "AND contype = 'f' "
				+ "AND conkey @> ( "
					+ "SELECT array_agg(attnum) "
					+ "FROM pg_attribute "
					+ "WHERE attrelid = 'songstreams'::regclass "
					+ "AND attname = 'username' "
					+ ")"
				+ "LIMIT 1;")
				.query(String.class)
				.single();
	}
}
