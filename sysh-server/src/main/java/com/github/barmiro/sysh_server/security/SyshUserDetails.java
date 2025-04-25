package com.github.barmiro.sysh_server.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SyshUserDetails implements UserDetails {

	private static final long serialVersionUID = -898339266300426059L;
	private SyshUser user;
	
	public SyshUserDetails(SyshUser user) {
		this.user = user;
	}
	
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		String roleString = "ROLE_" + user.role();
		return Collections.singleton(new SimpleGrantedAuthority(roleString));
	}

	@Override
	public String getPassword() {
		return user.password();
	}

	@Override
	public String getUsername() {
		return user.username();
	}

}
