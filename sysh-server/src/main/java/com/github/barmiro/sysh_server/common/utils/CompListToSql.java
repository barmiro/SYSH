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
	
	public static <T> String updateFullCache(List<RecordCompInfo> comps) {
		
		StringBuilder sb = new StringBuilder()
				.append("UPDATE Stats_Cache_Full SET ");
		
		for (RecordCompInfo comp:comps) {
			sb.append(comp.compName() + " = :" + comp.compName() + ",");
		}
		
		sb.deleteCharAt(sb.length() - 1);
		
		sb.append(" WHERE id = 1;");
		
		return sb.toString();
	}
	
	public static <T> String insertRangeCache(
			List<RecordCompInfo> comps) {
		
		StringBuilder sb = new StringBuilder()
				.append("INSERT INTO Stats_Cache_Range(");
		
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
