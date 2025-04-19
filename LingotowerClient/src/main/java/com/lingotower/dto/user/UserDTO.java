package com.lingotower.dto.user;

/**
 * Data Transfer Object (DTO) representing user information, often received from
 * admin endpoints. Needed by frameworks like Jackson/RestTemplate for
 * deserialization.
 */
public class UserDTO {

	private Long id;
	private String username;
	private String email;
	private String language; // Matches server DTO field

	/**
	 * Default constructor required by frameworks like Jackson.
	 */
	public UserDTO() {
		// Default constructor
	}

	/**
	 * Parameterized constructor for creating UserDTO instances.
	 *
	 * @param id       The user's ID.
	 * @param username The user's username.
	 * @param email    The user's email address.
	 * @param language The user's preferred language code.
	 */
	public UserDTO(Long id, String username, String email, String language) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.language = language;
	}

	// --- Getters and Setters ---

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	// toString() method for easier debugging/logging
	@Override
	public String toString() {
		return "UserDTO{" + "id=" + id + ", username='" + username + '\'' + ", email='" + email + '\'' + ", language='"
				+ language + '\'' + '}';
	}

}