package com.github.barmiro.demo_data_generator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.barmiro.demo_data_generator.dto.albums.AlbumsWrapper;
import com.github.barmiro.demo_data_generator.dto.albums.SampleAlbum;

@RestController
public class SearchController {
	private final RestClient apiClient;
	
	public SearchController(RestClient apiClient) {
		this.apiClient = apiClient;
	}
	
	
	@PostMapping("/ids")
	List<String> getAlbumIds(@RequestBody List<SampleAlbum> albumList) throws JsonMappingException, JsonProcessingException {
		
		List<String> albumIDs = new ArrayList<>();
		for (SampleAlbum album:albumList) {
			albumIDs.add(searchForAlbum(album.album(), album.artist()));
		}
		return albumIDs;
	}
	
	
	String searchForAlbum(String albumName, String artistName) throws JsonMappingException, JsonProcessingException {
		String convertedAlbumName = albumName.replace(" ", "%20");
		String convertedArtistName = artistName.replace(" ", "%20");
		
		ResponseEntity<String> response = apiClient
				.get()
				.uri("search?q="
						+ convertedAlbumName
						+ "%20artist:"
						+ convertedArtistName
						+ "&type=album")
				.header("Authorization", "Bearer " + SpotifyTokenService.spotifyToken)
				.retrieve()
				.toEntity(String.class);
		
		ObjectMapper mapper = new ObjectMapper()
				.configure(DeserializationFeature
						.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		return mapper.readValue(response.getBody(), AlbumsWrapper.class)
				.albums()
				.items()
				.getFirst()
				.id();
	
	}
}
