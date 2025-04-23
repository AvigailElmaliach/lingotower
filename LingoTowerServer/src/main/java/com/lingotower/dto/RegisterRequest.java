package com.lingotower.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;


public class RegisterRequest {

    @NotBlank(message = "Username is required")
	private String username;

	@NotEmpty(message = "Password cannot be empty")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Password must be at least 8 characters long and contain at least one letter and one number")
	private String password;

	@NotBlank(message = "Email cannot be empty")
	@Email(message = "Invalid email format")
	private String email;
	private String sourceLanguage;
	private String targetLanguage;

	public RegisterRequest() {
	}

	public RegisterRequest(String username, String password, String email, String sourceLanguage) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.sourceLanguage = sourceLanguage;
		this.targetLanguage = determineTargetLanguage(sourceLanguage);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSourceLanguage() {
		return sourceLanguage;
	}

	public void setSourceLanguage(String sourceLanguage) {
		this.sourceLanguage = sourceLanguage;
		this.targetLanguage = determineTargetLanguage(sourceLanguage);
	}

	public String getTargetLanguage() {
		return targetLanguage;
	}

	private String determineTargetLanguage(String sourceLanguage) {
		return "he".equals(sourceLanguage) ? "en" : "he";
	}
}
