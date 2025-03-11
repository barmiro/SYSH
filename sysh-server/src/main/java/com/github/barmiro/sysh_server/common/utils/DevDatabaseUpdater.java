package com.github.barmiro.sysh_server.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

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
//			String sql = ("CREATE FUNCTION create_stats_cache_full()\n"
//					+ "RETURNS TRIGGER AS $$\n"
//					+ "BEGIN\n"
//					+ "    INSERT INTO Stats_Cache_Full (username)\n"
//					+ "    VALUES (NEW.username);\n"
//					+ "    RETURN NEW;\n"
//					+ "END;\n"
//					+ "$$ LANGUAGE plpgsql;\n"
//					+ "\n"
//					+ "CREATE TRIGGER user_insert_trigger\n"
//					+ "AFTER INSERT ON Users\n"
//					+ "FOR EACH ROW\n"
//					+ "EXECUTE FUNCTION create_stats_cache_full();");
//			jdbc.sql(sql)
//			.update();			
//		}
//	}
}
