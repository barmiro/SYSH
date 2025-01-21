package com.github.barmiro.sysh_server.common.records;

public record RecordCompInfo(
		String compName,
		Object compValue,
		int sqlType) {		// null values handled by SqlType.get()

}
