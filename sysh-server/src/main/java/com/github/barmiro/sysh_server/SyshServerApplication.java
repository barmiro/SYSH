package com.github.barmiro.sysh_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class SyshServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SyshServerApplication.class, args);
	}

}
