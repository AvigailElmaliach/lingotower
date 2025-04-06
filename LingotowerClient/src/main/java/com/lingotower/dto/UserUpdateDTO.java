package com.lingotower.dto;

public class UserUpdateDTO {
	private String username;
	private String email;
	private String language;

	public UserUpdateDTO() {
	}

	public UserUpdateDTO(String username, String email, String language) {
		this.username = username;
		this.email = email;
		this.language = language;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}