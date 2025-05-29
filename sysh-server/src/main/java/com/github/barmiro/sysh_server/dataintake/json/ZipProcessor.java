package com.github.barmiro.sysh_server.dataintake.json;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class ZipProcessor {
	
	ObjectMapper mapper = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.configure(DeserializationFeature
					.FAIL_ON_UNKNOWN_PROPERTIES, false);

	public Map<String, List<StreamDTO>> processExtendedStreamingHistory(byte[] zipBytes, String username) throws IOException {
		
		
		
		Map<String, List<StreamDTO>> jsonFiles = new TreeMap<String, List<StreamDTO>>();
		
		try (ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
				ZipInputStream zis = new ZipInputStream(bais)) {
			
			ZipEntry entry;
			
			while ((entry = zis.getNextEntry()) != null) {
				String filename = entry.getName();
				
				if (!filename.matches(".*/Streaming_History_Audio.*\\.json")) {
					continue;
				}
				
				String json = new String(zis.readAllBytes(), StandardCharsets.UTF_8);
				
				List<StreamDTO> streamList = mapper.readValue(
						json,
						new TypeReference<List<StreamDTO>>() {}
						);
				
				jsonFiles.put(filename.substring(filename.lastIndexOf('/')).replace("/", ""), streamList);
				
			}
		}
		
		return jsonFiles;
	}
	
	
}
