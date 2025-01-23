package com.github.barmiro.sysh_server.catalog.tracks;

import java.util.List;

public interface ApiWrapper<T> {
	List<T> unwrap();

}
