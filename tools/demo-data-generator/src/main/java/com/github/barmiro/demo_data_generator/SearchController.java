package com.github.barmiro.demo_data_generator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.barmiro.demo_data_generator.dto.albums.AlbumsWrapper;
import com.github.barmiro.demo_data_generator.dto.albums.ApiAlbum;
import com.github.barmiro.demo_data_generator.dto.albums.tracks.items.ApiAlbumTrack;
import com.github.barmiro.demo_data_generator.dto.searchalbums.SampleAlbum;
import com.github.barmiro.demo_data_generator.dto.searchalbums.SearchAlbumsWrapper;

@RestController
public class SearchController {
	private final RestClient apiClient;
	
	public SearchController(RestClient apiClient) {
		this.apiClient = apiClient;
	}
	
	ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature
					.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	List<SampleAlbum> albumStore = new ArrayList<>();
	List<String> trackIDStore = new ArrayList<>();
	
	@PostMapping("/idSchedule")
	List<SampleAlbum> scheduleGetTrackIds(@RequestBody List<SampleAlbum> albumList) {
		albumStore = albumList;
		return albumStore;
	}
	
	@Scheduled(fixedRate = 30000)
	void processIdsCron() throws JsonMappingException, JsonProcessingException {
		if (!albumStore.isEmpty()) {
			System.out.println(albumStore.size() + " albums left, adding...");
			List<SampleAlbum> albums = new ArrayList<>();
			for (int i = 0; i < 20; i++) {
				if (!albumStore.isEmpty()) {
					albums.add(albumStore.removeFirst());				
				}
			}
			System.out.println(albums.getFirst());
			trackIDStore.addAll(getTrackIds(albums));
		} else {
			System.out.println("albumStore is empty");
		}

	}
	
	@GetMapping("/getProcessedIds")
	List<String> getProcessedIds() {
		return trackIDStore;
	}
	
	
	@PostMapping("/ids")
	List<String> getTrackIds(@RequestBody List<SampleAlbum> albumList) throws JsonMappingException, JsonProcessingException {
		
		List<String> albumIDs = new ArrayList<>();
		for (SampleAlbum album:albumList) {
			albumIDs.add(searchForAlbum(album.album(), album.artist(), album.year()));
		}
		System.out.println(albumIDs);
		return albumTrackIDs(albumIDs);
	}
	
	
	String searchForAlbum(String albumName, String artistName, Integer year) throws JsonMappingException, JsonProcessingException {
		String convertedAlbumName = albumName.replace(" ", "%20");
		String convertedArtistName = artistName.replace(" ", "%20");
		
		ResponseEntity<String> response = apiClient
				.get()
				.uri("search?q="
						+ convertedAlbumName
						+ "%20album:"
						+ convertedAlbumName
						+ "%20artist:"
						+ convertedArtistName
						+ "%20year:"
						+ year
						+ "&type=album")
				.header("Authorization", "Bearer " + SpotifyTokenService.spotifyToken)
				.retrieve()
				.toEntity(String.class);
		

		
		return mapper.readValue(response.getBody(), SearchAlbumsWrapper.class)
				.albums()
				.items()
				.getFirst()
				.id();
	
	}
	
	List<String> albumTrackIDs(List<String> albumIDs) {
		List<String> trackIDs = new ArrayList<>();
		List<String> albumPackets = prepIdPackets(albumIDs, 20);
		
		List<ApiAlbum> apiAlbums = new ArrayList<>();
		
		for (String packet:albumPackets) {		
			ResponseEntity<String> response = apiClient
					.get()
					.uri(packet)
					.header("Authorization", "Bearer " + SpotifyTokenService.spotifyToken)
					.retrieve()
					.toEntity(String.class);
			try {
				apiAlbums.addAll(mapper.readValue(response.getBody(), AlbumsWrapper.class).albums());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				System.out.println("Method mapApiAlbums threw an exception");
			}
		}
		
		for (ApiAlbum album:apiAlbums) {
			for (ApiAlbumTrack track:album.tracks().items()) {
				trackIDs.add(track.id());
			}
		}
		return trackIDs;
	}
	
	
	List<String> prepIdPackets(
			List<String> IDlist,
			int limit
			) {

		int listSize = IDlist.size();
		
		List<String> idPackets = new ArrayList<>();
		
		for(int i = 0; i < listSize; i += limit) {
			if (i + limit >= listSize) {
				limit = listSize - i;
			}
			String idPacket = stringify(IDlist.subList(i, i + limit));
			idPackets.add(idPacket);
		}
		
		return idPackets;
	}
	
	
	String stringify(List<String> newIDs) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("albums?ids=");
		
		for (String id:newIDs) {
			sb.append(id + ",");
		}
		
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	
}
