package com.lingotower.dto.admin;

import com.lingotower.model.Role;

//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotEmpty;
//import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object (DTO) for updating an admin user's details. Contains
 * fields for username, role, password, and email.
 */
public class AdminUpdateDTO {
	//@NotBlank(message = "Username is required")
	private String username;
	private Role role;
	//@NotEmpty(message = "Password cannot be empty")
	//@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Password must be at least 8 characters long and contain at least one letter and one number")
	private String password;
	//@NotBlank(message = "Email cannot be empty")
	//@Email(message = "Invalid email format")
	private String email;

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
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
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
	 * @param role The role to set.
	 */
	public void setRole(Role role) {
		this.role = role;
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
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
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
	 * @param email The email address to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}
}
