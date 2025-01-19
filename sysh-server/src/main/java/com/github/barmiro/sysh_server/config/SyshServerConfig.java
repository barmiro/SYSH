package com.github.barmiro.sysh_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class SyshServerConfig {
	
	@Bean
	RestClient apiClient(RestClient.Builder builder) {
		return builder.baseUrl("https://api.spotify.com/v1/").build();
	}
	
	@Bean
	RestClient tokenClient(RestClient.Builder builder) {
		return builder
				.baseUrl("https://accounts.spotify.com/api/token")
				.build();
	}
	
	@Bean
	RestClient trackClient(RestClient.Builder builder) {
		return builder
				.baseUrl("https://api.spotify.com/v1/")
				.build();
	}
}
