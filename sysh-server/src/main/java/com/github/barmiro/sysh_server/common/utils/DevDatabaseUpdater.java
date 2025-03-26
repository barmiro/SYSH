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
//			String sql = ("CREATE INDEX idx_songstreams_user_ts ON SongStreams(username, ts DESC);\n"
//					+ "CREATE INDEX idx_songstreams_user_ms_played ON SongStreams(username, ms_played);\n"
//					+ "CREATE INDEX idx_songstreams_user_count ON SongStreams(username) WHERE ms_played >= 30000;\n"
//					+ "CREATE INDEX idx_songstreams_user_ms_played_covering ON SongStreams(username, ts, ms_played);"
//					);
//			jdbc.sql(sql)
//			.update();			
//		}
//	}
}
