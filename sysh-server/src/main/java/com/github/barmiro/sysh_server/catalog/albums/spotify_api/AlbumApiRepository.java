package com.github.barmiro.sysh_server.catalog.albums.spotify_api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.barmiro.sysh_server.auth.TokenService;
import com.github.barmiro.sysh_server.catalog.SpotifyApiRepository;
import com.github.barmiro.sysh_server.catalog.albums.Album;
import com.github.barmiro.sysh_server.catalog.albums.AlbumRepository;
import com.github.barmiro.sysh_server.catalog.albums.spotify_api.dto.AlbumsWrapper;
import com.github.barmiro.sysh_server.catalog.albums.spotify_api.dto.albums.ApiAlbum;
import com.github.barmiro.sysh_server.integration.json.ConvertDTOs;

@Repository
public class AlbumApiRepository extends SpotifyApiRepository<
											AlbumRepository,
											Album,
											ApiAlbum,
											AlbumsWrapper> {

	AlbumApiRepository(JdbcClient jdbc, RestClient apiClient, TokenService tkn, AlbumRepository catalogRepository) {
		super(jdbc, apiClient, tkn, catalogRepository);
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
			ResponseEntity<String> response = getResponse(packet);				

			try {
				apiAlbums.addAll(mapResponse(response, AlbumsWrapper.class));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				System.out.println("Method mapApiAlbums threw an exception");
			}
		}
		
		if (apiAlbums.size() == 0) {
			System.out.println("No albums to add.");
			return new ArrayList<Album>();
		}
		
		List<Album> albums = ConvertDTOs.apiAlbums(apiAlbums);
		
		catalogRepository.addAlbums(albums);
		
		return albums;
	}
}
