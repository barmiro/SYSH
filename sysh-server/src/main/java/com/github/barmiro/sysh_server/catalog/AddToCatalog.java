package com.github.barmiro.sysh_server.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.stereotype.Repository;

import com.github.barmiro.sysh_server.catalog.albums.Album;
import com.github.barmiro.sysh_server.catalog.albums.spotify_api.AlbumApiRepository;
import com.github.barmiro.sysh_server.catalog.streams.Stream;
import com.github.barmiro.sysh_server.catalog.streams.StreamRepository;
import com.github.barmiro.sysh_server.catalog.tracks.Track;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.TrackApiRepository;

@Repository
public class AddToCatalog {
	
	TrackApiRepository trackApiRepository;
	AlbumApiRepository albumApiRepository;
	StreamRepository streamRepository;
	
	public AddToCatalog(
			TrackApiRepository trackApiRepository,
			AlbumApiRepository albumApiRepository,
			StreamRepository streamRepository) {
		this.trackApiRepository = trackApiRepository;
		this.albumApiRepository = albumApiRepository;
		this.streamRepository = streamRepository;
	}

	public String adder(List<Stream> streams) {
		
		int streamsAdded = 0;
		int tracksAdded = 0;
		int albumsAdded = 0;
		
		List<Track> tracks = new ArrayList<>();
		List<String> trackIDs = new ArrayList<>();
		
		List<Album> albums = new ArrayList<>();
		List<String> albumIDs = new ArrayList<>();
		
		Future<Integer> streamsFuture = streamRepository.addAllAsync(streams); // ASYNC
		
		for (Stream stream:streams) {
			trackIDs.add(stream.spotify_track_id());
		}
		
		tracks.addAll(trackApiRepository.addNewTracks(trackIDs));
		tracksAdded = tracks.size();
		
		
		for (Track track:tracks) {
			albumIDs.add(track.album_id());
		}
		
		albums.addAll(albumApiRepository.addNewAlbums(albumIDs));
		albumsAdded = albums.size();
		
		
		
		
		try {
			streamsAdded = streamsFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return "Something went wrong with the async method";
		}
		
		return (streamsAdded + " streams added.\n" 
				+ tracksAdded + " tracks added.\n"
				+ albumsAdded + " albums added.\n");
	}
	
}
