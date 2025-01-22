package com.github.barmiro.sysh_server.catalog;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.barmiro.sysh_server.auth.TokenService;

@Service
public abstract class SpotifyApiRepository<
	RepositoryClass extends CatalogRepository,
	EntityClass extends CatalogEntity> {

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
	
	protected ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature
					.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	
	protected List<String> getNewIDs(List<String> entityIDs,
			String idName, 
			Class<EntityClass> entCls) {
		
		List<String> newIDs = new ArrayList<>();
		
		for(String entityID:entityIDs) {
			if (entityID == "") {
				continue;
			}
			
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
		
		if (listSize == 0) {
			return new ArrayList<>();
		}
		
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
		
		return response;
	}
}

