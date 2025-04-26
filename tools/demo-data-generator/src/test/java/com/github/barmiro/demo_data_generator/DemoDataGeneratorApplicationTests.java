package com.github.barmiro.demo_data_generator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"SYSH_SERVER_PORT=57540"
})
class DemoDataGeneratorApplicationTests {

	@Test
	void contextLoads() {
	}

}
