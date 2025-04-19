package com.lingotower.dto.user;

/**
 * Data Transfer Object (DTO) representing the progress of a user. This class is
 * used to encapsulate user progress data for transfer between layers.
 */
public class UserProgressDTO {
	private String username; // The username of the user
	private double progressPercentage; // The progress percentage of the user

	/**
	 * Constructor to initialize the UserProgressDTO with a username and progress
	 * percentage.
	 *
	 * @param username           the username of the user
	 * @param progressPercentage the progress percentage of the user
	 */
	public UserProgressDTO(String username, double progressPercentage) {
		this.username = username;
		this.progressPercentage = progressPercentage;
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
	 * Gets the progress percentage of the user.
	 *
	 * @return the progress percentage of the user
	 */
	public double getProgressPercentage() {
		return progressPercentage;
	}

	/**
	 * Sets the progress percentage of the user.
	 *
	 * @param progressPercentage the new progress percentage of the user
	 */
	public void setProgressPercentage(Double progressPercentage) {
		this.progressPercentage = progressPercentage;
	}
}
