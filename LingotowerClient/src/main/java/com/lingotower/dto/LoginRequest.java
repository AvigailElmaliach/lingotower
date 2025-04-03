package com.lingotower.dto;

public class LoginRequest {

	private String identifier; // יכול להיות username או email
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