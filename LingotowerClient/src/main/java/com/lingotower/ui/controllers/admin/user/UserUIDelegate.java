package com.lingotower.ui.controllers.admin.user;

import org.slf4j.Logger;

import com.lingotower.utils.LoggingUtility;

import javafx.application.Platform;

/**
 * Delegate class for user interface operations
 */
public class UserUIDelegate {
	private static final Logger logger = LoggingUtility.getLogger(UserUIDelegate.class);

	private final UserManagementController controller;

	public UserUIDelegate(UserManagementController controller) {
		this.controller = controller;
	}

	/**
	 * Set up UI components
	 */
	public void setupUIComponents() {
		// Hide status message initially
		if (controller.getStatusLabel() != null) {
			controller.getStatusLabel().setVisible(false);
		}

		// Hide forms initially
		if (controller.getEditUserForm() != null) {
			controller.getEditUserForm().setVisible(false);
		}

		if (controller.getConfirmationDialog() != null) {
			controller.getConfirmationDialog().setVisible(false);
		}
	}

	/**
	 * Shows a status message with auto-hiding for success messages
	 */
	public void showStatusMessage(String message, boolean isError) {
		if (controller.getStatusLabel() == null)
			return;

		logger.debug("Status message: {} (isError: {})", message, isError);

		controller.getStatusLabel().setText(message);
		controller.getStatusLabel().getStyleClass().removeAll("error-message", "success-message");
		controller.getStatusLabel().getStyleClass().add(isError ? "error-message" : "success-message");
		controller.getStatusLabel().setVisible(true);

		// Automatically hide success messages after 5 seconds
		if (!isError) {
			Thread timerThread = new Thread(() -> {
				try {
					Thread.sleep(5000);
					Platform.runLater(() -> controller.getStatusLabel().setVisible(false));
				} catch (InterruptedException e) {
					logger.debug("Status message timer interrupted", e);
				}
			});
			timerThread.setDaemon(true);
			timerThread.setName("StatusMessageTimer");
			timerThread.start();
		}
	}
}