package com.github.barmiro.sysh_server.catalog.tracks.spotify_api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.barmiro.sysh_server.auth.TokenService;
import com.github.barmiro.sysh_server.catalog.SpotifyApiService;
import com.github.barmiro.sysh_server.catalog.albums.spotify_api.AlbumApiService;
import com.github.barmiro.sysh_server.catalog.tracks.Track;
import com.github.barmiro.sysh_server.catalog.tracks.TrackService;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.TracksWrapper;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.ApiTrack;

@Service
public class TrackApiService extends SpotifyApiService<TrackService, Track> {

	private final AlbumApiService albumApiService;
	TrackApiService(JdbcClient jdbc, RestClient apiClient, TokenService tkn, TrackService catalogService, AlbumApiService albumApiService) {
		super(jdbc, apiClient, tkn, catalogService);
		this.albumApiService = albumApiService;
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
			
			added += catalogService.addNewTrack(newTrack);
			added += (albumApiService.addNewAlbums(album_id, false) * 1000000);
//			List<ApiTrackArtist> artists = track.artists();
		}
		added += (albumApiService.addNewAlbums("", true) * 1000000);
		return added;
	}
	
	public Integer addNewTracks(String track_id, boolean end) {
		
		makeList(track_id, "spotify_track_id", Track.class);
		
		if (newIDs.size() < 50 && !end) {
			return 0;
		}
		
		ResponseEntity<String> response = null;
		
		try {
			response = getList(newIDs, Track.class, 50);
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
