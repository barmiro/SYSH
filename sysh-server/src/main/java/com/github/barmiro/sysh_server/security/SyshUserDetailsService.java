package com.github.barmiro.sysh_server.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.github.barmiro.sysh_server.users.SyshUserRepository;

@Service
public class SyshUserDetailsService implements UserDetailsService {
	
	
	private SyshUserRepository userRepo;
	
	public SyshUserDetailsService(SyshUserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	
		SyshUser user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
		
		if (user.must_change_password()) {
			user = new SyshUser(
					user.username(),
					user.password(),
					UserRole.RESTRICTED,
					user.timezone(),
					user.spotify_state(),
					user.must_change_password()
					);
		}
		
		return new SyshUserDetails(user);			
	}
	

}
