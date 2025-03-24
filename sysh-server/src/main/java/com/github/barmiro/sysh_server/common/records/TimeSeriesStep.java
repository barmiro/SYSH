package com.github.barmiro.sysh_server.common.records;

import java.time.temporal.ChronoUnit;

public record TimeSeriesStep(
		ChronoUnit unit,
		Integer count
		) {

}
