package com.github.barmiro.sysh_server.users;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import com.github.barmiro.sysh_server.common.utils.GetRandom;
import com.github.barmiro.sysh_server.security.SyshUser;
import com.github.barmiro.sysh_server.security.UserRole;

@Repository
public class SyshUserRepository {
	
	JdbcClient jdbc;
	
	SyshUserRepository(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}

	public Integer userCount() {
		return jdbc.sql("SELECT COUNT(username) FROM Users")
			.query(Integer.class)
			.single();
	}
	
	public Integer adminCount() {
		return jdbc.sql("SELECT COUNT(username) FROM Users WHERE role = :role")
				.param("role", UserRole.ADMIN, Types.OTHER)
				.query(Integer.class)
				.single();
	}
	
	public UserRole getRole(SyshUser user) {
		return jdbc.sql("SELECT role FROM Users "
				+ "WHERE username = :username "
				+ "LIMIT 1")
				.param("username", user.username(), Types.VARCHAR)
				.query(UserRole.class)
				.single();
	}
	
	public ZoneId getUserTimezone(String username) {
		return jdbc.sql("SELECT timezone FROM Users "
				+ "WHERE username = :username "
				+ "LIMIT 1")
				.param("username", username, Types.VARCHAR)
				.query(String.class)
				.optional()
				.map(timeZoneString -> ZoneId.of(timeZoneString))
				.orElse(ZoneId.of(TimeZone.getDefault().getID()));
	}
	
	public Optional<SyshUser> findByUsername(String username) {
		return jdbc.sql("SELECT * FROM Users "
				+ "WHERE username = :username "
				+ "LIMIT 1")
				.param("username", username, Types.VARCHAR)
				.query(SyshUser.class)
				.optional();
	}

	
	public Optional<String> findBySpotifyState(String spotifyState) {
		return jdbc.sql("SELECT username FROM Users "
				+ "WHERE spotify_state = :spotify_state "
				+ "LIMIT 1")
				.param("spotify_state", spotifyState, Types.VARCHAR)
				.query(String.class)
				.optional();
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
	
	BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
	
	public int createUser(SyshUser user) {
		
		String passwordHash = passwordEncoder.encode(user.password());
		String spotifyState = GetRandom.alphaNumeric(32);
		
		return jdbc.sql("INSERT INTO Users ("
				+ "username,"
				+ "password,"
				+ "role,"
				+ "timezone,"
				+ "spotify_state,"
				+ "must_change_password"
				+ ") VALUES ("
				+ ":username,"
				+ ":password,"
				+ ":role,"
				+ ":timezone,"
				+ ":spotify_state,"
				+ ":must_change_password"
				+ ") ON CONFLICT (username) "
				+ "DO NOTHING")
				.param("username", user.username(), Types.VARCHAR)
				.param("password", passwordHash, Types.VARCHAR)
				.param("role", user.role(), Types.OTHER)
				.param("timezone", user.timezone())
				.param("spotify_state", spotifyState, Types.VARCHAR)
				.param("must_change_password", user.must_change_password(), Types.BOOLEAN)
				.update();
	}
	
	public int deleteUser(String username) {
		return jdbc.sql("DELETE FROM Users WHERE username = :username;")
				.param("username", username, Types.VARCHAR)
				.update();
	}
	
	public int changePassword(String username, String newPassword, Boolean mustChangePassword) {
		String passwordHash = passwordEncoder.encode(newPassword);
		
		return jdbc.sql("UPDATE Users "
				+ "SET password = :passwordHash, "
				+ "must_change_password = :mustChangePassword "
				+ "WHERE username = :username;")
				.param("passwordHash", passwordHash, Types.VARCHAR)
				.param("mustChangePassword", mustChangePassword, Types.BOOLEAN)
				.param("username", username, Types.VARCHAR)
				.update();
	}
	
	public int updateUserTimezone(String username, String timezone) {
		return jdbc.sql("UPDATE Users "
				+ "set timezone = :timezone "
				+ "WHERE username = :username")
				.param("timezone", timezone, Types.VARCHAR)
				.param("username", username, Types.VARCHAR)
				.update();
	}
	
	public int addSpotifyUserData(String username, SpotifyUserDataDTO userData) {
		String imageUrl = null;
		if (!userData.images().isEmpty()) {
			imageUrl = userData.images().getFirst().url();
		}
		return jdbc.sql("UPDATE Users SET "
				+ "display_name = :display_name, "
				+ "image_url = :image_url "
				+ "WHERE username = :username")
				.param("display_name", userData.display_name(), Types.VARCHAR)
				.param("image_url", imageUrl, Types.VARCHAR)
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
	
	public AppUserData getAppUserData(String username) {
		return jdbc.sql("SELECT * "
				+ "FROM Users "
				+ "WHERE username = :username")
				.param("username", username, Types.VARCHAR)
				.query(AppUserData.class)
				.single();
	}
	
	public List<AppUserData> getAllUserData() {
		return jdbc.sql("SELECT * "
				+ "FROM Users;")
				.query(AppUserData.class)
				.list();
	}

	public int setHasImportedData(String username, Boolean value) {
		return jdbc.sql("UPDATE Users SET "
				+ "has_imported_data = :value "
				+ "WHERE username = :username;")
				.param("value", value, Types.BOOLEAN)
				.param("username", username, Types.VARCHAR)
				.update();
	}
	
}
