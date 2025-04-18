package com.lingotower.service;

import com.lingotower.data.BaseUserRepository;
import com.lingotower.model.BaseUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public abstract class BaseUserService<T extends BaseUser> {

	protected final PasswordEncoder passwordEncoder;
	protected final BaseUserRepository<T> baseUserRepository;

	public BaseUserService(PasswordEncoder passwordEncoder, BaseUserRepository<T> baseUserRepository) {
		this.passwordEncoder = passwordEncoder;
		this.baseUserRepository = baseUserRepository;
	}

	protected void updatePasswordInternal(T user, String newPassword) {
		user.setPassword(passwordEncoder.encode(newPassword));
		baseUserRepository.save(user);
	}

	public abstract void updatePassword(String username, String newPassword);

	protected abstract void saveUser(T user);

	public abstract T findByUsername(String username);
}