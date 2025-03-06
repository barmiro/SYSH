package com.github.barmiro.sysh_server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	KeyPairManager keyPairManager;
	
	public SecurityConfig(KeyPairManager keyPairManager) {
		this.keyPairManager = keyPairManager;
	}
	
	@Bean
	public InMemoryUserDetailsManager user() {
		return new InMemoryUserDetailsManager(
				User.withUsername("admin")
					.password("{noop}password")
					.authorities("read")
					.build()
		);
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.httpBasic(Customizer.withDefaults())
				.build();
	}
	
	@Bean
	JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder
				.withPublicKey(keyPairManager.getPublicKey())
				.build();
	}
	
	@Bean
	JwtEncoder jwtEncoder() {
		JWK jwk = new RSAKey
				.Builder(keyPairManager.getPublicKey())
				.privateKey(keyPairManager.getPrivateKey())
				.build();
		
		JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		
		return new NimbusJwtEncoder(jwks);
	}

}
