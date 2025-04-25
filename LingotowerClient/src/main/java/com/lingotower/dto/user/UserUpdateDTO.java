package com.lingotower.dto.user;

/**
 * Data Transfer Object (DTO) for updating user information. This class is used
 * to encapsulate user update data for transfer between layers.
 */
public class UserUpdateDTO {

	private String username; // The updated username of the user
	private String email; // The updated email address of the user
	private String sourceLanguage; // The updated source language preference of the user
	private String password;

	/**
	 * Default constructor.
	 */
	public UserUpdateDTO() {
	}

	/**
	 * Parameterized constructor to initialize the UserUpdateDTO with user details.
	 *
	 * @param username       The updated username of the user.
	 * @param email          The updated email address of the user.
	 * @param sourceLanguage The updated source language preference of the user.
	 * @param password       The new password of the user.
	 * @param oldPassword    The old password of the user.
	 */
	public UserUpdateDTO(String username, String email, String sourceLanguage, String password) {
		this.username = username;
		this.email = email;
		this.sourceLanguage = sourceLanguage;
		this.password = password;

	}

	/**
	 * Gets the updated username of the user.
	 *
	 * @return The updated username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the updated username of the user.
	 *
	 * @param username The new username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the updated email address of the user.
	 *
	 * @return The updated email address.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the updated email address of the user.
	 *
	 * @param email The new email address to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the updated source language preference of the user.
	 *
	 * @return The updated source language preference.
	 */
	public String getSourceLanguage() {
		return sourceLanguage;
	}

	/**
	 * Sets the updated source language preference of the user.
	 *
	 * @param sourceLanguage The new source language preference to set.
	 */
	public void setSourceLanguage(String sourceLanguage) {
		this.sourceLanguage = sourceLanguage;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}