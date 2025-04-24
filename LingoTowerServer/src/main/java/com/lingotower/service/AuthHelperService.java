package com.lingotower.service;

import com.lingotower.model.BaseUser;
import com.lingotower.data.BaseUserRepository;
import com.lingotower.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.nulabinc.zxcvbn.Zxcvbn;
import com.nulabinc.zxcvbn.Strength;

/*
 * This helper class handles login and registration logic
 * for any user type that extends BaseUser (like User or Admin).
 */
public class AuthHelperService<T extends BaseUser> {

	private final BaseUserRepository<T> baseUserRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

	/*
	 * Constructor: needs the repository to find/save users, a password encoder to
	 * hash passwords, and a token provider to generate JWTs.
	 */
	public AuthHelperService(BaseUserRepository<T> baseUserRepository, PasswordEncoder passwordEncoder,
			JwtTokenProvider jwtTokenProvider) {
		this.baseUserRepository = baseUserRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	/*
	 * This method logs in a user using either username or email. It checks if the
	 * password matches and returns a JWT token if successful.
	 */
	public String login(String identifier, String password) {
		T user = baseUserRepository.findByUsername(identifier).or(() -> baseUserRepository.findByEmail(identifier))
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new IllegalArgumentException("Invalid credentials");
		}

		return jwtTokenProvider.generateToken(user);
	}

	/*
	 * This method registers a new user. It checks if the email or username already
	 * exist, if the password is strong enough, and then saves the user with an
	 * encoded password.
	 */
	public String register(T newUser, String plainPassword) {
		if (baseUserRepository.existsByEmail(newUser.getEmail())) {
			throw new IllegalArgumentException("Email already exists");
		}

		if (baseUserRepository.findByUsername(newUser.getUsername()).isPresent()) {
			throw new IllegalArgumentException("Username is already taken");
		}

		if (!isPasswordStrong(plainPassword)) {
			throw new IllegalArgumentException("Password is too weak. Please choose a stronger password.");
		}

		newUser.setPassword(passwordEncoder.encode(plainPassword));
		baseUserRepository.save(newUser);
		return jwtTokenProvider.generateToken(newUser);
	}

	/*
	 * Checks if the password is strong using the zxcvbn library. It returns true if
	 * the strength score is 3 or more (scale is 0–4).
	 */
	public static boolean isPasswordStrong(String password) {
		Zxcvbn zxcvbn = new Zxcvbn();
		Strength strength = zxcvbn.measure(password);
		return strength.getScore() >= 3;
	}
}
