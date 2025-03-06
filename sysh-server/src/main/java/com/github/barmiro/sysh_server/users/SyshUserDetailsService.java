package com.github.barmiro.sysh_server.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SyshUserDetailsService implements UserDetailsService {
	
	private static final Logger log = LoggerFactory.getLogger(SyshUserDetailsService.class);
	
	private SyshUserRepository userRepo;
	
	public SyshUserDetailsService(SyshUserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		SyshUser user = userRepo.findByUsername(username);
		
		if (user == null) {
			throw new UsernameNotFoundException("User not found");
		} else {
			return new SyshUserDetails(user);			
		}
	}
	

}
