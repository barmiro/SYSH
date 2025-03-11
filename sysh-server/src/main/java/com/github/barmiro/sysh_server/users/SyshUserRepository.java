package com.github.barmiro.sysh_server.users;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import com.github.barmiro.sysh_server.common.utils.GetRandom;
import com.github.barmiro.sysh_server.security.SyshUser;

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
	
	public Optional<SyshUser> findByUsername(String username) {
		return jdbc.sql("SELECT * FROM Users "
				+ "WHERE username = :username "
				+ "LIMIT 1")
				.param("username", username, Types.VARCHAR)
				.query(SyshUser.class)
				.optional();
	}
	
	public String findBySpotifyState(String spotifyState) {
		return jdbc.sql("SELECT username FROM Users "
				+ "WHERE spotify_state = :spotify_state "
				+ "LIMIT 1")
				.param("spotify_state", spotifyState, Types.VARCHAR)
				.query(String.class)
				.single();
	}
	
	public int updateSpotifyAccessToken(String username, String accessToken) {
		return jdbc.sql("UPDATE Users SET "
				+ "access_token = :access_token "
				+ "WHERE username = :username")
				.param("access_token", accessToken, Types.VARCHAR)
				.param("username", username, Types.VARCHAR)
				.update();

	}
	
	public Optional<String> getSpotifyAccessToken(String username) {
		return jdbc.sql("SELECT access_token FROM Users "
				+ "WHERE username = :username "
				+ "LIMIT 1")
				.param("username", username, Types.VARCHAR)
				.query(String.class)
				.optional();
	}
	
	public int updateSpotifyRefreshToken(String username, String refreshToken) {
		return jdbc.sql("UPDATE Users SET "
				+ "refresh_token = :refresh_token "
				+ "WHERE username = :username")
				.param("refresh_token", refreshToken, Types.VARCHAR)
				.param("username", username, Types.VARCHAR)
				.update();

	}
	
	public Optional<String> getSpotifyRefreshToken(String username) {
		return jdbc.sql("SELECT refresh_token FROM Users "
				+ "WHERE username = :username "
				+ "LIMIT 1")
				.param("username", username, Types.VARCHAR)
				.query(String.class)
				.optional();
	}
	
	public int updateTokenExpirationTime(String username, Timestamp tokenExpires) {
		return jdbc.sql("UPDATE Users SET "
				+ "token_expires = :token_expires "
				+ "WHERE username = :username")
				.param("token_expires", tokenExpires, Types.TIMESTAMP)
				.param("username", username, Types.VARCHAR)
				.update();
	}
	
	public Optional<Timestamp> getTokenExpirationTime(String username) {
		return jdbc.sql("SELECT token_expires FROM Users "
				+ "WHERE username = :username "
				+ "LIMIT 1")
				.param("username", username, Types.VARCHAR)
				.query(Timestamp.class)
				.optional();
	}
	
	public List<String> findAllUsernames() {
		return jdbc.sql("SELECT username FROM Users ORDER BY username ASC")
				.query(String.class)
				.list();
	}
	
	public String getSpotifyState(String username) {
		return jdbc.sql("SELECT spotify_state FROM Users "
				+ "WHERE username = :username "
				+ "LIMIT 1")
				.param("username", username, Types.VARCHAR)
				.query(String.class)
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
	
	public int addUserDisplayName(String username, String displayName) {
		return jdbc.sql("UPDATE Users SET "
				+ "display_name = :display_name "
				+ "WHERE username = :username")
				.param("display_name", displayName, Types.VARCHAR)
				.param("username", username, Types.VARCHAR)
				.update();
	}
	
	public String getUserDisplayName(String username) {
		return jdbc.sql("SELECT display_name FROM Users "
				+ "WHERE username = :username")
				.param("username", username, Types.VARCHAR)
				.query(String.class)
				.single();
	}

	
}
