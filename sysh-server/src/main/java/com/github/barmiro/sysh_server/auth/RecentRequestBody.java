package com.github.barmiro.sysh_server.auth;

public record RecentRequestBody(
	String grant_type,
	String code,
	String redirect_uri
){
}
