package com.github.barmiro.sysh_server.recent;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.barmiro.sysh_server.auth.TokenService;
import com.github.barmiro.sysh_server.catalog.albums.Album;
import com.github.barmiro.sysh_server.catalog.albums.spotify_api.AlbumApiService;
import com.github.barmiro.sysh_server.catalog.tracks.Track;
import com.github.barmiro.sysh_server.catalog.tracks.TrackService;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.TrackApiService;
import com.github.barmiro.sysh_server.recent.dto.ItemsWrapper;
import com.github.barmiro.sysh_server.recent.dto.recentstream.RecentStream;
import com.github.barmiro.sysh_server.streams.Stream;
import com.github.barmiro.sysh_server.streams.StreamService;

@RestController
public class RecentController {
	
	TokenService tkn;
	RestClient apiClient;
	StreamService strs;
	TrackService trs;
	TrackApiService trApi;
	AlbumApiService alApi;
	
	public RecentController(TokenService tkn,
			RestClient apiClient,
			StreamService strs,
			TrackService trs,
			TrackApiService trApi,
			AlbumApiService alApi) {
		this.tkn = tkn;
		this.apiClient = apiClient;
		this.strs = strs;
		this.trs = trs;
		this.trApi = trApi;
		this.alApi = alApi;
	}
	
	ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature
					.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	@GetMapping("/recent")
	public String recent() throws Exception {
		ResponseEntity<String> response = apiClient
				.get()
				.uri("me/player/recently-played")
				.header("Authorization", "Bearer " + tkn.getToken())
				.retrieve()
				.toEntity(String.class);
		
		List<Stream> previous = strs.find(20);
		
		List<Track> tracks = new ArrayList<>();
		List<String> recentIDs = new ArrayList<>();
		
		List<Album> albums = new ArrayList<>();
		List<String> albumIDs = new ArrayList<>();
		
		int streamsAdded = 0;
		int tracksAdded = 0;
		int albumsAdded = 0;
		
		List<RecentStream> items = objectMapper
				.readValue(response.getBody(), ItemsWrapper.class)
				.items();
		
		for (RecentStream item:items) {
			
			Timestamp ts = item.played_at();
			Integer ms_played = item.track().duration_ms();
			String spotify_track_id = item.track().uri().replace("spotify:track:", "");
			
			Stream stream = new Stream(ts, ms_played, spotify_track_id);
						
			if (!previous.contains(stream)) {
				streamsAdded += strs.addNew(stream);
				recentIDs.add(spotify_track_id);
			}
		}
		
		tracks.addAll(trApi.addNewTracks(recentIDs));
		tracksAdded = tracks.size();
		
		for (Track track:tracks) {
			albumIDs.add(track.album_id());
		}
		
		albums.addAll(alApi.addNewAlbums(albumIDs));
		albumsAdded = albums.size();
		
		return (streamsAdded + " streams added.\n" 
				+ tracksAdded + " tracks added.\n"
				+ albumsAdded + " albums added.\n");
	}
}
