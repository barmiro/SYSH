package com.github.barmiro.sysh_server.stats.dto;

import com.github.barmiro.sysh_server.catalog.albums.AlbumStats;
import com.github.barmiro.sysh_server.catalog.artists.ArtistStats;
import com.github.barmiro.sysh_server.catalog.tracks.TrackStats;

public record HomeStats(
			Integer minutes_streamed,
			Integer stream_count,
			ArtistStats top_artist,
			AlbumStats top_album,
			TrackStats top_track) {

}
