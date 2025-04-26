package com.github.barmiro.demo_data_generator.dto;

public record SpotifyAuthorizationResponseDTO(
		String access_token,
		String token_type,
		String scope,
		Integer expires_in,
		String refresh_token
){
}
