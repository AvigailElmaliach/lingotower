package com.lingotower.service;

import com.lingotower.data.BaseUserRepository;
import com.lingotower.model.BaseUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/*
 * This abstract service class provides common functionality for handling
 * BaseUser-related operations, such as updating the password and saving the user.
 * It is meant to be extended by specific user service implementations.
 */
public abstract class BaseUserService<T extends BaseUser> {

	protected final PasswordEncoder passwordEncoder;
	protected final BaseUserRepository<T> baseUserRepository;

	/*
	 * Constructor: receives the password encoder and user repository as
	 * dependencies.
	 */
	public BaseUserService(PasswordEncoder passwordEncoder, BaseUserRepository<T> baseUserRepository) {
		this.passwordEncoder = passwordEncoder;
		this.baseUserRepository = baseUserRepository;
	}

	/*
	 * This method updates the user's password by encoding the new password and
	 * saving the updated user.
	 */
	protected void updatePasswordInternal(T user, String newPassword) {
		user.setPassword(passwordEncoder.encode(newPassword)); // Encode the password
		baseUserRepository.save(user); // Save the updated user
	}

	/*
	 * Abstract method to update the password for a specific user based on username.
	 * Subclasses must implement this method.
	 */
	public abstract void updatePassword(String username, String newPassword);

	/*
	 * Abstract method to save the user. Specific implementations must define how
	 * the user is saved.
	 */
	protected abstract void saveUser(T user);

	/*
	 * Abstract method to find a user by username. Specific implementations must
	 * define how the user is retrieved.
	 */
	public abstract T findByUsername(String username);
}
