package com.github.barmiro.sysh_server.catalog.tracks.spotify_api;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.barmiro.sysh_server.auth.TokenService;
import com.github.barmiro.sysh_server.catalog.tracks.Track;
import com.github.barmiro.sysh_server.catalog.tracks.TrackService;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.TracksWrapper;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.ApiTrack;

@Service
public class TrackApiService {

	private final JdbcClient jdbcClient;
	private final RestClient trackClient;
	private final TokenService tkn;
	private final TrackService trackService;
	
	TrackApiService(JdbcClient jdbcClient, 
			RestClient trackClient, 
			TokenService tkn,
			TrackService trackService) {
		this.jdbcClient = jdbcClient;
		this.trackClient = trackClient;
		this.tkn = tkn;
		this.trackService = trackService;
	}
	
	private List<String> newIDs = new ArrayList<>();
	
	ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature
					.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	
	void makeList(String trackID) {
		int exists = jdbcClient.sql("SELECT 1 FROM Tracks "
				+ "WHERE spotify_track_id = :trackID "
				+ "LIMIT 1")
				.param("trackID", trackID, Types.VARCHAR)
				.query(Integer.class)
				.list()
				.size();
		
		if (exists == 0) {
			newIDs.add(trackID);			
		}
		
	}
	
	private String stringify(List<String> newIDs) {
		StringBuilder sb = new StringBuilder();
		sb.append("tracks?ids=");
		for (String id:newIDs) {
			sb.append(id + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		
		return sb.toString();
	}
	
	private ResponseEntity<String> getList() throws Exception {
		System.out.println(stringify(newIDs));
		ResponseEntity<String> response = trackClient
				.get()
				.uri(stringify(newIDs))
				.header("Authorization", "Bearer " + tkn.getToken())
				.retrieve()
				.toEntity(String.class);
		
		return response;
	}
	
	private Integer addToTracks(ResponseEntity<String> getList) 
			throws JsonMappingException, JsonProcessingException {
//		TODO: IMPLEMENT SQL AND DTOs
		int added = 0;
		
		List<ApiTrack> apiTracks = mapper
				.readValue(getList.getBody(), TracksWrapper.class)
				.tracks();
		
		for (ApiTrack track:apiTracks) {
			String spotify_track_id = track.id();
			String name = track.name();
			Integer duration_ms = track.duration_ms();
			String album_id = track.album().id();
			
			Track newTrack = new Track(
					spotify_track_id,
					name,
					duration_ms,
					album_id);
			
			added += trackService.addNewTrack(newTrack);
			
//			List<ApiTrackArtist> artists = track.artists();
		}
		
		return added;
	}
	
	public Integer addNewTracks(String track_id) {
		
		makeList(track_id);
		
		if (newIDs.size() < 20) {
			return 0;
		}
		
		ResponseEntity<String> response = null;
		
		try {
			response = getList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		
		try {
			newIDs.clear();
			return addToTracks(response);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		
	}
}
