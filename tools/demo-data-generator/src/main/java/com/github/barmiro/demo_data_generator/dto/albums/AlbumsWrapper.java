package com.github.barmiro.demo_data_generator.dto.albums;

import java.util.List;

public record AlbumsWrapper (
		List<ApiAlbum> albums
		) {
}
