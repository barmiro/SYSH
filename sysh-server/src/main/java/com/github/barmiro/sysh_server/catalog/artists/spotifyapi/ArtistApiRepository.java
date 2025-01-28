package com.github.barmiro.sysh_server.catalog.artists.spotifyapi;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	public ArtistApiRepository(JdbcClient jdbc, RestClient apiClient, TokenService tkn, ArtistRepository catalogRepository) {
		super(jdbc, apiClient, tkn, catalogRepository);
	}

	private static final Logger log = LoggerFactory.getLogger(ArtistApiRepository.class);
	
	public List<Artist> addNewArtists(List<String> artist_ids) {
		
		List<String> newIDs = getNewIDs(artist_ids, "id");
		log.info("Found " + newIDs.size() + " new artists.");
		
		List<String> packets = new ArrayList<>();

		try {
			packets = prepIdPackets(newIDs, 50);			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Method prepIdPackets threw an exception.");
			return new ArrayList<Artist>();
		}
		
		List<ApiArtist> apiArtists = new ArrayList<>();
		for (String packet:packets) {
			ResponseEntity<String> response = getResponse(packet);

			try {
				apiArtists.addAll(mapResponse(response));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				log.error("Method mapApiArtists threw an exception");
			}
		}
		
		if (apiArtists.size() == 0) {
			log.info("No artists to add.");
			return new ArrayList<Artist>();
		}
		
		List<Artist> artists = ConvertDTOs.apiArtists(apiArtists);
		
		catalogRepository.addArtists(artists);
		
		return artists;
	}
}
