package com.github.barmiro.sysh_server.catalog.artists.spotifyapi;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.barmiro.sysh_server.auth.TokenService;
import com.github.barmiro.sysh_server.catalog.artists.Artist;
import com.github.barmiro.sysh_server.catalog.artists.ArtistRepository;
import com.github.barmiro.sysh_server.catalog.artists.spotifyapi.dto.ArtistsWrapper;
import com.github.barmiro.sysh_server.catalog.artists.spotifyapi.dto.artists.ApiArtist;
import com.github.barmiro.sysh_server.catalog.interfaces.SpotifyApiRepository;
import com.github.barmiro.sysh_server.common.utils.ConvertDTOs;

@Repository
public class ArtistApiRepository extends SpotifyApiRepository<
											ArtistRepository,
											Artist,
											ApiArtist,
											ArtistsWrapper> {

	ArtistApiRepository(JdbcClient jdbc, RestClient apiClient, TokenService tkn, ArtistRepository catalogRepository) {
		super(jdbc, apiClient, tkn, catalogRepository);
	}


	
	public List<Artist> addNewArtists(List<String> artist_ids) {
		
		List<String> newIDs = getNewIDs(artist_ids, "id");
		System.out.println("Found " + newIDs.size() + " new artists.");
		
		List<String> packets = new ArrayList<>();

		try {
			packets = prepIdPackets(newIDs, 50);			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Method prepIdPackets threw an exception.");
			return new ArrayList<Artist>();
		}
		
		List<ApiArtist> apiArtists = new ArrayList<>();
		for (String packet:packets) {
			ResponseEntity<String> response = getResponse(packet);

			try {
				apiArtists.addAll(mapResponse(response));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				System.out.println("Method mapApiArtists threw an exception");
			}
		}
		
		if (apiArtists.size() == 0) {
			System.out.println("No artists to add.");
			return new ArrayList<Artist>();
		}
		
		List<Artist> artists = ConvertDTOs.apiArtists(apiArtists);
		
		catalogRepository.addArtists(artists);
		
		return artists;
	}
}
