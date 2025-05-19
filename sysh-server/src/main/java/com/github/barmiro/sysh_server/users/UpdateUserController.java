package com.github.barmiro.sysh_server.users;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.barmiro.sysh_server.security.SyshUser;
import com.github.barmiro.sysh_server.users.register.PasswordChangeRequest;
import com.github.barmiro.sysh_server.users.register.RegisterResponse;

@RestController
public class UpdateUserController {

	private SyshUserManager manager;
	
	public UpdateUserController(SyshUserManager manager) {
		this.manager = manager;
	}
	
	@PostMapping("/updateTimezone")
	void updateTimezone(@RequestBody SyshUser user) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		manager.updateUserTimezone(username, user.timezone());

		return;
		
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
