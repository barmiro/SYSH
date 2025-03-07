package com.github.barmiro.sysh_server.dataintake.json;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.barmiro.sysh_server.catalog.AddToCatalog;
import com.github.barmiro.sysh_server.catalog.streams.SongStream;
import com.github.barmiro.sysh_server.common.utils.ConvertDTOs;

@RestController
public class JsonController {
	
	private final AddToCatalog addToCatalog;
	
	JsonController(AddToCatalog addToCatalog) {
		this.addToCatalog = addToCatalog;
	}
	
	private static final Logger log = LoggerFactory.getLogger(JsonController.class);
	
	@PostMapping("/addJson")
	String addJson(@RequestBody List<StreamDTO> streamDTOs,
			@AuthenticationPrincipal Jwt jwt) {
		
		log.info("Adding json file...");
		long start = System.currentTimeMillis();
		String username = jwt.getPrincipalClaimName();
		

		List<SongStream> streams = ConvertDTOs.streamsJson(streamDTOs, username);  // null?
		if (streams.isEmpty()) {
			return "No streams found";
		}
		String result = addToCatalog.adder(streams, username);
		
		long end = System.currentTimeMillis();
		long time = (end - start) / 1000;
		log.info("Time elapsed: " + time);
		
		return result;
	}
	}
 