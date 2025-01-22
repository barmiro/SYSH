package com.github.barmiro.sysh_server.integration.json;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.barmiro.sysh_server.catalog.AddToCatalog;
import com.github.barmiro.sysh_server.catalog.streams.Stream;

@RestController
public class JsonController {
	
	private final AddToCatalog addToCatalog;
	
	JsonController(AddToCatalog addToCatalog) {
		this.addToCatalog = addToCatalog;
	}
	
	@PostMapping("/addJson")
	String addJson(@RequestBody List<StreamDTO> streamDTOs) {
		
		System.out.println("Adding json file...");
		long start = System.currentTimeMillis();

		List<Stream> streams = ConvertStreams.json(streamDTOs);  // null?
		
		String result = addToCatalog.adder(streams);
		
		long end = System.currentTimeMillis();
		long time = (end - start) / 1000;
		System.out.println("Time elapsed: " + time);
		
		return result;
	}
}
 