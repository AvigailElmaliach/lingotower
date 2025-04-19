package com.lingotower.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an Admin entity that extends the User class. This class is used to
 * manage users and quizzes in the system.
 */
public class Admin extends User {

	private Long id; // The unique identifier for the admin
	private String username; // The username of the admin
	private String password; // The password of the admin
	private String role; // The role of the admin
	private List<User> users = new ArrayList<>(); // List of users managed by the admin
	private List<Quiz> quizzes = new ArrayList<>(); // List of quizzes created by the admin

	/**
	 * Default constructor.
	 */
	public Admin() {
		// This constructor is intentionally empty.
		// It is required for frameworks like Hibernate or JPA to instantiate the
		// entity.
	}

	/**
	 * Gets the unique identifier of the admin.
	 *
	 * @return the unique identifier of the admin
	 */
	@Override
	public Long getId() {
		return id;
	}

	/**
	 * Sets the unique identifier of the admin.
	 *
	 * @param id the unique identifier of the admin
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the username of the admin.
	 *
	 * @return the username of the admin
	 */
	@Override
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username of the admin.
	 *
	 * @param username the new username of the admin
	 */
	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the password of the admin.
	 *
	 * @return the password of the admin
	 */
	@Override
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password of the admin.
	 *
	 * @param password the new password of the admin
	 */
	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the role of the admin.
	 *
	 * @return the role of the admin
	 */
	@Override
	public String getRole() {
		return role;
	}

	/**
	 * Sets the role of the admin.
	 *
	 * @param role the new role of the admin
	 */
	@Override
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * Gets the list of users managed by the admin.
	 *
	 * @return the list of users managed by the admin
	 */
	public List<User> getUsers() {
		return users;
	}

	/**
	 * Sets the list of users managed by the admin.
	 *
	 * @param users the new list of users managed by the admin
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

	/**
	 * Gets the list of quizzes created by the admin.
	 *
	 * @return the list of quizzes created by the admin
	 */
	public List<Quiz> getQuizzes() {
		return quizzes;
	}

	/**
	 * Adds a quiz to the list of quizzes created by the admin. Also sets the admin
	 * as the creator of the quiz.
	 *
	 * @param quiz the quiz to be added
	 */
	public void addQuiz(Quiz quiz) {
		quizzes.add(quiz);
		quiz.setAdminByCreated(this);
	}

	/**
	 * Sets the email of the admin. Overrides the method from the User class.
	 *
	 * @param email the new email of the admin
	 */
	@Override
	public void setEmail(String email) {
		super.setEmail(email);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Admin admin = (Admin) o;
		return id != null && id.equals(admin.id);
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

}
