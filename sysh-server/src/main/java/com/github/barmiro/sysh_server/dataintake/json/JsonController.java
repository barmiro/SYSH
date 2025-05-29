package com.github.barmiro.sysh_server.dataintake.json;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.barmiro.sysh_server.catalog.AddToCatalog;
import com.github.barmiro.sysh_server.catalog.streams.SongStream;
import com.github.barmiro.sysh_server.common.utils.ConvertDTOs;
import com.github.barmiro.sysh_server.stats.CacheService;
import com.github.barmiro.sysh_server.users.SyshUserRepository;

@RestController
public class JsonController {
	
	private final AddToCatalog addToCatalog;
	private final SyshUserRepository userRepo;
	private final ZipProcessor zipProcessor;
	CacheService cacheService;
	
	JsonController(
			AddToCatalog addToCatalog,
			SyshUserRepository userRepo,
			ZipProcessor zipProcessor,
			CacheService cacheService
			) {
		this.addToCatalog = addToCatalog;
		this.userRepo = userRepo;
		this.zipProcessor = zipProcessor;
		this.cacheService = cacheService;
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
	
	
	
	private Map<String, ZipUploadItem> zipStatusMap = new ConcurrentHashMap<>();
	private Map<String, CopyOnWriteArrayList<JsonInfo>> jsonStatusMap = new ConcurrentHashMap<>();
	private Map<String, CopyOnWriteArrayList<SseEmitter>> statusEmitterMap = new ConcurrentHashMap<>();
	
	@PostMapping(value = "/uploadZip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	String uploadZip (@RequestParam MultipartFile file) throws IOException {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		String uploadID = UUID.randomUUID().toString();
		String zipName = file.getOriginalFilename();
		byte[] zipBytes = file.getBytes();
		
		log.info("Importing zip file for user " + username + ": " + zipName);
		
		if (zipStatusMap.containsKey(username)) {
			
			FileProcessingStatus status = zipStatusMap.get(username).status();
			
			if (status != FileProcessingStatus.SUCCESS && status != FileProcessingStatus.ERROR && status != FileProcessingStatus.COMPLETE) {
				throw new DuplicateKeyException("User " + username + " is already importing a zip file");
			} else {
				zipStatusMap.remove("username");
				jsonStatusMap.remove("username");
			}
		}
		
		long start = System.currentTimeMillis();
		
		ZipUploadItem zipUploadItem = new ZipUploadItem(
				uploadID,
				zipName,
				FileProcessingStatus.PREPARING,
				null
		);
		
		zipStatusMap.put(
				username,
				zipUploadItem
		);
		

		// not optimal, but the list is very small, shouldn't impact performance
		jsonStatusMap.put(
				username,
				new CopyOnWriteArrayList<>()
		);
		emitStatus(username);
		CompletableFuture.runAsync(() -> {
			try {
				
				Map<String, List<StreamDTO>> jsonFiles = zipProcessor.processExtendedStreamingHistory(zipBytes, username);
				
				zipStatusMap.put(username, new ZipUploadItem(uploadID, zipName, FileProcessingStatus.PROCESSING, null));
				emitStatus(username);
				
				log.info("Found " + jsonFiles.size() + " json files");
				
				CopyOnWriteArrayList<JsonInfo> jsonInfoList = new CopyOnWriteArrayList<>();
				for (Map.Entry<String, List<StreamDTO>> jsonFile : jsonFiles.entrySet()) {
					jsonInfoList.add(
							new JsonInfo(
									jsonFile.getKey(),
									FileProcessingStatus.WAITING,
									null
							)
					);
				}
				
				jsonStatusMap.put(username, jsonInfoList);
				emitStatus(username);
				
				for (Map.Entry<String, List<StreamDTO>> jsonFile : jsonFiles.entrySet()) {
					CopyOnWriteArrayList<JsonInfo> tempList = jsonStatusMap.get(username);
					
					tempList = updateJsonInfo(
							tempList,
							jsonFile.getKey(),
							FileProcessingStatus.PROCESSING,
							null
					);
					
					jsonStatusMap.put(username, tempList);
					emitStatus(username);
					
					try {
						log.info(jsonFile.getKey() + ":");
						
						List<SongStream> streams = ConvertDTOs.streamsJson(jsonFile.getValue(), username);
						
						int entriesAdded = 0;
						
						if (!streams.isEmpty()) {
							entriesAdded = addToCatalog.adder(streams, username);
						}
						
						if (entriesAdded == 0) {
							tempList = updateJsonInfo(
									tempList,
									jsonFile.getKey(),
									FileProcessingStatus.ERROR,
									0
							);
							
							log.info("No streams found in file: " + jsonFile.getKey());
						} else {
							tempList = updateJsonInfo(
									tempList,
									jsonFile.getKey(),
									FileProcessingStatus.SUCCESS,
									entriesAdded
							);
						}
						
						jsonStatusMap.put(username, tempList);
						emitStatus(username);
						
					} catch (Exception e) {
						e.printStackTrace();
						tempList = updateJsonInfo(
								tempList,
								jsonFile.getKey(),
								FileProcessingStatus.ERROR,
								null
						);
						jsonStatusMap.put(username, tempList);
						emitStatus(username);
					}
				}
				
				zipStatusMap.put(username, new ZipUploadItem(uploadID, zipName, FileProcessingStatus.FINALIZING, null));
				emitStatus(username);
				cacheService.cacheGenerator(username);
				
				userRepo.setHasImportedData(username, true);
				zipStatusMap.put(username, new ZipUploadItem(uploadID, zipName, FileProcessingStatus.SUCCESS, Instant.now().atZone(userRepo.getUserTimezone(username))));
				emitStatus(username);
				log.info("Finished zip file import for user " + username + ": " + zipName);
				
				long end = System.currentTimeMillis();
				long time = (end - start) / 1000;
				log.info("Time elapsed: " + time);
				
			} catch (Exception e) {
				e.printStackTrace();
				zipStatusMap.put(username, new ZipUploadItem(uploadID, zipName, FileProcessingStatus.ERROR, null));
				emitStatus(username);
			}	
		});
		
		return uploadID;
		
	}

	
	private record ZipUploadStatusResponse(ZipUploadItem zipUploadItem, List<JsonInfo> jsonInfoList) {}
	
	public ZipUploadStatusResponse zipUploadStatus(String username) {
		
		return new ZipUploadStatusResponse(
				zipStatusMap.get(username),
				jsonStatusMap.get(username)
		);
	}
	
	
	@GetMapping("/zipStatusStream")
	public SseEmitter streamStatus() {
	    SseEmitter emitter = new SseEmitter(86400_000L); // 24-hour timeout just in case checkConnections() misses something
	    
	    String username = SecurityContextHolder.getContext().getAuthentication().getName();
	    
	    statusEmitterMap.computeIfAbsent(username, k -> new CopyOnWriteArrayList<>()).add(emitter);

	    emitter.onCompletion(() -> statusEmitterMap.getOrDefault(username, new CopyOnWriteArrayList<>()).remove(emitter));
	    emitter.onTimeout(() -> statusEmitterMap.getOrDefault(username, new CopyOnWriteArrayList<>()).remove(emitter));
	    emitter.onError((e) -> statusEmitterMap.getOrDefault(username, new CopyOnWriteArrayList<>()).remove(emitter));

	    ZipUploadItem item = zipStatusMap.get(username);
	    if (item != null && item.status() == FileProcessingStatus.SUCCESS) {
		    	zipStatusMap.put(
		    			username,
		    			new ZipUploadItem(
				    			item.uploadID(),
				    			item.zipName(),
				    			FileProcessingStatus.COMPLETE,
				    			item.completedOn()
			    		)
		    	);
	    }
	    
	    emitStatus(username);
	    return emitter;
	}
	
	private void emitStatus(String username) {
		List<SseEmitter> emitters = statusEmitterMap.get(username);
		if (emitters != null) {
			for (SseEmitter emitter : emitters) {
				try {
					emitter.send(
							SseEmitter.event()
									.name("status")
									.data(zipUploadStatus(username))
							);
				} catch (IOException e) {
					emitter.complete();
				} catch (Exception ex) {
					emitter.completeWithError(ex);
					zipStatusMap.remove(username);
				}
			}
		}
	}
	
	@Scheduled(fixedRate = 60000)
	void checkConnections() {
		statusEmitterMap.forEach((username, emitters) -> {
			for (SseEmitter emitter : emitters) {
				try {
					emitter.send(
							SseEmitter.event()
									.name("ping")
									.data("ping")
					);
				} catch (Exception e) {
					log.info("Connection from user " + username + " closed");
					emitter.complete();
				}
			}
		});
	}
	
	
	private CopyOnWriteArrayList<JsonInfo> updateJsonInfo(
				CopyOnWriteArrayList<JsonInfo> list,
				String name,
				FileProcessingStatus newStatus,
				Integer entriesAdded) {
		
		for (int i = 0; i < list.size(); i++) {
			JsonInfo info = list.get(i);
			if (info.filename().equals(name)) {
				JsonInfo updated = new JsonInfo(
						info.filename(),
						newStatus,
						entriesAdded
				);
				list.set(i, updated);
			}
		}
		return list;
	}
	
	
	
}
 