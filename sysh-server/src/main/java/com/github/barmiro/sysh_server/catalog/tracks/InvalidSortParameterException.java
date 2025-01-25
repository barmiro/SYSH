package com.github.barmiro.sysh_server.catalog.tracks;

public class InvalidSortParameterException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public InvalidSortParameterException() {
		super("The endpoint was called with an invalid sort parameter.");
	}

}
