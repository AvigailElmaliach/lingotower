package com.lingotower.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lingotower.data.AdminRepository;
import com.lingotower.data.UserRepository;
import com.lingotower.model.Admin;
import com.lingotower.model.BaseUser;
import com.lingotower.model.User;

@Service
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AdminRepository adminRepository;

	/**
	 * Loads user details by the given username for authentication. It first tries
	 * to find a User in the userRepository. If not found, it tries to find an Admin
	 * in the adminRepository. If a user is found, it wraps it in a UserPrincipal.
	 * If an admin is found, it wraps the admin in a UserPrincipal (since Admin
	 * extends BaseUser). If neither a user nor an admin is found with the given
	 * username, it throws a UsernameNotFoundException.
	 * 
	 * @param username The username of the user to load.
	 * @return A UserDetails object representing the user or admin.
	 * @throws UsernameNotFoundException If no user or admin is found with the given
	 *                                   username.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

		Admin admin = adminRepository.findByUsername(username).orElse(null);

		if (admin != null) {
			return new UserPrincipal((BaseUser) admin);
		}

		return new UserPrincipal(user);
	}

}