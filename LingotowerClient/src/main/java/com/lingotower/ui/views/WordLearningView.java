package com.lingotower.ui.views;

import java.io.IOException;

import com.lingotower.model.Category;
import com.lingotower.model.User;
import com.lingotower.ui.controllers.WordLearningController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

public class WordLearningView implements View {

	private Category category;
	private Runnable onBackToDashboard;
	private User currentUser;

	public WordLearningView(Category category, Runnable onBackToDashboard, User currentUser) {
		this.category = category;
		this.onBackToDashboard = onBackToDashboard;
		this.currentUser = currentUser;
	}

	@Override
	public Parent createView() {
		try {
			System.out.println("Creating WordLearningView for category: " + category.getName());

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WordLearning.fxml"));
			Parent view = loader.load();

			WordLearningController controller = loader.getController();

			// Set the user first
			if (currentUser != null) {
				System.out.println("Setting user in controller: " + currentUser.getUsername());
				controller.setUser(currentUser);
			} else {
				System.out.println("WARNING: currentUser is null in WordLearningView");
			}

			// Set category and callbacks
			System.out.println("Setting category in controller: " + category.getName());
			controller.setCategory(category);
			controller.setOnBackToDashboard(onBackToDashboard);

			return view;
		} catch (IOException e) {
			System.err.println("Error loading word learning view: " + e.getMessage());
			e.printStackTrace();
			return new Label("Error loading word learning view: " + e.getMessage());
		}
	}

	@Override
	public void refresh() {
		// No refresh functionality needed
	}
}