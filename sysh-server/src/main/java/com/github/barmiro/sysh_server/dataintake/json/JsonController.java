package com.github.barmiro.sysh_server.dataintake.json;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.barmiro.sysh_server.catalog.AddToCatalog;
import com.github.barmiro.sysh_server.catalog.streams.SongStream;
import com.github.barmiro.sysh_server.common.utils.ConvertDTOs;
import com.github.barmiro.sysh_server.users.SyshUserRepository;

@RestController
public class JsonController {
	
	private final AddToCatalog addToCatalog;
	private final SyshUserRepository userRepo;
	
	JsonController(
			AddToCatalog addToCatalog,
			SyshUserRepository userRepo
			) {
		this.addToCatalog = addToCatalog;
		this.userRepo = userRepo;
	}
	
	private static final Logger log = LoggerFactory.getLogger(JsonController.class);
	
	@PostMapping("/addJson")
	Integer addJson(@RequestBody List<StreamDTO> streamDTOs)
			throws JsonProcessingException,
			ClassCastException,
			IllegalAccessException,
			InvocationTargetException
	{
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		log.info("Adding json file for user " + username);
		long start = System.currentTimeMillis();

		List<SongStream> streams = ConvertDTOs.streamsJson(streamDTOs, username);
		
		if (streams.isEmpty()) {
			log.info("No new streams found");
			return 0;
		}
		
		Integer result = addToCatalog.adder(streams, username);
		userRepo.setHasImportedData(username, true);
		
		long end = System.currentTimeMillis();
		long time = (end - start) / 1000;
		log.info("Time elapsed: " + time);
		
		return result;
	}
	}
 