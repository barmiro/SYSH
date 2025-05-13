package com.github.barmiro.sysh_server.users.register;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.github.barmiro.sysh_server.security.SyshUser;
import com.github.barmiro.sysh_server.security.UserRole;
import com.github.barmiro.sysh_server.users.SyshUserManager;

@RestController
public class RegisterController {
	
	private SyshUserManager manager;
	
	public RegisterController(SyshUserManager manager) {
		this.manager = manager;
	}
	
	@Value("${SYSH_RESTRICTED_MODE:false}")
	private boolean isRestrictedMode;

	@PostMapping("/register")
	public RegisterResponse registerUser(@RequestBody SyshUser user) {
		
		
//		not handled in security config
//		because I want this route to be completely disabled in restricted mode
		if (isRestrictedMode) {
			throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
		}
		
		Optional<SyshUser> createdUserOptional;
		
		if (manager.isUsernameTaken(user.username())) {
			throw new HttpClientErrorException(HttpStatus.CONFLICT);
		} else if (manager.adminExists()) { // blocking explicit role assignments in the request
			createdUserOptional = Optional.ofNullable(
					manager.createUser(
							new SyshUser(
									user.username(),
									user.password(),
									UserRole.USER,
									user.timezone(),
									user.spotify_state()
							)
					)
			);
		} else { // if there is no user with role ADMIN, the next created user will be an admin
			createdUserOptional = Optional.ofNullable(
					manager.createUser(
							new SyshUser(
									user.username(),
									user.password(),
									UserRole.ADMIN,
									user.timezone(),
									user.spotify_state()
							)
					)
			);
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
