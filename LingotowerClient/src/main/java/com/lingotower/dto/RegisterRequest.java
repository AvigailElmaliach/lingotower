package com.lingotower.dto;

/**
 * Represents a registration request containing user details and language
 * preferences.
 */

public class RegisterRequest {

	private String username;
	private String password;
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

	/**
	 * Gets the username.
	 *
	 * @return The username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username.
	 *
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the password.
	 *
	 * @return The password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 *
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the email address.
	 *
	 * @return The email address.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email address.
	 *
	 * @param email The email address to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the source language.
	 *
	 * @return The source language.
	 */
	public String getSourceLanguage() {
		return sourceLanguage;
	}

	/**
	 * Sets the source language and updates the target language accordingly.
	 *
	 * @param sourceLanguage The source language to set.
	 */
	public void setSourceLanguage(String sourceLanguage) {
		this.sourceLanguage = sourceLanguage;
		this.targetLanguage = determineTargetLanguage(sourceLanguage);
	}

	/**
	 * Gets the target language.
	 *
	 * @return The target language.
	 */
	public String getTargetLanguage() {
		return targetLanguage;
	}

	/**
	 * Determines the target language based on the source language. If the source
	 * language is "he" (Hebrew), the target language is "en" (English), and vice
	 * versa.
	 *
	 * @param sourceLanguage The source language.
	 * @return The corresponding target language.
	 */
	private String determineTargetLanguage(String sourceLanguage) {
		return "he".equals(sourceLanguage) ? "en" : "he";
	}
}
