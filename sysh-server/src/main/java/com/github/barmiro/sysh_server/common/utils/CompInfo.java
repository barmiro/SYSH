package com.github.barmiro.sysh_server.common.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;

import com.github.barmiro.sysh_server.common.records.RecordCompInfo;

public class CompInfo {
	
	public static <T> List<RecordCompInfo> get (T obj
			) throws IllegalAccessException, InvocationTargetException {
		RecordComponent[] components = obj.getClass().getRecordComponents();
		List<RecordCompInfo> recordComps = new ArrayList<>();
		
		for (RecordComponent comp:components) {
			recordComps.add(
					new RecordCompInfo(
							comp.getName(),
							comp.getAccessor().invoke(obj),
							SqlType.get(comp.getType())));
		}
		return recordComps;
	}
}
