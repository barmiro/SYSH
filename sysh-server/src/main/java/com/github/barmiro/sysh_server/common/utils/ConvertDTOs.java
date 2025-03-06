package com.github.barmiro.sysh_server.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.barmiro.sysh_server.catalog.albums.Album;
import com.github.barmiro.sysh_server.catalog.albums.spotifyapideprecated.dto.albums.ApiAlbum;
import com.github.barmiro.sysh_server.catalog.artists.Artist;
import com.github.barmiro.sysh_server.catalog.artists.spotifyapi.dto.artists.ApiArtist;
import com.github.barmiro.sysh_server.catalog.streams.SongStream;
import com.github.barmiro.sysh_server.catalog.tracks.Track;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.ApiTrack;
import com.github.barmiro.sysh_server.catalog.tracks.spotify_api.dto.tracks.album.ApiTrackAlbum;
import com.github.barmiro.sysh_server.dataintake.json.StreamDTO;
import com.github.barmiro.sysh_server.dataintake.recent.dto.ItemsWrapper;
import com.github.barmiro.sysh_server.dataintake.recent.dto.recentstream.RecentStream;
import com.github.barmiro.sysh_server.users.SpotifyUserData;

public class ConvertDTOs {
	
	static ObjectMapper objectMapper = new ObjectMapper()
		.configure(DeserializationFeature
				.FAIL_ON_UNKNOWN_PROPERTIES, false);
			
	public static List<SongStream> streamsJson(List<StreamDTO> streamDTOs) {
		List<SongStream> streams = new ArrayList<>();
		
		for (StreamDTO streamDTO:streamDTOs) {
			if (streamDTO.spotify_track_uri() != null && streamDTO.ms_played() > 0) {
				SongStream stream = new SongStream(streamDTO.ts(),
						streamDTO.ms_played(),
						streamDTO.spotify_track_uri().replace("spotify:track:", ""));
				
				streams.add(stream);
			}
		}
		return streams;
	}
	
	public static SpotifyUserData userData(ResponseEntity<String> response) {
		
		SpotifyUserData data;
		try {
			data = objectMapper
					.readValue(response.getBody(), SpotifyUserData.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new SpotifyUserData("to SYSH");
		}
		
		return data;
	}
	
	public static List<SongStream> streamsRecent(
			ResponseEntity<String> response,
			List<SongStream> previous) {

		List<RecentStream> items;
		
		try {
			items = objectMapper
					.readValue(response.getBody(), ItemsWrapper.class)
					.items();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new ArrayList<SongStream>();
		}
		
		List<SongStream> streams = new ArrayList<>();
		
		for (RecentStream item:items) {
			SongStream stream = new SongStream(
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
	
	
	@Deprecated
	public static List<Album> apiAlbums(List<ApiAlbum> apiAlbums) {
		
		List<Album> addedAlbums = new ArrayList<>();
		
		for (ApiAlbum album:apiAlbums) {
			String id = album.id();
			String name = album.name();
			Integer total_tracks = album.total_tracks();
			String release_date = album.release_date();
			String image_url = album
					.images()
					.get(0)
					.url();
			String thumbnail_url = album
					.images()
					.get(album.images().size() - 1)
					.url();
			
			Album newAlbum = new Album (
					id,
					name,
					total_tracks,
					release_date,
					image_url,
					thumbnail_url);
			
			addedAlbums.add(newAlbum);
		}
		return addedAlbums;
	}
	
	
	
public static List<Album> apiTrackAlbums(List<ApiTrackAlbum> apiTrackAlbums) {
		
		List<Album> addedAlbums = new ArrayList<>();
		
		for (ApiTrackAlbum album:apiTrackAlbums) {
			String id = album.id();
			String name = album.name();
			Integer total_tracks = album.total_tracks();
			String release_date = album.release_date();
			
//			TODO: handle albums with no images
			int imageListSize = album.images().size();
			String image_url = "";
			String thumbnail_url = "";
			if (imageListSize > 0) {
				image_url = album
						.images()
						.get(0)
						.url();
				thumbnail_url = album
						.images()
						.get(imageListSize - 1)
						.url();				
			}
			Album newAlbum = new Album (
					id,
					name,
					total_tracks,
					release_date,
					image_url,
					thumbnail_url);
			
			addedAlbums.add(newAlbum);
		}
		
		return addedAlbums;
	}
	
	
	public static List<Artist> apiArtists(List<ApiArtist> apiArtists) {
		
		List<Artist> addedArtists = new ArrayList<>();
		
		
		for (ApiArtist artist:apiArtists) {
			String id = artist.id();
			String name = artist.name();
			
			int imageListSize = artist.images().size();
			String image_url = "";
			String thumbnail_url = "";
			if (imageListSize > 0) {
				image_url = artist
						.images()
						.get(0)
						.url();
				thumbnail_url = artist
						.images()
						.get(imageListSize - 1)
						.url();	
			}

			Artist newArtist = new Artist (
					id,
					name,
					image_url,
					thumbnail_url);
					
			addedArtists.add(newArtist);
		}
		
		return addedArtists;
	}
}
