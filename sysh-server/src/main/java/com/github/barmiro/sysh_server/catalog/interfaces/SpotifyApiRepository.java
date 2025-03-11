package com.github.barmiro.sysh_server.catalog.interfaces;

import java.lang.reflect.ParameterizedType;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.barmiro.sysh_server.spotifyauthorization.SpotifyTokenService;

@Repository
public abstract class SpotifyApiRepository<
	RepositoryClass extends CatalogRepository<EntityClass>,
	EntityClass extends CatalogEntity,
	ApiEntityClass extends ApiEntity,
	WrapperClass extends ApiWrapper<? extends ApiEntity>>
	{

	protected final JdbcClient jdbc;
	protected final RestClient apiClient;
	protected final SpotifyTokenService tkn;
	protected final RepositoryClass catalogRepository;
	
	protected SpotifyApiRepository(JdbcClient jdbc, 
			RestClient apiClient, 
			SpotifyTokenService tkn,
			RepositoryClass catalogRepository) {
		this.jdbc = jdbc;
		this.apiClient = apiClient;
		this.tkn = tkn;
		this.catalogRepository = catalogRepository;
	}
	
	@SuppressWarnings("unchecked")
	Class<EntityClass> getEntityClass() throws ClassCastException {
		ParameterizedType superClass = (ParameterizedType) getClass()
				.getGenericSuperclass();
		return (Class<EntityClass>) superClass.getActualTypeArguments()[1];
	}
	
	@SuppressWarnings("unchecked")
	Class<WrapperClass> getWrapperClass() throws ClassCastException {
		ParameterizedType superClass = (ParameterizedType) getClass()
										.getGenericSuperclass();
		return (Class<WrapperClass>) superClass.getActualTypeArguments()[3];
	}
	
	
	@SuppressWarnings("unchecked")
	protected List<ApiEntityClass> mapResponse(
			ResponseEntity<String> response
			) throws JsonProcessingException {
		
		Class<WrapperClass> wrapper = getWrapperClass();
		
		ObjectMapper mapper = new ObjectMapper()
				.configure(DeserializationFeature
						.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		return (List<ApiEntityClass>) mapper.readValue(response.getBody(), wrapper).unwrap();
	}
	
	
	protected List<String> getNewIDs(List<String> entityIDs,
			String idName) {
		
		Class<EntityClass> entCls = getEntityClass();
		List<String> newIDs = new ArrayList<>();
		
		for(String entityID:entityIDs) {
			Optional.ofNullable(entityID)
						.filter(id -> !id.isEmpty())
						.orElseThrow();
			
			int exists = jdbc.sql("SELECT * FROM "
					+ entCls.getSimpleName() + "s "
					+ "WHERE " + idName + " = :entityID "
					+ "LIMIT 1")
					.param("entityID", entityID, Types.VARCHAR)
					.query(entCls)
					.list()
					.size();
			
			if (exists == 0 && !newIDs.contains(entityID)) {
				newIDs.add(entityID);
			}
		}
		return newIDs;
	}
	
	
	protected String stringify(List<String> newIDs) {
		Class<EntityClass> entCls = getEntityClass();
		
		StringBuilder sb = new StringBuilder();
		sb.append(entCls.getSimpleName().toLowerCase() + "s?ids=");
		
		for (String id:newIDs) {
			sb.append(id + ",");
		}
		
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	
	protected List<String> prepIdPackets(
			List<String> IDlist,
			int limit
			) {

		int listSize = IDlist.size();
		
		List<String> idPackets = new ArrayList<>();
		
		for(int i = 0; i < listSize; i += limit) {
			if (i + limit >= listSize) {
				limit = listSize - i;
			}
			String idPacket = stringify(IDlist.subList(i, i + limit));
			idPackets.add(idPacket);
		}
		
		return idPackets;
	}
	
	
    @Retryable(
            value = { HttpServerErrorException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2)
        )
	protected ResponseEntity<String> getResponse(String packet, String username) {
		
		
		ResponseEntity<String> response = apiClient
				.get()
				.uri(packet)
				.header("Authorization", "Bearer " + tkn.getToken(username))
				.retrieve()
				.toEntity(String.class);
		
		return Optional.ofNullable(response).orElseThrow();
	}
}

