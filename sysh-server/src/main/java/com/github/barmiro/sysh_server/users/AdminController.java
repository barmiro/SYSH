package com.github.barmiro.sysh_server.users;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.github.barmiro.sysh_server.security.SyshUser;
import com.github.barmiro.sysh_server.users.register.RegisterResponse;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	SyshUserManager userManager;
	
	AdminController(SyshUserManager userManager) {
		this.userManager = userManager;
	}
	

	@GetMapping("/users")
	List<AppUserData> users() {
		return userManager.getAllUserData();
	}
	
	@PostMapping("/users/delete")
	Integer deleteUser(@RequestBody UsernameWrapper user) {
		return userManager.deleteUser(user.username());
	}
	
	@PostMapping("/users/create")
	public RegisterResponse createUser(@RequestBody SyshUser user) {
		
		Optional<SyshUser> createdUserOptional;
		
		if (userManager.isUsernameTaken(user.username())) {
			throw new HttpClientErrorException(HttpStatus.CONFLICT);
		} else {
			createdUserOptional = Optional.ofNullable(
					userManager.createUser(user));
		}
		
		RegisterResponse response = createdUserOptional.map(
				createdUser -> new RegisterResponse(
						createdUser.username(),
						createdUser.role()
				)
		).orElseThrow(); // allows for handling of NoSuchElementException

		return response;
	}
	
}
