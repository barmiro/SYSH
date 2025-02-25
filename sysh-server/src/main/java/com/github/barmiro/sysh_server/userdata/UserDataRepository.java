package com.github.barmiro.sysh_server.userdata;

import java.sql.Types;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class UserDataRepository {
	
	JdbcClient jdbc;
	
	UserDataRepository(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}

	public int addUserData(UserData data) {
		return jdbc.sql("UPDATE User_Data SET "
				+ "display_name = :display_name "
				+ "WHERE id = 1;")
				.param("display_name", data.display_name(), Types.VARCHAR)
				.update();
	}
}
