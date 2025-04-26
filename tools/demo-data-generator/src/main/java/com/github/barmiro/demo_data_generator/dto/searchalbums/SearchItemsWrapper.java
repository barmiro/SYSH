package com.github.barmiro.demo_data_generator.dto.searchalbums;

import java.util.List;

public record SearchItemsWrapper(
		List<SearchApiAlbum> items
		) {

}
