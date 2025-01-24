package com.github.barmiro.sysh_server.catalog.interfaces;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import org.springframework.stereotype.Repository;

import com.github.barmiro.sysh_server.common.records.RecordCompInfo;
import com.github.barmiro.sysh_server.common.utils.CompInfo;
import com.github.barmiro.sysh_server.common.utils.CompListToSql;

@Repository
public abstract class CatalogRepository<EntityClass extends CatalogEntity> {

	protected final JdbcClient jdbc;
	protected CatalogRepository(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}
	
	@SuppressWarnings("unchecked")
	Class<EntityClass> getEntityClass() throws ClassCastException {
		ParameterizedType superClass = (ParameterizedType) getClass()
										.getGenericSuperclass();
		return (Class<EntityClass>) superClass.getActualTypeArguments()[0];
	}
	
	
	public List<EntityClass> findAll() {
		Class<EntityClass> clazz = getEntityClass();
		return jdbc.sql("SELECT * FROM "
				+ clazz.getSimpleName() + 's')
				.query(clazz)
				.list();
	}
	
	public int addNew(EntityClass entity, Class<EntityClass> entCls
			) throws IllegalAccessException, InvocationTargetException {
		

		List<RecordCompInfo> recordComps = CompInfo.get(entity);
		
		String sql = CompListToSql.insert(recordComps, entCls);
		StatementSpec jdbcCall = jdbc.sql(sql);
		
		for (RecordCompInfo comp:recordComps) {
			jdbcCall = jdbcCall.param(
					comp.compName(),
					comp.compValue(),
					comp.sqlType());
		}
		
		int added = 0;
		
		try {
			added = jdbcCall.update();		
			
		} catch (DuplicateKeyException e){
			System.out.println(
					entity.getName() 
					+ " : "
					+ entity.getId() 
					+ " already exists.");
			return 0;
			
		} catch(DataIntegrityViolationException e) {
			System.out.println(
					entity.getName() 
					+ " : "
					+ entity.getId() 
					+ "contains invalid values.");
			return 0;
		}
		
		
		return added;
	}
}
