package com.github.barmiro.sysh_server.common.utils;

import java.util.List;

import com.github.barmiro.sysh_server.common.records.RecordCompInfo;

public class CompListToSql {
	
	public static String insert(List<RecordCompInfo> comps) {
		
		StringBuilder sb = new StringBuilder()
				.append("INSERT INTO Tracks(");
		
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
