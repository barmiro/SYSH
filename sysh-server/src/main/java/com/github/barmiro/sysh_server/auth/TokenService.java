package com.github.barmiro.sysh_server.auth;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
public class TokenService {
	private String token;
	private LocalDateTime expirationTime;
	private String refreshToken;
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getToken() {
		return token;
	}
	
	public void expTimeFromSeconds(int seconds) {
		this.expirationTime = LocalDateTime.now().plusSeconds(seconds);
	}
	
	public LocalDateTime getExpTime() {
		return expirationTime;
	}
	
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	public String refreshToken() {
		return refreshToken;
	}
}
