package com.lingotower.ui.views;

import java.io.IOException;

import com.lingotower.model.User;
import com.lingotower.ui.controllers.UserProfileController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

public class UserProfileView implements View {

	private FXMLLoader loader;
	private Parent root;
	private UserProfileController controller;
	private User currentUser;

	public UserProfileView() {
		initializeLoader();
	}

	private void initializeLoader() {
		loader = new FXMLLoader(getClass().getResource("/fxml/UserProfile.fxml"));
	}

	@Override
	public Parent createView() {
		try {
			if (root == null) {
				root = loader.load();
				controller = loader.getController();

				// Set the current user if available
				if (currentUser != null) {
					controller.setUser(currentUser);
				}
			}
			return root;
		} catch (IOException e) {
			e.printStackTrace();
			// Create a simple error view as fallback
			return new Label("Error loading user profile view: " + e.getMessage());
		}
	}

	@Override
	public void refresh() {
		if (controller != null) {
			controller.refresh();
		}
	}

	public void setUser(User user) {
		this.currentUser = user;
		if (controller != null) {
			controller.setUser(user);
		}
	}

	public UserProfileController getController() {
		return controller;
	}
}