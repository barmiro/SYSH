package com.github.barmiro.sysh_server.recent.dto.recentstream;

import com.github.barmiro.sysh_server.recent.dto.recentstream.context.Context;
import com.github.barmiro.sysh_server.recent.dto.recentstream.recenttrack.RecentTrack;

public record RecentStream(
		RecentTrack track,
		String played_at,
		Context context,
		String uri
		) {

}
