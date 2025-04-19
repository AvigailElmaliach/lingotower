package com.lingotower.dto.user;

/**
 * Data Transfer Object (DTO) for updating user information. This class is used
 * to encapsulate user update data for transfer between layers.
 */
public class UserUpdateDTO {
	private String username; // The username of the user
	private String email; // The email address of the user
	private String sourceLanguage; // The source language preference of the user

	/**
	 * Default constructor. This constructor is required for frameworks like Jackson
	 * to deserialize JSON into this DTO.
	 */
	public UserUpdateDTO() {
	}

	/**
	 * Constructor to initialize the UserUpdateDTO with username, email, and source
	 * language.
	 *
	 * @param username       the username of the user
	 * @param email          the email address of the user
	 * @param sourceLanguage the source language preference of the user
	 */
	public UserUpdateDTO(String username, String email, String sourceLanguage) {
		this.username = username;
		this.email = email;
		this.sourceLanguage = sourceLanguage;
	}

	/**
	 * Gets the username of the user.
	 *
	 * @return the username of the user
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username of the user.
	 *
	 * @param username the new username of the user
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the email address of the user.
	 *
	 * @return the email address of the user
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email address of the user.
	 *
	 * @param email the new email address of the user
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the source language preference of the user.
	 *
	 * @return the source language preference of the user
	 */
	public String getSourceLanguage() {
		return sourceLanguage;
	}

	/**
	 * Sets the source language preference of the user.
	 *
	 * @param sourceLanguage the new source language preference of the user
	 */
	public void setSourceLanguage(String sourceLanguage) {
		this.sourceLanguage = sourceLanguage;
	}
}
