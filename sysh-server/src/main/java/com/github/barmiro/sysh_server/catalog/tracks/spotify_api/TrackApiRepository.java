package com.github.barmiro.sysh_server.catalog.tracks.spotify_api;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.barmiro.sysh_server.catalog.interfaces.SpotifyApiRepository;
import com.github.barmiro.sysh_server.catalog.tracks.Track;
import com.github.barmiro.sysh_server.catalog.tracks.TrackRepository;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.TracksWrapper;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.ApiTrack;
import com.github.barmiro.sysh_server.common.utils.ConvertDTOs;
import com.github.barmiro.sysh_server.spotifyauthorization.SpotifyTokenService;

@Repository
public class TrackApiRepository extends SpotifyApiRepository<
											TrackRepository,
											Track,
											ApiTrack,
											TracksWrapper> {


	public TrackApiRepository(JdbcClient jdbc, RestClient apiClient, SpotifyTokenService tkn, TrackRepository catalogRepository) {
		super(jdbc, apiClient, tkn, catalogRepository);
	}
	
	private static final Logger log = LoggerFactory.getLogger(TrackApiRepository.class);

	public List<ApiTrack> getApiTracks(List<String> track_ids, String username) throws JsonProcessingException, ClassCastException {
		
		// Safety check: ensure we have IDs to look for
		if (track_ids == null || track_ids.isEmpty()) {
			return new ArrayList<>();
		}

		// Defensive: Make sure we aren't looking up the same ID twice in one go
		List<String> uniqueIds = track_ids.stream().distinct().toList();

		List<String> newIDs = getNewIDs(uniqueIds, "spotify_track_id");
		log.info("Found " + newIDs.size() + " new tracks.");

		if (newIDs.isEmpty()) {
			return new ArrayList<>();
		}

		List<String> packets = prepIdPackets(newIDs, 50);
		
		List<ApiTrack> apiTracks = new ArrayList<>();
		for (String packet:packets) {
			try {
				// Sleep for 1000ms (1 second) between batches
				Thread.sleep(1000); 
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				log.error("Thread was interrupted", e);
			}

			ResponseEntity<String> response = getResponse(packet, username);
			
			if (response == null) {
				log.error("Response for " + packet + " is null.");
				continue;
			}

			apiTracks.addAll(mapResponse(response));

		}
		
		if (apiTracks.size() == 0) {
			log.info("No tracks to add.");
			return new ArrayList<ApiTrack>();
		}
		
		return apiTracks;
	}
		
	public List<Track> addNewTracks (List<ApiTrack> apiTracks) throws IllegalAccessException, InvocationTargetException {
		
		// If there's nothing to add, stop here to prevent NPEs in ConvertDTOs
		if (apiTracks == null || apiTracks.isEmpty()) {
			return new ArrayList<>();
		}

		List<Track> tracks = ConvertDTOs.apiTracks(apiTracks);
		
		if (tracks != null && !tracks.isEmpty()) {
			catalogRepository.addTracks(tracks);
		}
		
		return tracks != null ? tracks : new ArrayList<>();
	}
}
