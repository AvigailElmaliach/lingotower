package com.lingotower.dto.admin;

import com.lingotower.model.Role;

public class AdminUpdateDTO {
	private String username;
	private String password; // Optional
	private Role role;

	// Public constructor (optional), Getters, Setters needed
	public String getUsername() {
		return username;
	}

	public void setUsername(String u) {
		username = u;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String p) {
		password = p;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role r) {
		role = r;
	}
}
