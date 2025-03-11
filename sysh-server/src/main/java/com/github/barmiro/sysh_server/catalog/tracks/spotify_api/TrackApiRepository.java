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
		
		List<String> newIDs = getNewIDs(track_ids, "spotify_track_id");
		log.info("Found " + newIDs.size() + " new tracks.");
		List<String> packets = new ArrayList<>();
		
		packets = prepIdPackets(newIDs, 50);			

		
		List<ApiTrack> apiTracks = new ArrayList<>();
		for (String packet:packets) {
			
			ResponseEntity<String> response = null;
			response = getResponse(packet, username);
			
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
		
		List<Track> tracks = ConvertDTOs.apiTracks(apiTracks);
		
		catalogRepository.addTracks(tracks);
		
		return tracks;
		
	}
}
