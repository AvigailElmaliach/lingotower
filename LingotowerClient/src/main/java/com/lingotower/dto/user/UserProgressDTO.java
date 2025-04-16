package com.lingotower.dto.user;

public class UserProgressDTO {
	private String username;
	private double progressPercentage;

	public UserProgressDTO(String username, double progressPercentage) {
		this.username = username;
		this.progressPercentage = progressPercentage;
	}

	public String getUsername() {
		return username;
	}

	public double getProgressPercentage() {
		return progressPercentage;
	}

	public void setProgressPercentage(Double progressPercentage) {
		this.progressPercentage = progressPercentage;
	}

}
