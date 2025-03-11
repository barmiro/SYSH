package com.github.barmiro.sysh_server.users.register;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.github.barmiro.sysh_server.security.SyshUser;
import com.github.barmiro.sysh_server.security.SyshUserDetails;
import com.github.barmiro.sysh_server.users.SyshUserManager;

@RestController
public class RegisterController {
	
	private SyshUserManager manager;
	
	public RegisterController(SyshUserManager manager) {
		this.manager = manager;
	}

	@PostMapping("/register")
	public RegisterResponse registerUser(@RequestBody SyshUser user) {
		
		if (manager.isUsernameTaken(user.username())) {
			throw new HttpClientErrorException(HttpStatus.CONFLICT);
		} else {
			SyshUserDetails userDetails = new SyshUserDetails(user);
			manager.createUser(userDetails);
			return new RegisterResponse(userDetails.getUsername());
		}
		

	}
			
	
}
