package com.lingotower.dto.admin;

import com.lingotower.model.Role;

/**
 * Data Transfer Object (DTO) for representing an admin user response. Contains
 * user details such as ID, username, email, and role.
 */
public class AdminResponseDTO {

	private String username;
	private String email;
	private Long id; // The unique identifier of the admin
	private Role role;

	/**
	 * Constructor for creating an AdminResponseDTO object with all fields.
	 *
	 * @param id       The unique identifier of the admin.
	 * @param username The username of the admin.
	 * @param email    The email address of the admin.
	 * @param role     The role of the admin.
	 */
	public AdminResponseDTO(Long id, String username, String email, Role role) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.role = role;
	}

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

	/**
	 * Gets the unique identifier of the admin.
	 *
	 * @return The ID.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the unique identifier of the admin.
	 *
	 * @param id The ID to set.
	 */
	public void setId(Long id) {
		this.id = id;
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
}
