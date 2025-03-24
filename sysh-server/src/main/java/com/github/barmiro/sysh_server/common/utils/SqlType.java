package com.github.barmiro.sysh_server.common.utils;

import java.security.Timestamp;
import java.sql.Types;
import java.time.OffsetDateTime;

public class SqlType {

	public static Integer get(Class<?> type) {
		
		if (type == String.class) {
			return Types.VARCHAR;
		} else if (type == Integer.class || type == int.class) {
			return Types.INTEGER;
		} else if (type == Timestamp.class) {
			return Types.TIMESTAMP;
		} else if (type == OffsetDateTime.class) {
			return Types.TIMESTAMP_WITH_TIMEZONE;
		} else {
			return Types.OTHER;
		}
		
	}
}
