package com.github.barmiro.sysh_server.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class SyshUserDetailsManager implements UserDetailsManager {
	
	private static final Logger log = LoggerFactory.getLogger(SyshUserDetailsManager.class);
	
	private SyshUserRepository userRepo;
	
	public SyshUserDetailsManager(SyshUserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		SyshUser user = userRepo.findByUsername(username);
		
		if (user == null) {
			throw new RuntimeException("User not found");
		} else {
			return new SyshUserDetails(user);			
		}
	}

	@Override
	public void createUser(UserDetails user) {
		int created = userRepo.createUser(user);
		if (created == 0) {
			log.error("User creation for " + user.getUsername() + " failed");
		} else if (created == 1) {
			log.info("User " + user.getUsername() + " created succcessfully");
		}
	}

	@Override
	public void updateUser(UserDetails user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteUser(String username) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean userExists(String username) {
		// TODO Auto-generated method stub
		return false;
	}

}
