package com.github.barmiro.sysh_server.users.register;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
		
		
		if (isRestrictedMode && manager.adminExists()) {
			throw new HttpClientErrorException(HttpStatus.METHOD_NOT_ALLOWED);
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
									user.spotify_state(),
									false
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
									user.spotify_state(),
									false
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
	
	@PostMapping("/changePassword")
	RegisterResponse changePassword(@RequestBody PasswordChangeRequest request) { //TODO: change to a different type
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		SyshUser user = manager.getUserByUsername(username);
		
		PasswordEncoder encoder = new BCryptPasswordEncoder(10);
		int passwordChanged = 0;
		if (encoder.matches(request.oldPassword(), user.password())) {
			passwordChanged = manager.changePassword(username, request.newPassword(), false);
		}
		
		if (passwordChanged == 1) {
			return new RegisterResponse(user.username(), user.role());
		} else {
			throw new RuntimeException();
		}
	}
	
}
