package com.github.barmiro.sysh_server.catalog.albums.spotify_api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.barmiro.sysh_server.auth.TokenService;
import com.github.barmiro.sysh_server.catalog.SpotifyApiService;
import com.github.barmiro.sysh_server.catalog.albums.Album;
import com.github.barmiro.sysh_server.catalog.albums.AlbumService;
import com.github.barmiro.sysh_server.catalog.albums.spotify_api.dto.AlbumsWrapper;
import com.github.barmiro.sysh_server.catalog.albums.spotify_api.dto.albums.ApiAlbum;

@Service
public class AlbumApiService extends SpotifyApiService<AlbumService, Album>{

	AlbumApiService(JdbcClient jdbc, RestClient apiClient, TokenService tkn, AlbumService catalogService) {
		super(jdbc, apiClient, tkn, catalogService);
	}

	private Integer addToAlbums(ResponseEntity<String> getList) 
			throws JsonMappingException, JsonProcessingException {

		int added = 0;
		
		List<ApiAlbum> apiAlbums = mapper
				.readValue(getList.getBody(), AlbumsWrapper.class)
				.albums();
		
		for (ApiAlbum album:apiAlbums) {
			String id = album.id();
			String name = album.name();
			Integer total_tracks = album.total_tracks();
			String release_date = album.release_date();
			
			Album newAlbum = new Album (
					id,
					name,
					total_tracks,
					release_date);

			
			added += catalogService.addAlbum(newAlbum);
			
//			List<ApiTrackArtist> artists = track.artists();
		}
		
		return added;
	}
	
	public Integer addNewAlbums(String id, boolean end) {
		
		makeList(id, "id", Album.class);
		
		if (newIDs.size() < 20 && !end) {
			return 0;
		}
		
		ResponseEntity<String> response = null;
		
		try {
			response = getList(newIDs, Album.class, 50);
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
			return addToAlbums(response);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return 0;
		}
		
	}
}
