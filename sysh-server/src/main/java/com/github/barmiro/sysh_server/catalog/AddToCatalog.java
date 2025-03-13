package com.github.barmiro.sysh_server.catalog;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.barmiro.sysh_server.catalog.albums.Album;
import com.github.barmiro.sysh_server.catalog.albums.AlbumRepository;
import com.github.barmiro.sysh_server.catalog.artists.Artist;
import com.github.barmiro.sysh_server.catalog.artists.spotifyapi.ArtistApiRepository;
import com.github.barmiro.sysh_server.catalog.jointables.AlbumsTracks;
import com.github.barmiro.sysh_server.catalog.jointables.TracksArtists;
import com.github.barmiro.sysh_server.catalog.streams.SongStream;
import com.github.barmiro.sysh_server.catalog.streams.StreamRepository;
import com.github.barmiro.sysh_server.catalog.tracks.Track;
import com.github.barmiro.sysh_server.catalog.tracks.TrackRepository;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.TrackApiRepository;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.ApiTrack;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.album.ApiTrackAlbum;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.artists.ApiTrackArtist;
import com.github.barmiro.sysh_server.common.utils.ConvertDTOs;
import com.github.barmiro.sysh_server.spotifyauthorization.SpotifyTokenService;
import com.github.barmiro.sysh_server.stats.StatsCache;

@Repository
public class AddToCatalog {
	
//	this list is getting pretty long, I don't want this to become a god object
	TrackRepository trackRepository;
	TrackApiRepository trackApiRepository;
	AlbumRepository albumRepository;
	ArtistApiRepository artistApiRepository;
	StreamRepository streamRepository;
	AlbumsTracks albumsTracks;
	TracksArtists tracksArtists;
	SpotifyTokenService tkn;
	StatsCache statsCache;
	
	public AddToCatalog(
			TrackRepository trackRepository,
			TrackApiRepository trackApiRepository,
			AlbumRepository albumRepository,
			ArtistApiRepository artistApiRepository,
			StreamRepository streamRepository,
			AlbumsTracks albumsTracks,
			TracksArtists tracksArtists,
			SpotifyTokenService tkn,
			StatsCache statsCache) {
		this.trackRepository = trackRepository;
		this.trackApiRepository = trackApiRepository;
		this.albumRepository = albumRepository;
		this.artistApiRepository = artistApiRepository;
		this.streamRepository = streamRepository;
		this.albumsTracks = albumsTracks;
		this.tracksArtists = tracksArtists;
		this.tkn = tkn;
		this.statsCache = statsCache;
	}

	@Transactional
	public String adder(List<SongStream> streams, String username) throws JsonProcessingException, ClassCastException, IllegalAccessException, InvocationTargetException {
		
		tkn.refresh(username);
		
		int streamsAdded = 0;
		int tracksAdded = 0;
		int albumsAdded = 0;
		int artistsAdded = 0;
		
		List<ApiTrack> apiTracks = new ArrayList<>();
		List<Track> tracks = new ArrayList<>();
		List<String> trackIDs = new ArrayList<>();
		
		List<Album> albums = new ArrayList<>();
		
		List<Artist> artists = new ArrayList<>();
		List<String> artistIDs = new ArrayList<>();
		

//		this used to performed asynchronously
//		but async methods perform separate transactions :(
		streamsAdded = streamRepository.addAll(streams);
		
		for (SongStream stream:streams) {
			trackIDs.add(stream.spotify_track_id());
		}
		
		apiTracks.addAll(trackApiRepository.getApiTracks(trackIDs, username));
		tracks.addAll(trackApiRepository.addNewTracks(apiTracks));
		tracksAdded = tracks.size();
		
		
		List <ApiTrackAlbum> apiTrackAlbums = new ArrayList<>();
		for (ApiTrack apiTrack:apiTracks) {
			if (!apiTrackAlbums.contains(apiTrack.album())) {
				apiTrackAlbums.add(apiTrack.album());				
			}
		}
		albums.addAll(ConvertDTOs.apiTrackAlbums(apiTrackAlbums));
		albumsAdded = albumRepository.addAlbums(albums);
		
		albumsTracks.updateJoinTable(tracks);
		
		for (ApiTrack apiTrack:apiTracks) {
			for (ApiTrackArtist artist:apiTrack.artists()) {
				artistIDs.add(artist.id());
			}
		}
		
		artists.addAll(artistApiRepository.addNewArtists(artistIDs, username));
		artistsAdded = artists.size();
		
		tracksArtists.updateJoinTable(apiTracks);
		
//		the <= 50 condition limits this cache update to recent streams,
//		easier than returning all necessary values and handling it in recent logic
		if (streams.size() > 0 && streams.size() <= 50) {
			statsCache.updateCache(streams, username, tracksAdded, albumsAdded, artistsAdded);
			trackRepository.updateTopTracksCache(username);
			albumRepository.updateTopAlbumsCache(username);
			
		}
		
		return (streamsAdded + " streams added.\n" 
				+ tracksAdded + " tracks added.\n"
				+ albumsAdded + " albums added.\n"
				+ artistsAdded + " artists added.\n");
	}
	
}
