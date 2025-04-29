package com.lingotower.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.lingotower.model.BaseUser;
import com.lingotower.model.Role;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

	private final BaseUser user;

	/**
	 * Constructs a UserPrincipal object from a BaseUser. This object implements
	 * UserDetails and provides the necessary information for Spring Security to
	 * perform authentication and authorization.
	 * 
	 * @param user The BaseUser object representing the authenticated user.
	 */
	public UserPrincipal(BaseUser user) {
		this.user = user;
	}

	/**
	 * Returns the authorities (roles) granted to the user. If the user's role is
	 * "ADMIN", it grants the "ROLE_ADMIN" authority. Otherwise, it grants the
	 * "ROLE_USER" authority.
	 * 
	 * @return A collection of GrantedAuthority objects representing the user's
	 *         roles.
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (user.getRole().equals("ADMIN")) {
			return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
	}

	/**
	 * Returns the target language of the user. This information is specific to the
	 * application's user model.
	 * 
	 * @return The target language of the user.
	 */
	public String getTargetLanguage() {
		return user.getTargetLanguage();
	}

	/**
	 * Returns the password of the user. This is used by Spring Security for
	 * authentication.
	 * 
	 * @return The password of the user.
	 */
	@Override
	public String getPassword() {
		return user.getPassword();
	}

	/**
	 * Returns the username of the user. This is used by Spring Security as the
	 * principal for authentication.
	 * 
	 * @return The username of the user.
	 */
	@Override
	public String getUsername() {
		return user.getUsername();
	}

}