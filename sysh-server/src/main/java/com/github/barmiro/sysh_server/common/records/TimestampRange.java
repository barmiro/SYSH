package com.github.barmiro.sysh_server.common.records;

import java.sql.Timestamp;

public record TimestampRange(
		Timestamp startTimestamp,
		Timestamp endTimestamp) {
}
