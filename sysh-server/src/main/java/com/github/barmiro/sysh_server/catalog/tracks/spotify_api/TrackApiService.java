package com.github.barmiro.sysh_server.catalog.tracks.spotify_api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.barmiro.sysh_server.auth.TokenService;
import com.github.barmiro.sysh_server.catalog.SpotifyApiService;
import com.github.barmiro.sysh_server.catalog.tracks.Track;
import com.github.barmiro.sysh_server.catalog.tracks.TrackService;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.TracksWrapper;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.ApiTrack;

@Service
public class TrackApiService extends SpotifyApiService<TrackService, Track> {


	TrackApiService(JdbcClient jdbc, RestClient apiClient, TokenService tkn, TrackService catalogService) {
		super(jdbc, apiClient, tkn, catalogService);
	}

	private List<ApiTrack> mapApiTracks(ResponseEntity<String> response
			) throws JsonMappingException, JsonProcessingException {
		
		return mapper
				.readValue(response.getBody(), TracksWrapper.class)
				.tracks();
	}
	
	public List<Track> convertApiTracks(List<ApiTrack> apiTracks) {
		
		List<Track> addedTracks = new ArrayList<>();
		
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
			
			addedTracks.add(newTrack);
		}
		
		return addedTracks;
	}
	

	public List<Track> addNewTracks(List<String> track_ids) {
		
		List<String> newIDs = getNewIDs(track_ids, "spotify_track_id", Track.class);
		
		List<String> packets = new ArrayList<>();
		try {
			packets = prepIdPackets(newIDs, Track.class, 50);			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Method prepIdPackets threw an exception.");
			return new ArrayList<Track>();
		}
		
		List<ApiTrack> apiTracks = new ArrayList<>();
		for (String packet:packets) {
			
			ResponseEntity<String> response = null;
			response = getResponse(packet);
			
			if (response == null) {
				System.out.println("Response for " + packet + " is null.");
				continue;
			}
			
			try {
				apiTracks.addAll(mapApiTracks(response));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				System.out.println("Method mapApiTracks threw an exception");
			}
		}
		
		if (apiTracks.size() == 0) {
			System.out.println("No tracks to add.");
			return new ArrayList<Track>();
		}
		
		List<Track> tracks = convertApiTracks(apiTracks);
		
		Integer tracksAdded = catalogService.addTracks(tracks);
		
		System.out.println(tracksAdded + " new tracks added");
		return tracks;
		
	}
}
