package com.github.barmiro.sysh_server.catalog.albums.spotify_api;

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
import com.github.barmiro.sysh_server.catalog.albums.Album;
import com.github.barmiro.sysh_server.catalog.albums.AlbumService;
import com.github.barmiro.sysh_server.catalog.albums.spotify_api.dto.AlbumsWrapper;
import com.github.barmiro.sysh_server.catalog.albums.spotify_api.dto.albums.ApiAlbum;

@Service
public class AlbumApiService extends SpotifyApiService<AlbumService, Album>{

	AlbumApiService(JdbcClient jdbc, RestClient apiClient, TokenService tkn, AlbumService catalogService) {
		super(jdbc, apiClient, tkn, catalogService);
	}

	private List<ApiAlbum> mapApiAlbums(ResponseEntity<String> response
			) throws JsonMappingException, JsonProcessingException {
		
		return mapper
				.readValue(response.getBody(), AlbumsWrapper.class)
				.albums();
	}
	
	public List<Album> convertApiAlbums(List<ApiAlbum> apiAlbums) {
		
		List<Album> addedAlbums = new ArrayList<>();
		
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
			
			addedAlbums.add(newAlbum);
		}
		
		return addedAlbums;
	}
	

	
	public List<Album> addNewAlbums(List<String> album_ids) {
		
		
		List<String> newIDs = getNewIDs(album_ids, "id", Album.class);
		
		System.out.println("Found " + newIDs.size() + " new albums.");
		
		List<String> packets = new ArrayList<>();
		try {
			packets = prepIdPackets(newIDs, Album.class, 20);			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Method prepIdPackets threw an exception.");
			return new ArrayList<Album>();
		}
		
		List<ApiAlbum> apiAlbums = new ArrayList<>();
		for (String packet:packets) {
			
			ResponseEntity<String> response = null;
			response = getResponse(packet);
			
			if (response == null) {
				continue;
			}
			
			try {
				apiAlbums.addAll(mapApiAlbums(response));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				System.out.println("Method mapApiAlbums threw an exception");
			}
		}
		
		if (apiAlbums.size() == 0) {
			System.out.println("No albums to add.");
			return new ArrayList<Album>();
		}
		
		List<Album> albums = convertApiAlbums(apiAlbums);
		
		Integer albumsAdded = catalogService.addAlbums(albums);
		

		return albums;
		
	}
}
