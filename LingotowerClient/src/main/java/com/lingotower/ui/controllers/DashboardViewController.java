package com.lingotower.ui.controllers;

import java.io.IOException;
import java.util.List;

import com.lingotower.model.Category;
import com.lingotower.model.User;
import com.lingotower.ui.controllers.CategoryTileController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

/**
 * Controller for the Dashboard View that displays categories and recent
 * activities
 */
public class DashboardViewController {

	@FXML
	private BorderPane view;

	@FXML
	private TilePane categoriesContainer;

	@FXML
	private Label errorMessageLabel;

	@FXML
	private Button logoutButton;

	private User currentUser;
	private Runnable onLogoutCallback;

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the FXML file has been loaded.
	 */
	@FXML
	private void initialize() {
		// Hide error message initially
		if (errorMessageLabel != null) {
			errorMessageLabel.setVisible(false);
		}

		// Set up logout button if present
		if (logoutButton != null) {
			logoutButton.setOnAction(e -> handleLogout());
		}
	}

	/**
	 * Set the current user
	 * 
	 * @param user The current user
	 */
	public void setUser(User user) {
		this.currentUser = user;
		// You can update UI elements based on the user if needed
	}

	/**
	 * Set the logout callback
	 * 
	 * @param callback The callback to run when logout is requested
	 */
	public void setOnLogoutCallback(Runnable callback) {
		this.onLogoutCallback = callback;
	}

	/**
	 * Handle logout request
	 */
	private void handleLogout() {
		if (onLogoutCallback != null) {
			onLogoutCallback.run();
		}
	}

	/**
	 * Updates the categories displayed in the UI
	 * 
	 * @param categories The list of categories to display
	 */
	public void updateCategories(List<Category> categories) {
		// Clear existing content
		categoriesContainer.getChildren().clear();

		// Hide error message
		if (errorMessageLabel != null) {
			errorMessageLabel.setVisible(false);
		}

		// Display message if no categories
		if (categories == null || categories.isEmpty()) {
			Label noCategories = new Label("No categories available");
			noCategories.getStyleClass().add("info-label");
			categoriesContainer.getChildren().add(noCategories);
			return;
		}

		// Add a tile for each category by loading the FXML for each
		for (Category category : categories) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CategoryTile.fxml"));
				VBox categoryTile = loader.load();

				// Configure the controller
				CategoryTileController tileController = loader.getController();
				tileController.setCategory(category);

				// Add to container
				categoriesContainer.getChildren().add(categoryTile);
			} catch (IOException e) {
				System.err.println("Error loading category tile: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Shows an error message
	 * 
	 * @param message The error message to display
	 */
	public void showErrorMessage(String message) {
		if (errorMessageLabel != null) {
			errorMessageLabel.setText(message);
			errorMessageLabel.setVisible(true);
		}
	}
}