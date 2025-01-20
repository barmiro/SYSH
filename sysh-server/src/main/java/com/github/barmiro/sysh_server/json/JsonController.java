package com.github.barmiro.sysh_server.json;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.barmiro.sysh_server.catalog.albums.Album;
import com.github.barmiro.sysh_server.catalog.albums.spotify_api.AlbumApiService;
import com.github.barmiro.sysh_server.catalog.tracks.Track;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.TrackApiService;
import com.github.barmiro.sysh_server.streams.Stream;
import com.github.barmiro.sysh_server.streams.StreamService;

@RestController
public class JsonController {
	
	private final StreamService streamService;
	private final TrackApiService trackApiService;
	private final JsonService jsonService;
	private final AlbumApiService albumApiService;
	
	JsonController(StreamService streamService,
			TrackApiService trackApiService,
			JsonService jsonService,
			AlbumApiService albumApiService) {
		this.streamService = streamService;
		this.trackApiService = trackApiService;
		this.jsonService = jsonService;
		this.albumApiService = albumApiService;
	}
	
	@PostMapping("/addJson")
	String addJson(@RequestBody List<StreamDTO> streamDTOs) {
		long start = System.currentTimeMillis();
		Integer streamsAdded = 0;
		Integer tracksAdded = 0;
		Integer albumsAdded = 0;
		
		List<Stream> streams = jsonService.convertStreamDTOs(streamDTOs);
		streamsAdded = streamService.addAll(streams);
		
		List<Track> tracks = new ArrayList<>();
		List<String> trackIDs = new ArrayList<>();
		
		List<Album> albums = new ArrayList<>();
		List<String> albumIDs = new ArrayList<>();
		
		for (Stream stream:streams) {
			trackIDs.add(stream.spotify_track_id());
		}
		
		tracks.addAll(trackApiService.addNewTracks(trackIDs));
		tracksAdded = tracks.size();
		
		
		for (Track track:tracks) {
			albumIDs.add(track.album_id());
		}
		
		albums.addAll(albumApiService.addNewAlbums(albumIDs));
		albumsAdded = albums.size();
		
		
		long end = System.currentTimeMillis();
		long time = (end - start) / 1000;
		System.out.println("Time elapsed: " + time);
		return (streamsAdded + " streams added.\n" 
				+ tracksAdded + " tracks added.\n"
				+ albumsAdded + " albums added.\n");
	}
}
