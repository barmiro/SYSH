package com.github.barmiro.sysh_server.users;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	
}
