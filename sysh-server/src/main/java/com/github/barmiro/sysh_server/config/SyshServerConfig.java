package com.github.barmiro.sysh_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestClient;

@Configuration
@EnableScheduling
@EnableTransactionManagement 
public class SyshServerConfig {
	
	@Bean
	RestClient apiClient(RestClient.Builder builder) {
		return builder
				.baseUrl("https://api.spotify.com/v1/")
				.build();
	}
	
	@Bean
	RestClient tokenClient(RestClient.Builder builder) {
		return builder
				.baseUrl("https://accounts.spotify.com/api/token")
				.build();
	}
}
