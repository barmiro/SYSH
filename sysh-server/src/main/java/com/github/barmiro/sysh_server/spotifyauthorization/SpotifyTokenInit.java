package com.github.barmiro.sysh_server.spotifyauthorization;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class SpotifyTokenInit {

	JdbcClient jdbc;
	
	SpotifyTokenInit(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}
	
	String initToken() {

		List<String> tokenList = new ArrayList<>();
		try {
			tokenList = jdbc.sql("SELECT token FROM Refresh LIMIT 1").query(String.class).list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (tokenList.size() == 0 || tokenList.get(0) == null || tokenList.get(0).isEmpty()) {
			return null;
		}
		return tokenList.get(0);
//		this was an approach to limit the time the token is stored, but it lead to needing many re-authorizations
//		jdbc.sql("DELETE FROM Refresh").update();
	}
	
//	TODO: change to ON CONFLICT DO UPDATE SET
	public void saveToken(String refreshToken) {

		
		try {
			jdbc.sql("DELETE FROM Refresh").update();
			if (refreshToken == null) {
				return;
			}
			jdbc.sql("INSERT INTO Refresh (token) VALUES (:refreshToken)")
			.param("refreshToken", refreshToken, Types.VARCHAR)
			.update();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
