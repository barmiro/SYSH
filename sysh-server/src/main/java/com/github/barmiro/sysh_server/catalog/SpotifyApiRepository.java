package com.github.barmiro.sysh_server.catalog;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.barmiro.sysh_server.auth.TokenService;
import com.github.barmiro.sysh_server.catalog.interfaces.ApiEntity;
import com.github.barmiro.sysh_server.catalog.interfaces.ApiWrapper;
import com.github.barmiro.sysh_server.catalog.interfaces.CatalogEntity;
import com.github.barmiro.sysh_server.catalog.interfaces.CatalogRepository;

@Repository
public abstract class SpotifyApiRepository<
	RepositoryClass extends CatalogRepository,
	EntityClass extends CatalogEntity,
	ApiEntityClass extends ApiEntity,
	WrapperClass extends ApiWrapper<? extends ApiEntity>>
	{

	protected final JdbcClient jdbc;
	protected final RestClient apiClient;
	protected final TokenService tkn;
	protected final RepositoryClass catalogRepository;
	
	protected SpotifyApiRepository(JdbcClient jdbc, 
			RestClient apiClient, 
			TokenService tkn,
			RepositoryClass catalogRepository) {
		this.jdbc = jdbc;
		this.apiClient = apiClient;
		this.tkn = tkn;
		this.catalogRepository = catalogRepository;
	}
	
	
	
	@SuppressWarnings("unchecked")
	protected List<ApiEntityClass> mapResponse(
			ResponseEntity<String> response,
			Class<WrapperClass> wrapper
			) throws JsonMappingException, JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper()
				.configure(DeserializationFeature
						.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
//		Keep in mind: unchecked casting
		return (List<ApiEntityClass>) mapper.readValue(response.getBody(), wrapper).unwrap();
	}
	
	
	protected List<String> getNewIDs(List<String> entityIDs,
			String idName, 
			Class<EntityClass> entCls) {
		
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
	
	
	protected String stringify(List<String> newIDs, Class<EntityClass> entCls) {
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
			Class<EntityClass> entCls,
			int limit
			) throws Exception {
		
		int listSize = IDlist.size();
		
		List<String> idPackets = new ArrayList<>();
		
		for(int i = 0; i < listSize; i += limit) {
			if (i + limit >= listSize) {
				limit = listSize - i;
			}
			String idPacket = stringify(IDlist.subList(i, i + limit), entCls);
			idPackets.add(idPacket);
		}
		
		return idPackets;
	}
	
	
	protected ResponseEntity<String> getResponse(String packet) {
		ResponseEntity<String> response = apiClient
				.get()
				.uri(packet)
				.header("Authorization", "Bearer " + tkn.getToken())
				.retrieve()
				.toEntity(String.class);
		
		
		return Optional.ofNullable(response).orElseThrow();
	}
}

