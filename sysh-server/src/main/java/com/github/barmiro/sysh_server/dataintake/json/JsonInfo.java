package com.github.barmiro.sysh_server.dataintake.json;

public record JsonInfo(
		String filename,
		FileProcessingStatus status,
		Integer entriesAdded) {
}
