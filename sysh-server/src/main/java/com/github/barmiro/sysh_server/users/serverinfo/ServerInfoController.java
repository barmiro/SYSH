package com.github.barmiro.sysh_server.users.serverinfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.barmiro.sysh_server.users.SyshUserRepository;

@RestController
public class ServerInfoController {
	
	SyshUserRepository userRepo;
	
	public ServerInfoController(SyshUserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
	@Value("${SYSH_RESTRICTED_MODE:false}")
	private boolean isRestrictedMode;

	@GetMapping("/info")
	public ServerInfo info() {
		
		Boolean usersExist = userRepo.userCount() > 0;
		
		return new ServerInfo(
				usersExist,
				isRestrictedMode
				);
	}
}
