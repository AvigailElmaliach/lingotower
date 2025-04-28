package com.lingotower.dto;

import jakarta.validation.constraints.NotEmpty;

public class LoginRequest {
	@NotEmpty(message = "Username or email is required")
	private String identifier;
	private String password;

	public LoginRequest() {
	}

	public LoginRequest(String identifier, String password) {
		this.identifier = identifier;
		this.password = password;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
