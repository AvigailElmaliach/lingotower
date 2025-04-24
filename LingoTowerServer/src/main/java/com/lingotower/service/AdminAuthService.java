package com.lingotower.service;

import com.lingotower.data.AdminRepository;
import com.lingotower.dto.admin.AdminCreateDTO;
import com.lingotower.model.Admin;
import com.lingotower.model.Role;
import com.lingotower.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/*
 * This service handles authentication logic for Admin users.
 * It uses a helper class to perform login and (possibly) registration.
 */
@Service
public class AdminAuthService {

	private final AuthHelperService<Admin> authHelperService;

	/*
	 * Constructor: receives the admin repository, password encoder, and JWT
	 * provider. Creates an AuthHelperService to manage the authentication.
	 */
	@Autowired
	public AdminAuthService(AdminRepository adminRepository, PasswordEncoder passwordEncoder,
			JwtTokenProvider jwtTokenProvider) {
		this.authHelperService = new AuthHelperService<>(adminRepository, passwordEncoder, jwtTokenProvider);
	}

	/*
	 * Logs in an admin using either username or email, and returns a JWT token.
	 */
	public String loginAdmin(String identifier, String password) {
		return authHelperService.login(identifier, password);
	}
}
