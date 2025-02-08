package com.github.barmiro.sysh_server.auth;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class TokenInit {

	JdbcClient jdbc;
	TokenService tkn;
	
	TokenInit(JdbcClient jdbc, TokenService tkn) {
		this.jdbc = jdbc;
		this.tkn = tkn;
	}
	
//	I don't like this approach, but it was quick
	@Value("${test.env:false}")
	private boolean isTestEnv;

	@PostConstruct
	void initToken() {

		
		if (isTestEnv) {
			return;
		}
		
		List<String> tokenList = new ArrayList<>();
		try {
			tokenList = jdbc.sql("SELECT token FROM Refresh LIMIT 1").query(String.class).list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (tokenList.size() == 0 || tokenList.get(0) == null || tokenList.get(0).isEmpty()) {
			return;
		}
		tkn.setRefreshToken(tokenList.get(0));
		jdbc.sql("DELETE FROM Refresh").update();
	}
	
	
	@PreDestroy
	public void saveToken() {
		if (isTestEnv) {
			return;
		}
		
		try {
			jdbc.sql("DELETE FROM Refresh").update();
			if (tkn.getRefreshToken() == null) {
				return;
			}
			jdbc.sql("INSERT INTO Refresh (token) VALUES (:refreshToken)")
			.param("refreshToken", tkn.getRefreshToken(), Types.VARCHAR)
			.update();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
