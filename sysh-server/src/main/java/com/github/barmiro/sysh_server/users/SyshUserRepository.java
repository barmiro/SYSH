package com.github.barmiro.sysh_server.users;

import java.sql.Types;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import com.github.barmiro.sysh_server.common.utils.GetRandom;

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
	
	public SyshUser findBySpotifyState(String spotifyState) {
		return jdbc.sql("SELECT * FROM Users "
				+ "WHERE spotify_state = :spotify_state "
				+ "LIMIT 1")
				.param("spotify_state", spotifyState, Types.VARCHAR)
				.query(SyshUser.class)
				.single();
	}
	
	public int createUser(UserDetails userDetails) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		
		String passwordHash = passwordEncoder.encode(userDetails.getPassword());
		String spotifyState = GetRandom.alphaNumeric(32);
		
		return jdbc.sql("INSERT INTO Users ("
				+ "username,"
				+ "password,"
				+ "spotify_state"
				+ ") VALUES ("
				+ ":username,"
				+ ":password,"
				+ ":spotify_state"
				+ ") ON CONFLICT (username) "
				+ "DO NOTHING")
				.param("username", userDetails.getUsername(), Types.VARCHAR)
				.param("password", passwordHash, Types.VARCHAR)
				.param("spotify_state", spotifyState, Types.VARCHAR)
				.update();
	}

	
}
