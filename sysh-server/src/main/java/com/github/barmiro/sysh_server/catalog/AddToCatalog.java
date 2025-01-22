package com.github.barmiro.sysh_server.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.stereotype.Service;

import com.github.barmiro.sysh_server.catalog.albums.Album;
import com.github.barmiro.sysh_server.catalog.albums.spotify_api.AlbumApiService;
import com.github.barmiro.sysh_server.catalog.streams.Stream;
import com.github.barmiro.sysh_server.catalog.streams.StreamService;
import com.github.barmiro.sysh_server.catalog.tracks.Track;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.TrackApiService;
@Service
public class AddToCatalog {
	
	TrackApiService trackApiService;
	AlbumApiService albumApiService;
	StreamService streamService;
	
	public AddToCatalog(
			TrackApiService trackApiService,
			AlbumApiService albumApiService,
			StreamService streamService) {
		this.trackApiService = trackApiService;
		this.albumApiService = albumApiService;
		this.streamService = streamService;
	}

	public String adder(List<Stream> streams
			) throws InterruptedException, ExecutionException {
		
		int streamsAdded = 0;
		int tracksAdded = 0;
		int albumsAdded = 0;
		
		List<Track> tracks = new ArrayList<>();
		List<String> trackIDs = new ArrayList<>();
		
		List<Album> albums = new ArrayList<>();
		List<String> albumIDs = new ArrayList<>();
		
		Future<Integer> streamsFuture = streamService.addAllAsync(streams); // ASYNC
		
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
		
		
		
		
		streamsAdded = streamsFuture.get();
		
		return (streamsAdded + " streams added.\n" 
				+ tracksAdded + " tracks added.\n"
				+ albumsAdded + " albums added.\n");
	}
	
}
