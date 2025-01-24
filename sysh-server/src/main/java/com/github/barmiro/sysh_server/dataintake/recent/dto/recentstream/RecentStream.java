package com.github.barmiro.sysh_server.dataintake.recent.dto.recentstream;

import java.sql.Timestamp;

import com.github.barmiro.sysh_server.dataintake.recent.dto.recentstream.context.Context;
import com.github.barmiro.sysh_server.dataintake.recent.dto.recentstream.recenttrack.RecentTrack;

public record RecentStream(
		RecentTrack track,
		Timestamp played_at,
		Context context
		) {

}
