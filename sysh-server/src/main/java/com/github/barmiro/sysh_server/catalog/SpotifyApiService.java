package com.github.barmiro.sysh_server.catalog;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.barmiro.sysh_server.auth.TokenService;

@Service
public abstract class SpotifyApiService<
	ServiceClass extends CatalogService,
	EntityClass extends CatalogEntity> {

	protected final JdbcClient jdbc;
	protected final RestClient apiClient;
	protected final TokenService tkn;
	protected final ServiceClass catalogService;
	
	protected SpotifyApiService(JdbcClient jdbc, 
			RestClient apiClient, 
			TokenService tkn,
			ServiceClass catalogService) {
		this.jdbc = jdbc;
		this.apiClient = apiClient;
		this.tkn = tkn;
		this.catalogService = catalogService;
	}
	
	protected List<String> newIDs = new ArrayList<>();
	
	protected ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature
					.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	
	protected void makeList(String entityID, String idName, Class<EntityClass> entCls) {
		if (entityID == "") {
			return;
		}
		int exists = jdbc.sql("SELECT * FROM "
				+ entCls.getSimpleName() + "s "
				+ "WHERE " + idName + " = :entityID "
				+ "LIMIT 1")
				.param("entityID", entityID, Types.VARCHAR)
				.query(entCls)
				.list()
				.size();
		
		if (exists == 0 && !newIDs.contains(entityID)) {
			newIDs.add(entityID);
		} else {
			System.out.println(entCls.getSimpleName() + " " + entityID + " already exists");
		}
		
	}
	
	protected String stringify(List<String> newIDs, Class<EntityClass> entCls) {
		StringBuilder sb = new StringBuilder();
		sb.append(entCls.getSimpleName().toLowerCase() + "s?ids=");
		for (String id:newIDs) {
			sb.append(id + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		
		return sb.toString();
	}

	protected ResponseEntity<String> getList(
			List<String> IDlist,
			Class<EntityClass> entCls,
			int limit
			) throws Exception {
		
		if (IDlist.size() == 0 || IDlist.size() > limit) {
			return null;
		}
		ResponseEntity<String> response = apiClient
				.get()
				.uri(stringify(IDlist, entCls))
				.header("Authorization", "Bearer " + tkn.getToken())
				.retrieve()
				.toEntity(String.class);
		
		return response;
	}
}
//	protected Integer addToTable(ResponseEntity<String> getList Class<WrapperClass> wraCls) 
//			throws JsonMappingException, JsonProcessingException {
//
//		int added = 0;
//		
//		List<ApiEntityClass> apiEntities = mapper
//				.readValue(getList.getBody(), wraCls);
//				.getList();
//		
//		for (ApiTrack track:apiTracks) {
//			String spotify_track_id = track.id();
//			String name = track.name();
//			Integer duration_ms = track.duration_ms();
//			String album_id = track.album().id();
//			
//			Track newTrack = new Track(
//					spotify_track_id,
//					name,
//					duration_ms,
//					album_id);
//			
//			added += catalogService.addNewTrack(newTrack);
//			
////			List<ApiTrackArtist> artists = track.artists();
//		}
//		
//		return added;
//	}
//	
//	public Integer addNewTracks(String track_id, boolean end) {
//		
//		makeList(track_id);
//		
//		if (newIDs.size() < 50 && !end) {
//			return 0;
//		}
//		
//		ResponseEntity<String> response = null;
//		
//		try {
//			response = getList(newIDs);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return 0;
//		}
//		
//		if (response == null) {
//			System.out.println("The ID list is either empty or too big.");
//			return 0;
//		}
//		try {
//			newIDs.clear();
//			return addToTracks(response);
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//			return 0;
//		}
//		
//	}
//}
