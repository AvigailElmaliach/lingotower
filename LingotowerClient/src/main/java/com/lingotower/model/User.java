package com.lingotower.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a User entity in the system. This class is used to store and
 * manage user-related data.
 */
public class User {

	private Long id; // The unique identifier for the user
	private String username; // The username of the user
	private String password; // The password of the user
	private String oldPassword;
	private String email; // The email address of the user
	private String language; // The preferred language of the user
	private LocalDateTime atCreated; // The timestamp when the user was created
	private Admin admin; // The admin associated with the user
	private List<Word> learnedWords = new ArrayList<>(); // List of words learned by the user
	private String role; // The role of the user (e.g., admin, regular user)

	/**
	 * Default constructor.
	 */
	public User() {
		// This constructor is intentionally empty.
		// It is required for frameworks like Hibernate or JPA to instantiate the
		// entity.
	}

	/**
	 * Gets the unique identifier of the user.
	 *
	 * @return the unique identifier of the user
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the unique identifier of the user.
	 *
	 * @param id the new unique identifier of the user
	 */
	public void setId(Long id) {
		this.id = id;
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
	 * Gets the password of the user.
	 *
	 * @return the password of the user
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password of the user.
	 *
	 * @param password the new password of the user
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the old password of the user (used for password change).
	 *
	 * @return the old password of the user
	 */
	public String getOldPassword() {
		return oldPassword;
	}

	/**
	 * Sets the old password of the user (used for password change).
	 *
	 * @param oldPassword the old password of the user
	 */
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
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
	 * Gets the preferred language of the user.
	 *
	 * @return the preferred language of the user
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the preferred language of the user.
	 *
	 * @param language the new preferred language of the user
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Gets the timestamp when the user was created.
	 *
	 * @return the creation timestamp of the user
	 */
	public LocalDateTime getAtCreated() {
		return atCreated;
	}

	/**
	 * Sets the timestamp when the user was created.
	 *
	 * @param atCreated the new creation timestamp of the user
	 */
	public void setAtCreated(LocalDateTime atCreated) {
		this.atCreated = atCreated;
	}

	/**
	 * Gets the admin associated with the user.
	 *
	 * @return the admin associated with the user
	 */
	public Admin getAdmin() {
		return admin;
	}

	/**
	 * Sets the admin associated with the user.
	 *
	 * @param admin the new admin associated with the user
	 */
	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	/**
	 * Gets the list of words learned by the user.
	 *
	 * @return the list of words learned by the user
	 */
	public List<Word> getLearnedWords() {
		return learnedWords;
	}

	/**
	 * Sets the list of words learned by the user.
	 *
	 * @param learnedWords the new list of words learned by the user
	 */
	public void setLearnedWords(List<Word> learnedWords) {
		this.learnedWords = learnedWords;
	}

	/**
	 * Adds a word to the list of words learned by the user. Ensures that duplicate
	 * words are not added.
	 *
	 * @param word the word to be added
	 */
	public void addLearnedWord(Word word) {
		if (!learnedWords.contains(word)) {
			learnedWords.add(word);
		}
	}

	/**
	 * Removes a word from the list of words learned by the user.
	 *
	 * @param word the word to be removed
	 */
	public void removeLearnedWord(Word word) {
		learnedWords.remove(word);
	}

	/**
	 * Gets the role of the user.
	 *
	 * @return the role of the user
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Sets the role of the user.
	 *
	 * @param role the new role of the user
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * Checks if this user is equal to another object. Two users are considered
	 * equal if their IDs are the same.
	 *
	 * @param o the object to compare
	 * @return true if the users are equal, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		User user = (User) o;
		return id != null && id.equals(user.id);
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	/**
	 * Returns a string representation of the user.
	 *
	 * @return a string representation of the user
	 */
	@Override
	public String toString() {
		return "User{" + "id=" + id + ", username='" + username + '\'' + ", language='" + language + '\'' + '}';
	}
}
