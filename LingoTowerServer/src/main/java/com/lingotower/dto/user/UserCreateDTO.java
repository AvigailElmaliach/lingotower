package com.lingotower.dto.user;

import com.lingotower.constants.LanguageConstants;

public class UserCreateDTO {
	private String username;
	private String password;
	private String email;
	private String sourceLanguage;
	private String targetLanguage;

	public UserCreateDTO(String username, String password, String email, String sourceLanguage) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.sourceLanguage = sourceLanguage;
		this.targetLanguage = determineTargetLanguage(sourceLanguage);
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public String getSourceLanguage() {
		return sourceLanguage;
	}

	public String getTargetLanguage() {
		return targetLanguage;
	}

	private String determineTargetLanguage(String sourceLanguage) {
		return LanguageConstants.HEBREW.equals(sourceLanguage) ? LanguageConstants.ENGLISH : LanguageConstants.HEBREW;
	}
}
