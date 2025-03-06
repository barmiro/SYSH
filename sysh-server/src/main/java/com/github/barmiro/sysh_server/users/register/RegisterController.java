package com.github.barmiro.sysh_server.users.register;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.barmiro.sysh_server.users.SyshUser;
import com.github.barmiro.sysh_server.users.SyshUserDetails;
import com.github.barmiro.sysh_server.users.SyshUserDetailsManager;

@RestController
public class RegisterController {
	
	private SyshUserDetailsManager manager;
	
	public RegisterController(SyshUserDetailsManager manager) {
		this.manager = manager;
	}

	@PostMapping("/register")
	public UserDetails registerUser(@RequestBody SyshUser user) {
		
		SyshUserDetails userDetails = new SyshUserDetails(user);
		manager.createUser(userDetails);
		return manager.loadUserByUsername(user.username());
	}
			
	
}
