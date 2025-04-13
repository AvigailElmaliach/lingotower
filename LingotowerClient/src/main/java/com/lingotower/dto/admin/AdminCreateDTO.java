package com.lingotower.dto.admin;

import com.lingotower.model.Role;

public class AdminCreateDTO {
	private String username;
	private String password;
	private String email;
	private Role role = Role.ADMIN;
	private String sourceLanguage;
	private String targetLanguage;

	// Public constructor (optional), Getters, Setters needed
	public String getUsername() {
		return username;
	}

	public void setUsername(String s) {
		username = s;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String s) {
		password = s;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String s) {
		email = s;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role r) {
		role = r;
	}

	public String getSourceLanguage() {
		return sourceLanguage;
	}

	public void setSourceLanguage(String s) {
		sourceLanguage = s;
	}

	public String getTargetLanguage() {
		return targetLanguage;
	}

	public void setTargetLanguage(String s) {
		targetLanguage = s;
	}
}
