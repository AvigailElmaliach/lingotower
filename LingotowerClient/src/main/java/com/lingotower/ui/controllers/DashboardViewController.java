package com.lingotower.ui.controllers;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;

import com.lingotower.model.Category;
import com.lingotower.model.User;
import com.lingotower.service.CategoryService;
import com.lingotower.utils.LoggingUtility;

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

	private static final Logger logger = LoggingUtility.getLogger(DashboardViewController.class);

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
	private CategoryService categoryService;
	private MainApplicationController mainController;

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the FXML file has been loaded.
	 */
	@FXML
	private void initialize() {
		logger.debug("Initializing DashboardViewController");
		categoryService = new CategoryService();

		// Load categories when the view initializes
		loadCategories();

		// Hide error message initially
		if (errorMessageLabel != null) {
			errorMessageLabel.setVisible(false);
		}

		// Set up logout button if present
		if (logoutButton != null) {
			logoutButton.setOnAction(e -> handleLogout());
		}
		logger.debug("DashboardViewController initialization complete");
	}

	public void setMainController(MainApplicationController mainController) {
		logger.debug("Setting main controller reference");
		this.mainController = mainController;
	}

	/**
	 * Set the current user
	 *
	 * @param user The current user
	 */
	public void setUser(User user) {
		logger.debug("Setting user: {}", user != null ? user.getUsername() : "null");
		this.currentUser = user;
		
	}

	/**
	 * Set the logout callback
	 *
	 * @param callback The callback to run when logout is requested
	 */
	public void setOnLogoutCallback(Runnable callback) {
		logger.debug("Setting logout callback");
		this.onLogoutCallback = callback;
	}

	/**
	 * Handle logout request
	 */
	private void handleLogout() {
		logger.info("Logout requested");
		if (onLogoutCallback != null) {
			onLogoutCallback.run();
		} else {
			logger.warn("Logout callback is null");
		}
	}

	public void loadCategories() {
		try {
			logger.info("Loading categories");
			// Fetch categories from server
			List<Category> categories = categoryService.getAllCategories();

			// Update UI with categories
			updateCategories(categories);
		} catch (Exception e) {
			logger.error("Error loading categories: {}", e.getMessage(), e);
			showErrorMessage("Error loading categories: " + e.getMessage());
		}
	}

	public void updateCategories(List<Category> categories) {
		// Clear existing content
		categoriesContainer.getChildren().clear();

		// Hide error message
		if (errorMessageLabel != null) {
			errorMessageLabel.setVisible(false);
		}

		// Display message if no categories
		if (categories == null || categories.isEmpty()) {
			logger.info("No categories available");
			Label noCategories = new Label("No categories available");
			noCategories.getStyleClass().add("info-label");
			categoriesContainer.getChildren().add(noCategories);
			return;
		}

		logger.info("Displaying {} categories", categories.size());

		// Add a tile for each category by loading the FXML for each
		for (Category category : categories) {
			//  null check for category object and its name
			if (category == null || category.getName() == null) {
				logger.warn("Skipping category with null object or name.");
				continue; // Skip this iteration if the category or its name is null
			}
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CategoryTile.fxml"));
				VBox categoryTile = loader.load();

				// Configure the controller
				CategoryTileController tileController = loader.getController();
				tileController.setCategory(category);

				// Set callback for when category is selected
				tileController.setOnCategorySelected(() -> {
					try {
						logger.debug("Category selected: {}", category.getName());
						if (mainController != null) {
							logger.debug("Calling mainController.showWordLearningForCategory()");
							mainController.showWordLearningForCategory(category);
						} else {
							throw new IllegalStateException("Main controller is null");
						}
					} catch (Exception e) {
						logger.error("Error handling category selection: {}", e.getMessage(), e);
						showErrorMessage("Error navigating to the selected category: " + e.getMessage());
					}
				});

				// Add to container
				categoriesContainer.getChildren().add(categoryTile);
			} catch (IOException e) {
				logger.error("Error loading category tile: {}", e.getMessage(), e);
				showErrorMessage("Error loading category tile: " + e.getMessage());
			}
		}
	}

	/**
	 * Shows an error message
	 *
	 * @param message The error message to display
	 */
	public void showErrorMessage(String message) {
		logger.debug("Showing error message: {}", message);
		if (errorMessageLabel != null) {
			errorMessageLabel.setText(message);
			errorMessageLabel.setVisible(true);
		}
	}
}