package com.github.barmiro.sysh_server.recent.dto.recentstream.recenttrack;

import java.util.List;

import com.github.barmiro.sysh_server.recent.dto.recentstream.recenttrack.recentalbum.RecentAlbum;
import com.github.barmiro.sysh_server.recent.dto.recentstream.recenttrack.recentartist.RecentArtist;

public record RecentTrack(
		Integer duration_ms,
		String name,
		RecentAlbum album,
		String id,
		List<RecentArtist> artists) {

}
