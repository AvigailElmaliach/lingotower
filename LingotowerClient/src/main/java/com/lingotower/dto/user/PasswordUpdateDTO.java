package com.lingotower.dto.user;

/**
 * Data Transfer Object (DTO) for password update requests. Contains the new
 * password to be set for the user.
 */
public class PasswordUpdateDTO {

	private String newPassword; // The new password to be set for the user

	/**
	 * Default constructor for creating an empty PasswordUpdateDTO object.
	 */
	public PasswordUpdateDTO() {
	}

	/**
	 * Constructor for creating a PasswordUpdateDTO object with the new password.
	 *
	 * @param newPassword The new password to set.
	 */
	public PasswordUpdateDTO(String newPassword) {
		this.newPassword = newPassword;
	}

	/**
	 * Gets the new password.
	 *
	 * @return The new password.
	 */
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * Sets the new password.
	 *
	 * @param newPassword The new password to set.
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
