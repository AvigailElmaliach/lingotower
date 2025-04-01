package com.lingotower.ui.views;

import java.io.IOException;

import com.lingotower.model.User;
import com.lingotower.ui.controllers.DailyWordController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

/**
 * View class for the Daily Word feature
 */
public class DailyWordView implements View {

	private FXMLLoader loader;
	private Parent root;
	private DailyWordController controller;
	private Runnable onBackToDashboard;
	private User currentUser;

	public DailyWordView(User user, Runnable onBackToDashboard) {
		this.currentUser = user;
		this.onBackToDashboard = onBackToDashboard;
		initializeLoader();
	}

	private void initializeLoader() {
		loader = new FXMLLoader(getClass().getResource("/fxml/DailyWordView.fxml"));
	}

	@Override
	public Parent createView() {
		try {
			if (root == null) {
				root = loader.load();
				controller = loader.getController();

				// Set the user to the controller
				if (currentUser != null) {
					controller.setUser(currentUser);
				}

				// Add any other callbacks if needed in the future
			}
			return root;
		} catch (IOException e) {
			e.printStackTrace();
			// Create a simple error view as fallback
			return new Label("Error loading daily word view: " + e.getMessage());
		}
	}

	@Override
	public void refresh() {
		// Nothing special to refresh here, the controller handles updating the word
		// This could be used to force refresh the daily word if needed
	}

	public DailyWordController getController() {
		return controller;
	}
}