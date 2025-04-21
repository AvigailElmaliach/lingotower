package com.lingotower.dto.admin;

import com.lingotower.model.Role;

/**
 * Data Transfer Object (DTO) for creating an admin user. Contains user details
 * and role information.
 */
public class AdminCreateDTO {

	private String username;
	private String password;
	private String email;
	private Role role = Role.ADMIN; // The role of the user, default is ADMIN
	private String sourceLanguage;
	private String targetLanguage;

	/**
	 * Gets the username of the admin.
	 *
	 * @return The username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username of the admin.
	 *
	 * @param s The username to set.
	 */
	public void setUsername(String s) {
		username = s;
	}

	/**
	 * Gets the password of the admin.
	 *
	 * @return The password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password of the admin.
	 *
	 * @param s The password to set.
	 */
	public void setPassword(String s) {
		password = s;
	}

	/**
	 * Gets the email address of the admin.
	 *
	 * @return The email address.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email address of the admin.
	 *
	 * @param s The email address to set.
	 */
	public void setEmail(String s) {
		email = s;
	}

	/**
	 * Gets the role of the admin.
	 *
	 * @return The role.
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * Sets the role of the admin.
	 *
	 * @param r The role to set.
	 */
	public void setRole(Role r) {
		role = r;
	}

	/**
	 * Gets the source language selected by the admin.
	 *
	 * @return The source language.
	 */
	public String getSourceLanguage() {
		return sourceLanguage;
	}

	/**
	 * Sets the source language selected by the admin.
	 *
	 * @param s The source language to set.
	 */
	public void setSourceLanguage(String s) {
		sourceLanguage = s;
	}

	/**
	 * Gets the target language determined by the admin.
	 *
	 * @return The target language.
	 */
	public String getTargetLanguage() {
		return targetLanguage;
	}

	/**
	 * Sets the target language determined by the admin.
	 *
	 * @param s The target language to set.
	 */
	public void setTargetLanguage(String s) {
		targetLanguage = s;
	}
}
