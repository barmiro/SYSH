package com.github.barmiro.sysh_server.catalog.albums.spotifyapideprecated;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.barmiro.sysh_server.catalog.albums.Album;
import com.github.barmiro.sysh_server.catalog.albums.AlbumRepository;
import com.github.barmiro.sysh_server.catalog.albums.spotifyapideprecated.dto.AlbumsWrapper;
import com.github.barmiro.sysh_server.catalog.albums.spotifyapideprecated.dto.albums.ApiAlbum;
import com.github.barmiro.sysh_server.catalog.interfaces.SpotifyApiRepository;
import com.github.barmiro.sysh_server.common.utils.ConvertDTOs;
import com.github.barmiro.sysh_server.spotifyauthorization.SpotifyTokenService;

@Repository
public class AlbumApiRepository extends SpotifyApiRepository<
											AlbumRepository,
											Album,
											ApiAlbum,
											AlbumsWrapper> {

	AlbumApiRepository(JdbcClient jdbc, RestClient apiClient, SpotifyTokenService tkn, AlbumRepository catalogRepository) {
		super(jdbc, apiClient, tkn, catalogRepository);
	}


	
	public List<Album> addNewAlbums(List<String> album_ids, String username) {
		
		List<String> newIDs = getNewIDs(album_ids, "id");
		System.out.println("Found " + newIDs.size() + " new albums.");
		
		List<String> packets = new ArrayList<>();

		try {
			packets = prepIdPackets(newIDs, 20);			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Method prepIdPackets threw an exception.");
			return new ArrayList<Album>();
		}
		
		List<ApiAlbum> apiAlbums = new ArrayList<>();
		for (String packet:packets) {
			ResponseEntity<String> response = getResponse(packet, username);				

			try {
				apiAlbums.addAll(mapResponse(response));
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
