package com.github.barmiro.sysh_server.users;

import java.sql.Types;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class SyshUserRepository {
	
	JdbcClient jdbc;
	
	SyshUserRepository(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}

//	public int addUserData(SyshUser data) {
//		return jdbc.sql("UPDATE User_Data SET "
//				+ "display_name = :display_name "
//				+ "WHERE id = 1;")
//				.param("display_name", data.display_name(), Types.VARCHAR)
//				.update();
//	}
	
	public String getRole(SyshUser user) {
		return jdbc.sql("SELECT role FROM Users "
				+ "WHERE username = :username "
				+ "LIMIT 1")
				.param("username", user.username(), Types.VARCHAR)
				.query(String.class)
				.single();
	}
	
	public SyshUser findByUsername(String username) {
		return jdbc.sql("SELECT * FROM Users "
				+ "WHERE username = :username "
				+ "LIMIT 1")
				.param("username", username, Types.VARCHAR)
				.query(SyshUser.class)
				.single();
	}
	
	public int createUser(UserDetails userDetails) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		String password_hash = passwordEncoder.encode(userDetails.getPassword());
		
		return jdbc.sql("INSERT INTO Users ("
				+ "username,"
				+ "password"
				+ ") VALUES ("
				+ ":username,"
				+ ":password"
				+ ") ON CONFLICT (username) "
				+ "DO NOTHING")
				.param("username", userDetails.getUsername(), Types.VARCHAR)
				.param("password", password_hash, Types.VARCHAR)
				.update();
	}

	
}
