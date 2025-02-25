package com.github.barmiro.sysh_server.userdata;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class UserDataRepository {
	
	JdbcClient jdbc;
	
	UserDataRepository(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}

	public int addUserData(UserData data) {
		return jdbc.sql(")
	}
}
