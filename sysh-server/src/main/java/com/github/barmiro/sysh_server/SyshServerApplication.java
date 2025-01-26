package com.github.barmiro.sysh_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement 
public class SyshServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SyshServerApplication.class, args);
	}

}
