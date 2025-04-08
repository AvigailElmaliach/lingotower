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

	public UserPrincipal(BaseUser user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// אם התפקיד הוא ADMIN, ניתן לו הרשאה של ROLE_ADMIN, אחרת ROLE_USER
		if (user.getRole().equals("ADMIN")) {
			return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
	}

	public String getTargetLanguage() {
		return user.getTargetLanguage();
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; // רק אם נצטרך את הפונקציות האלה והפונקציות שלמטה
	}

	@Override
	public boolean isAccountNonLocked() {
		return true; //
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; //
	}

	@Override
	public boolean isEnabled() {
		return true; //
	}
}
