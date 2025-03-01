package com.github.barmiro.sysh_server.dataintake.recent;

public record RecentRequestBody(
	String grant_type,
	String code,
	String redirect_uri
){
}
