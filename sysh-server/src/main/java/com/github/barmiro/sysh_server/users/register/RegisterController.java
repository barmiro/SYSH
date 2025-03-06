package com.github.barmiro.sysh_server.users.register;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.barmiro.sysh_server.users.SyshUser;
import com.github.barmiro.sysh_server.users.SyshUserDetails;
import com.github.barmiro.sysh_server.users.SyshUserManager;

@RestController
public class RegisterController {
	
	private SyshUserManager manager;
	
	public RegisterController(SyshUserManager manager) {
		this.manager = manager;
	}

	@PostMapping("/register")
	public String registerUser(@RequestBody SyshUser user) {
		
		SyshUserDetails userDetails = new SyshUserDetails(user);
		manager.createUser(userDetails);
		return user.username();
	}
			
	
}
