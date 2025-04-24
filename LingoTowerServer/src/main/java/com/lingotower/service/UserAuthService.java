package com.lingotower.service;

import com.lingotower.data.UserRepository;
import com.lingotower.dto.LoginRequest;
import com.lingotower.dto.RegisterRequest;
import com.lingotower.model.Role;
import com.lingotower.model.User;
import com.lingotower.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/*
 * This class manages user registration and login.
 * It works with a helper class that handles the main logic.
 */
@Service
public class UserAuthService {

	private final AuthHelperService<User> authHelperService;

	/*
	 * Constructor: creates the helper service that will do the real work. It needs
	 * access to the database (UserRepository), password encoder, and JWT token
	 * generator.
	 */
	@Autowired
	public UserAuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			JwtTokenProvider jwtTokenProvider) {
		this.authHelperService = new AuthHelperService<>(userRepository, passwordEncoder, jwtTokenProvider);
	}

	/*
	 * This method registers a new user. It checks the request details and passes
	 * the user and password to the helper service which handles saving and token
	 * generation.
	 */
	public String registerUser(RegisterRequest request) {
		User user = new User(request.getUsername(), null, // password will be set and encoded by the helper
				request.getEmail(), request.getSourceLanguage(), request.getTargetLanguage(), Role.USER);
		return authHelperService.register(user, request.getPassword());
	}

	/*
	 * This method logs in a user. It passes the identifier (username or email) and
	 * password to the helper service, which returns a JWT token if login is
	 * successful.
	 */
	public String login(LoginRequest request) {
		return authHelperService.login(request.getIdentifier(), request.getPassword());
	}
}
