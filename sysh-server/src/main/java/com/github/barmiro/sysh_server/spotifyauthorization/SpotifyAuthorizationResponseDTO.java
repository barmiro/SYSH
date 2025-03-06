package com.github.barmiro.sysh_server.spotifyauthorization;

public record SpotifyAuthorizationResponseDTO(
		String access_token,
		String token_type,
		String scope,
		Integer expires_in,
		String refresh_token
){
}
