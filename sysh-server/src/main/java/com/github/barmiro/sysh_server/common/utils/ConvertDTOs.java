package com.github.barmiro.sysh_server.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.barmiro.sysh_server.catalog.albums.Album;
import com.github.barmiro.sysh_server.catalog.albums.spotify_api.dto.albums.ApiAlbum;
import com.github.barmiro.sysh_server.catalog.artists.Artist;
import com.github.barmiro.sysh_server.catalog.artists.spotify_api.dto.artists.ApiArtist;
import com.github.barmiro.sysh_server.catalog.streams.Stream;
import com.github.barmiro.sysh_server.catalog.tracks.Track;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.ApiTrack;
import com.github.barmiro.sysh_server.dataintake.json.StreamDTO;
import com.github.barmiro.sysh_server.dataintake.recent.dto.ItemsWrapper;
import com.github.barmiro.sysh_server.dataintake.recent.dto.recentstream.RecentStream;

public class ConvertDTOs {
	
	
	public static List<Stream> streamsJson(List<StreamDTO> streamDTOs) {
		List<Stream> streams = new ArrayList<>();
		
		for (StreamDTO streamDTO:streamDTOs) {
			if (streamDTO.spotify_track_uri() != null) {
				Stream stream = new Stream(streamDTO.ts(),
						streamDTO.ms_played(),
						streamDTO.spotify_track_uri().replace("spotify:track:", ""));
				
				streams.add(stream);
			}
		}
		return streams;
	}
	
	
	public static List<Stream> streamsRecent(
			ResponseEntity<String> response,
			List<Stream> previous) {
		ObjectMapper objectMapper = new ObjectMapper()
				.configure(DeserializationFeature
						.FAIL_ON_UNKNOWN_PROPERTIES, false);
		List<RecentStream> items;
		
		try {
			items = objectMapper
					.readValue(response.getBody(), ItemsWrapper.class)
					.items();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new ArrayList<Stream>();
		}
		
		List<Stream> streams = new ArrayList<>();
		
		for (RecentStream item:items) {
			Stream stream = new Stream(
					item.played_at(),
					item.track().duration_ms(),
					item.track().id());
			
			if (!previous.contains(stream)) {
				streams.add(stream);
			}
		}
		return streams;
	}	
	
	public static List<Track> apiTracks(List<ApiTrack> apiTracks) {
		
		List<Track> addedTracks = new ArrayList<>();
		
		for (ApiTrack track:apiTracks) {
			String spotify_track_id = track.id();
			String name = track.name();
			Integer duration_ms = track.duration_ms();
			String album_id = track.album().id();
			Integer disc_number = track.disc_number();
			Integer track_number = track.track_number();
			
			Track newTrack = new Track(
					spotify_track_id,
					name,
					duration_ms,
					album_id,
					disc_number,
					track_number);
			
			addedTracks.add(newTrack);
		}
		
		return addedTracks;
	}
	
	public static List<Album> apiAlbums(List<ApiAlbum> apiAlbums) {
		
		List<Album> addedAlbums = new ArrayList<>();
		
		for (ApiAlbum album:apiAlbums) {
			String id = album.id();
			String name = album.name();
			Integer total_tracks = album.total_tracks();
			String release_date = album.release_date();
			String image_url = album
					.images()
					.get(album.images().size() - 1)
					.url();
			
			Album newAlbum = new Album (
					id,
					name,
					total_tracks,
					release_date,
					image_url);
			
			addedAlbums.add(newAlbum);
		}
		
		return addedAlbums;
	}
	
	public static List<Artist> apiArtists(List<ApiArtist> apiArtists) {
		
		List<Artist> addedArtists = new ArrayList<>();
		
		for (ApiArtist artist:apiArtists) {
			String id = artist.id();
			String name = artist.name();

			Artist newArtist = new Artist (
					id,
					name);
					
			addedArtists.add(newArtist);
		}
		
		return addedArtists;
	}
}
