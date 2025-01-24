package com.github.barmiro.sysh_server.catalog.tracks.spotify_api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.barmiro.sysh_server.auth.TokenService;
import com.github.barmiro.sysh_server.catalog.SpotifyApiRepository;
import com.github.barmiro.sysh_server.catalog.tracks.Track;
import com.github.barmiro.sysh_server.catalog.tracks.TrackRepository;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.TracksWrapper;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.ApiTrack;
import com.github.barmiro.sysh_server.integration.json.ConvertDTOs;

@Repository
public class TrackApiRepository extends SpotifyApiRepository<
											TrackRepository,
											Track,
											ApiTrack,
											TracksWrapper> {


	TrackApiRepository(JdbcClient jdbc, RestClient apiClient, TokenService tkn, TrackRepository catalogRepository) {
		super(jdbc, apiClient, tkn, catalogRepository);
	}
	

	public List<ApiTrack> getApiTracks(List<String> track_ids) {
		
		List<String> newIDs = getNewIDs(track_ids, "spotify_track_id");
		System.out.println("Found " + newIDs.size() + " new tracks.");
		List<String> packets = new ArrayList<>();
		try {
			packets = prepIdPackets(newIDs, 50);			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Method prepIdPackets threw an exception.");
			return new ArrayList<ApiTrack>();
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
				apiTracks.addAll(mapResponse(response));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				System.out.println("Method mapApiTracks threw an exception");
			}
		}
		
		if (apiTracks.size() == 0) {
			System.out.println("No tracks to add.");
			return new ArrayList<ApiTrack>();
		}
		
		return apiTracks;
	}
		
	public List<Track> addNewTracks (List<ApiTrack> apiTracks) {
		
		List<Track> tracks = ConvertDTOs.apiTracks(apiTracks);
		
		catalogRepository.addTracks(tracks);
		
		return tracks;
		
	}
}
