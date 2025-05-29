package com.github.barmiro.sysh_server.dataintake.json;

import java.time.ZonedDateTime;

public record ZipUploadItem(
		String uploadID,
		String zipName,
		FileProcessingStatus status,
		ZonedDateTime completedOn) {

}
