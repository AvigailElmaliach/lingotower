package com.lingotower.ui.controllers.profile;

import org.slf4j.Logger;

import com.lingotower.utils.LoggingUtility;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Manages the UI state for the UserProfileController Handles tab switching,
 * error messages, and responsive layout
 */
public class UIStateManager {
	private static final Logger logger = LoggingUtility.getLogger(UIStateManager.class);

	// Tab buttons
	private final Button profileTabButton;
	private final Button progressTabButton;
	private final Button wordsTabButton;

	// Content panels
	private final VBox profileContent;
	private final VBox progressContent;
	private final VBox wordsContent;

	// Status label for showing messages
	private final Label statusLabel;

	/**
	 * Constructor with all UI components to manage
	 */
	public UIStateManager(Button profileTabButton, Button progressTabButton, Button wordsTabButton, VBox profileContent,
			VBox progressContent, VBox wordsContent, Label statusLabel) {

		this.profileTabButton = profileTabButton;
		this.progressTabButton = progressTabButton;
		this.wordsTabButton = wordsTabButton;
		this.profileContent = profileContent;
		this.progressContent = progressContent;
		this.wordsContent = wordsContent;
		this.statusLabel = statusLabel;

		// Initialize status label as hidden
		if (statusLabel != null) {
			statusLabel.setVisible(false);
		}
	}

	/**
	 * Shows the profile tab
	 */
	public void showProfileTab() {
		// Update active tab styling
		profileTabButton.getStyleClass().add("custom-tab-active");
		progressTabButton.getStyleClass().remove("custom-tab-active");
		wordsTabButton.getStyleClass().remove("custom-tab-active");

		// Show/hide content
		profileContent.setVisible(true);
		progressContent.setVisible(false);
		wordsContent.setVisible(false);
	}

	/**
	 * Shows the progress tab
	 */
	public void showProgressTab() {
		// Update active tab styling
		profileTabButton.getStyleClass().remove("custom-tab-active");
		progressTabButton.getStyleClass().add("custom-tab-active");
		wordsTabButton.getStyleClass().remove("custom-tab-active");

		// Show/hide content
		profileContent.setVisible(false);
		progressContent.setVisible(true);
		wordsContent.setVisible(false);
	}

	/**
	 * Shows the words tab
	 */
	public void showWordsTab() {
		// Update active tab styling
		profileTabButton.getStyleClass().remove("custom-tab-active");
		progressTabButton.getStyleClass().remove("custom-tab-active");
		wordsTabButton.getStyleClass().add("custom-tab-active");

		// Show/hide content
		profileContent.setVisible(false);
		progressContent.setVisible(false);
		wordsContent.setVisible(true);
	}

	/**
	 * Shows an error message
	 */
	public void showErrorMessage(String message) {
		logger.debug("Showing error message: {}", message);
		showStatusMessage(message, true);
	}

	/**
	 * Shows a success message
	 */
	public void showSuccessMessage(String message) {
		logger.debug("Showing success message: {}", message);
		showStatusMessage(message, false);
	}

	/**
	 * Shows a status message with styling based on type
	 */
	public void showStatusMessage(String message, boolean isError) {
		if (statusLabel == null)
			return;

		statusLabel.setText(message);
		statusLabel.setStyle(isError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #2ecc71;");
		statusLabel.setVisible(true);

		// Auto-hide success messages after 5 seconds
		if (!isError) {
			Thread thread = new Thread(() -> {
				try {
					Thread.sleep(5000);
					Platform.runLater(() -> statusLabel.setVisible(false));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			});
			thread.setDaemon(true);
			thread.start();
		}
	}

	/**
	 * Sets up responsive layout for the view
	 */
	public void setupResponsiveLayout(BorderPane view) {
		// Listen for scene changes
		view.sceneProperty().addListener((observable, oldScene, newScene) -> {
			if (newScene != null) {
				// Listen for window changes
				newScene.windowProperty().addListener((prop, oldWindow, newWindow) -> {
					if (newWindow != null) {
						// Add listeners for width changes
						newWindow.widthProperty().addListener((obs, oldWidth, newWidth) -> {
							adjustLayoutForWidth(newWidth.doubleValue());
						});

						// Initial adjustment
						adjustLayoutForWidth(newWindow.getWidth());
					}
				});
			}
		});
		logger.debug("Responsive layout setup complete");
	}

	/**
	 * Adjusts the layout based on available width
	 */
	private void adjustLayoutForWidth(double width) {
		// For very narrow screens
		if (width < 500) {
			logger.trace("Adjusting layout for narrow width: {}", width);
			// Make profile content more compact
			if (profileContent != null) {
				profileContent.setSpacing(10); // Reduce spacing
			}

			// Make word list take full width
			if (wordsContent != null) {
				VBox.setMargin(wordsContent, new Insets(5));
			}
		} else {
			// For larger screens, reset to default
			if (profileContent != null) {
				profileContent.setSpacing(20); // Normal spacing
			}

			if (wordsContent != null) {
				VBox.setMargin(wordsContent, new Insets(15));
			}
		}
	}
}