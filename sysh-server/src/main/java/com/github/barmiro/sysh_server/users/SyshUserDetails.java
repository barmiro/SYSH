package com.github.barmiro.sysh_server.users;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SyshUserDetails implements UserDetails {

	private SyshUser user;
	
	public SyshUserDetails(SyshUser user) {
		this.user = user;
	}
	
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
//		TODO: change this
		String role = "USER";
		return Collections.singleton(new SimpleGrantedAuthority(role));
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
