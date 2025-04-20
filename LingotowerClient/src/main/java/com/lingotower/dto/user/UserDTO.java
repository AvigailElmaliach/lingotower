package com.lingotower.dto.user;

/**
 * Data Transfer Object (DTO) representing user information. required by
 * frameworks like Jackson/RestTemplate for deserialization.
 */
public class UserDTO {

	private Long id;
	private String username;
	private String email;
	private String language;

	/**
	 * Default constructor required by frameworks like Jackson.
	 */
	public UserDTO() {
		// Default constructor
	}

	/**
	 * Parameterized constructor for creating UserDTO instances.
	 *
	 * @param id       The unique identifier of the user.
	 * @param username The username of the user.
	 * @param email    The email address of the user.
	 * @param language The preferred language code of the user.
	 */
	public UserDTO(Long id, String username, String email, String language) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.language = language;
	}

	// --- Getters and Setters ---

	/**
	 * Gets the unique identifier of the user.
	 *
	 * @return The user's ID.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the unique identifier of the user.
	 *
	 * @param id The user's ID to set.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the username of the user.
	 *
	 * @return The username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username of the user.
	 *
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the email address of the user.
	 *
	 * @return The email address.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email address of the user.
	 *
	 * @param email The email address to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the preferred language code of the user.
	 *
	 * @return The language code.
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the preferred language code of the user.
	 *
	 * @param language The language code to set.
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Returns a string representation of the UserDTO object.
	 *
	 * @return A string containing the user's details.
	 */
	@Override
	public String toString() {
		return "UserDTO{" + "id=" + id + ", username='" + username + '\'' + ", email='" + email + '\'' + ", language='"
				+ language + '\'' + '}';
	}
}
