package com.github.barmiro.sysh_server.auth;

import org.springframework.stereotype.Service;

@Service
public class TokenService {
	private String token;
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getToken() {
		return token;
	}
}
