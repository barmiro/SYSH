package com.github.barmiro.sysh_server.common.utils;

import java.util.List;

import com.github.barmiro.sysh_server.common.records.RecordCompInfo;

public class CompListToSql {
	
	public static <T> String insert(
			List<RecordCompInfo> comps,
			Class<T> clazz) {
		
		StringBuilder sb = new StringBuilder()
				.append("INSERT INTO " + clazz.getSimpleName() + "s(");
		
		for (RecordCompInfo comp:comps) {
			sb.append(comp.compName() + ",");
		}
		
		sb.deleteCharAt(sb.length() - 1);
		
		sb.append(") VALUES (");
		
		for (RecordCompInfo comp:comps) {
			sb.append(":" + comp.compName() + ",");
		}
		
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");
		return sb.toString();
	}
	
	public static <T> String insertCache(
			List<RecordCompInfo> comps) {
		
		StringBuilder sb = new StringBuilder()
				.append("INSERT INTO Stats_Cache(");
		
		for (RecordCompInfo comp:comps) {
			sb.append(comp.compName() + ",");
		}
		
		sb.deleteCharAt(sb.length() - 1);
		
		sb.append(") VALUES (");
		
		for (RecordCompInfo comp:comps) {
			sb.append(":" + comp.compName() + ",");
		}
		
		sb.deleteCharAt(sb.length() - 1);
		sb.append(") ON CONFLICT (start_date, end_date) DO UPDATE SET ");
		
		for (RecordCompInfo comp:comps) {
			sb.append(comp.compName() + " = EXCLUDED." + comp.compName() + ",");
		}
		
		sb.deleteCharAt(sb.length() - 1);
		
		return sb.toString();
	}
}
