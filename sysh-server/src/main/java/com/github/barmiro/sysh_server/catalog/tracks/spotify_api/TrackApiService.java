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

	private final JdbcClient jdbc;
	private final RestClient apiClient;
	private final TokenService tkn;
	private final TrackService trackService;
	
	TrackApiService(JdbcClient jdbc, 
			RestClient apiClient, 
			TokenService tkn,
			TrackService trackService) {
		this.jdbc = jdbc;
		this.apiClient = apiClient;
		this.tkn = tkn;
		this.trackService = trackService;
	}
	
	private List<String> newIDs = new ArrayList<>();
	
	ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature
					.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	
	void makeList(String trackID) {
		if (trackID == "") {
			return;
		}
		int exists = jdbc.sql("SELECT * FROM Tracks "
				+ "WHERE spotify_track_id = :trackID "
				+ "LIMIT 1")
				.param("trackID", trackID, Types.VARCHAR)
				.query(Track.class)
				.list()
				.size();
		
		if (exists == 0 && !newIDs.contains(trackID)) {
			newIDs.add(trackID);
		} else {
			System.out.println("Failed: Track " + trackID + "already exists");
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
	
	private ResponseEntity<String> getList(List<String> IDlist) throws Exception {
		
		if (IDlist.size() == 0 || IDlist.size() > 50) {
			return null;
		}
		ResponseEntity<String> response = apiClient
				.get()
				.uri(stringify(IDlist))
				.header("Authorization", "Bearer " + tkn.getToken())
				.retrieve()
				.toEntity(String.class);
		
		return response;
	}
	
	private Integer addToTracks(ResponseEntity<String> getList) 
			throws JsonMappingException, JsonProcessingException {

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
	
	public Integer addNewTracks(String track_id, boolean end) {
		
		makeList(track_id);
		
		if (newIDs.size() < 50 && !end) {
			return 0;
		}
		
		ResponseEntity<String> response = null;
		
		try {
			response = getList(newIDs);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		
		if (response == null) {
			System.out.println("The ID list is either empty or too big.");
			return 0;
		}
		try {
			newIDs.clear();
			return addToTracks(response);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return 0;
		}
		
	}
}
