package com.github.barmiro.sysh_server.catalog;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.github.barmiro.sysh_server.catalog.albums.Album;
import com.github.barmiro.sysh_server.catalog.albums.AlbumRepository;
import com.github.barmiro.sysh_server.catalog.artists.Artist;
import com.github.barmiro.sysh_server.catalog.artists.spotify_api.ArtistApiRepository;
import com.github.barmiro.sysh_server.catalog.jointables.AlbumsTracks;
import com.github.barmiro.sysh_server.catalog.streams.Stream;
import com.github.barmiro.sysh_server.catalog.streams.StreamRepository;
import com.github.barmiro.sysh_server.catalog.tracks.Track;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.TrackApiRepository;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.ApiTrack;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.album.ApiTrackAlbum;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.artists.ApiTrackArtist;
import com.github.barmiro.sysh_server.common.utils.ConvertDTOs;

@Repository
public class AddToCatalog {
	
	TrackApiRepository trackApiRepository;
	AlbumRepository albumRepository;
	ArtistApiRepository artistApiRepository;
	StreamRepository streamRepository;
	AlbumsTracks albumsTracks;
	
	public AddToCatalog(
			TrackApiRepository trackApiRepository,
			AlbumRepository albumRepository,
			ArtistApiRepository artistApiRepository,
			StreamRepository streamRepository,
			AlbumsTracks albumsTracks) {
		this.trackApiRepository = trackApiRepository;
		this.albumRepository = albumRepository;
		this.artistApiRepository = artistApiRepository;
		this.streamRepository = streamRepository;
		this.albumsTracks = albumsTracks;
	}

	@Transactional
	public String adder(List<Stream> streams) {
		
		int streamsAdded = 0;
		int tracksAdded = 0;
		int albumsAdded = 0;
		int artistsAdded = 0;
		
		List<ApiTrack> apiTracks = new ArrayList<>();
		List<Track> tracks = new ArrayList<>();
		List<String> trackIDs = new ArrayList<>();
		
		List<Album> albums = new ArrayList<>();
//		List<String> albumIDs = new ArrayList<>();
		
		List<Artist> artists = new ArrayList<>();
		List<String> artistIDs = new ArrayList<>();
		

//		this used to performed asynchronously
//		but async methods perform separate transactions :(
		streamsAdded = streamRepository.addAll(streams);
		
		for (Stream stream:streams) {
			trackIDs.add(stream.spotify_track_id());
		}
		
		apiTracks.addAll(trackApiRepository.getApiTracks(trackIDs));
		tracks.addAll(trackApiRepository.addNewTracks(apiTracks));
		tracksAdded = tracks.size();
		
		
		
		
//		for (Track track:tracks) {
//			albumIDs.add(track.album_id());
//		}
		
		List <ApiTrackAlbum> apiTrackAlbums = new ArrayList<>();
		for (ApiTrack apiTrack:apiTracks) {
			if (!apiTrackAlbums.contains(apiTrack.album())) {
				apiTrackAlbums.add(apiTrack.album());				
			}
		}
		albums.addAll(ConvertDTOs.apiTrackAlbums(apiTrackAlbums));
		albumsAdded = albumRepository.addAlbums(albums);
		
		
//		albums.addAll(albumApiRepository.addNewAlbums(albumIDs));
//		albumsAdded = albums.size();
		albumsTracks.join(tracks);
		
		
		for (ApiTrack apiTrack:apiTracks) {
			for (ApiTrackArtist artist:apiTrack.artists()) {
				artistIDs.add(artist.id());
			}
		}
		
		artists.addAll(artistApiRepository.addNewArtists(artistIDs));
		artistsAdded = artists.size();
		

		return (streamsAdded + " streams added.\n" 
				+ tracksAdded + " tracks added.\n"
				+ albumsAdded + " albums added.\n"
				+ artistsAdded + " artists added.\n");
	}
	
}
