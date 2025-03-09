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
//		this delete is TEMPORARY
		StringBuilder sb = new StringBuilder()
				.append("DELETE FROM Stats_Cache_Full WHERE username = :username;"
						+ "INSERT INTO Stats_Cache_Full (");
		
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
		sb.append(") ON CONFLICT (username, start_date, end_date) DO UPDATE SET ");
		
		for (RecordCompInfo comp:comps) {
			sb.append(comp.compName() + " = EXCLUDED." + comp.compName() + ",");
		}
		
		sb.deleteCharAt(sb.length() - 1);
		
		return sb.toString();
	}
	
	public static <T> String insertTopItemsCache(
			List<RecordCompInfo> comps,
			String itemName) {
		
		StringBuilder sb = new StringBuilder()
				.append("INSERT INTO Top_" + itemName + "s_Cache(");
		
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
}
