package com.lingotower.ui.controllers.admin.user;

import org.slf4j.Logger;

import com.lingotower.model.User;
import com.lingotower.service.UserService;
import com.lingotower.utils.LoggingUtility;

import javafx.application.Platform;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 * Delegate for user editing and deletion functionality
 */
public class UserEditorDelegate {
	private static final Logger logger = LoggingUtility.getLogger(UserEditorDelegate.class);

	private final UserManagementController controller;
	private UserService userService;
	private User selectedUser;
	private UserUIDelegate uiDelegate;
	private UserLoaderDelegate loaderDelegate;

	public UserEditorDelegate(UserManagementController controller) {
		this.controller = controller;
		this.userService = new UserService();
		this.uiDelegate = new UserUIDelegate(controller);
		this.loaderDelegate = new UserLoaderDelegate(controller);
	}

	/**
	 * Display the user edit form
	 */
	public void showEditForm(User user) {
		// Hide confirmation dialog if visible
		if (controller.getConfirmationDialog() != null) {
			controller.getConfirmationDialog().setVisible(false);
		}

		// Store the selected user
		this.selectedUser = user;
		// Log the action
		logger.info("Showing edit form for user: {} (ID: {})", user.getUsername(), user.getId());

		// Fill the form fields
		controller.getUsernameField().setText(user.getUsername());
		controller.getEmailField().setText(user.getEmail());
		controller.getLanguageField().setText(user.getLanguage());

		// Show the edit form
		controller.getEditUserForm().setVisible(true);
	}

	/**
	 * Cancel editing operation
	 */
	public void cancelEdit() {
		logger.debug("Edit cancelled");
		controller.getEditUserForm().setVisible(false);
		this.selectedUser = null;
	}

	/**
	 * Save a user (new or updated)
	 */
	public void saveUser(String username, String email, String language, String newPassword) {
		if (selectedUser == null) {
			uiDelegate.showStatusMessage("No user selected", true);
			return;
		}

		long startTime = System.currentTimeMillis();

		// Validate required fields
		if (!validateInput(username, email, language)) {
			return; // Validation failed
		}

		// Update the selectedUser object with the new values
		updateSelectedUser(username, email, language, newPassword);

		uiDelegate.showStatusMessage("Updating user...", false);
		logger.info("Attempting to update user: {} (ID: {})", username, selectedUser.getId());

		// Perform user update in a background thread
		Thread updateThread = new Thread(() -> performUserUpdate(startTime, username));
		updateThread.setDaemon(true);
		updateThread.setName("UserUpdater");
		updateThread.start();
	}

	/**
	 * Show deletion confirmation dialog
	 */
	public void showDeleteConfirmation(User user) {
		if (user == null) {
			logger.error("Cannot show confirmation for null user");
			return;
		}

		logger.info("Creating confirmation dialog for deleting user: {} (ID: {})", user.getUsername(), user.getId());

		// Store selected user for later use
		this.selectedUser = user;

		// Create a custom confirmation dialog
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Delete Confirmation");
		dialog.setHeaderText("Are you sure you want to delete user '" + user.getUsername() + "'?");
		dialog.setContentText(
				"Username: " + user.getUsername() + "\nEmail: " + (user.getEmail() != null ? user.getEmail() : "N/A"));

		// Set dialog buttons
		ButtonType deleteButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
		ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().addAll(deleteButtonType, cancelButtonType);

		// Show dialog and handle the result
		dialog.showAndWait().ifPresent(buttonType -> {
			if (buttonType == deleteButtonType) {
				logger.info("Delete confirmed through dialog for user: {}", user.getUsername());
				deleteUserDirectly(user);
			} else {
				logger.info("Delete canceled through dialog for user: {}", user.getUsername());
			}
		});
	}

	/**
	 * Cancel delete operation
	 */
	public void cancelDelete() {
		logger.info("Delete operation canceled");
		if (controller.getConfirmationDialog() != null) {
			controller.getConfirmationDialog().setVisible(false);
		}
		this.selectedUser = null;
	}

	/**
	 * Confirm delete operation
	 */
	public void confirmDelete() {
		if (selectedUser == null) {
			uiDelegate.showStatusMessage("No user selected for deletion", true);
			return;
		}

		deleteUserDirectly(selectedUser);
	}

	/**
	 * Validates form input
	 */
	private boolean validateInput(String username, String email, String language) {
		if (username.isEmpty() || email.isEmpty()) {
			uiDelegate.showStatusMessage("Username and email are required", true);
			return false;
		}
		if (!language.equals("en") && !language.equals("he")) {
			uiDelegate.showStatusMessage("Language must be 'en' or 'he'", true);
			return false;
		}
		return true;
	}

	/**
	 * Update the user object with form values
	 */
	private void updateSelectedUser(String username, String email, String language, String newPassword) {
		selectedUser.setUsername(username);
		selectedUser.setEmail(email);
		selectedUser.setLanguage(language);

		if (newPassword != null && !newPassword.isEmpty()) {
			selectedUser.setPassword(newPassword);
		} else {
			selectedUser.setPassword(null);
		}
	}

//	/**
//	 * Perform user update in background
//	 */
//	private void performUserUpdate(long startTime, String username) {
//		try {
//			if (userService == null) {
//				userService = new UserService();
//			}
//
//			boolean success = userService.updateUser(selectedUser);
//			Platform.runLater(() -> handleUpdateResult(success, startTime, username));
//		} catch (Exception e) {
//			Platform.runLater(() -> handleUpdateError(e, startTime, username));
//		}
//	}
	/**
	 * Perform user update in background
	 */
	private void performUserUpdate(long startTime, String username) {
	    try {
	        if (userService == null) {
	            userService = new UserService();
	        }

	        // Get the old username (you may already have this from the selectedUser object)
	        String oldUsername = selectedUser.getUsername();  // assuming selectedUser has the current username

	        // Now call the updateUser method with both the old username and the selectedUser
	        boolean success = userService.updateUser(oldUsername, selectedUser);
	        Platform.runLater(() -> handleUpdateResult(success, startTime, username));
	    } catch (Exception e) {
	        Platform.runLater(() -> handleUpdateError(e, startTime, username));
	    }
	}

	/**
	 * Handle update result
	 */
	private void handleUpdateResult(boolean success, long startTime, String username) {
		if (success) {
			controller.getEditUserForm().setVisible(false);

			// Update the list
			int index = loaderDelegate.getUsersList().indexOf(selectedUser);
			if (index >= 0) {
				loaderDelegate.getUsersList().set(index, selectedUser);
			}

			selectedUser = null;
			uiDelegate.showStatusMessage("User updated successfully", false);
			controller.getPasswordField().clear();

			long duration = System.currentTimeMillis() - startTime;
			LoggingUtility.logPerformance(logger, "update_user", duration, "success");
			LoggingUtility.logAction(logger, "update",
					controller.getCurrentAdmin() != null ? controller.getCurrentAdmin().getUsername() : "system",
					"user:" + username, "success");
		} else {
			uiDelegate.showStatusMessage("Failed to update user", true);
			long duration = System.currentTimeMillis() - startTime;
			LoggingUtility.logPerformance(logger, "update_user", duration, "failed");
			LoggingUtility.logAction(logger, "update",
					controller.getCurrentAdmin() != null ? controller.getCurrentAdmin().getUsername() : "system",
					"user:" + username, "failed");
		}
	}

	/**
	 * Handle update error
	 */
	private void handleUpdateError(Exception e, long startTime, String username) {
		logger.error("Error updating user: {}", e.getMessage(), e);
		uiDelegate.showStatusMessage("Error updating user: " + e.getMessage(), true);
		long duration = System.currentTimeMillis() - startTime;
		LoggingUtility.logPerformance(logger, "update_user", duration, "error");
		LoggingUtility.logAction(logger, "update",
				controller.getCurrentAdmin() != null ? controller.getCurrentAdmin().getUsername() : "system",
				"user:" + username, "error: " + e.getMessage());
	}

	/**
	 * Delete a user directly
	 */
	private void deleteUserDirectly(User user) {
		if (user == null || user.getId() == null) {
			uiDelegate.showStatusMessage("Invalid user", true);
			return;
		}

		long startTime = System.currentTimeMillis();
		uiDelegate.showStatusMessage("Deleting user...", false);
		logger.info("Attempting to delete user: {} (ID: {})", user.getUsername(), user.getId());

		// Initialize service if needed
		if (userService == null) {
			userService = new UserService();
		}

		// Delete in background thread
		Thread deleteThread = new Thread(() -> performUserDeletion(user, startTime));
		deleteThread.setDaemon(true);
		deleteThread.setName("UserDeleter");
		deleteThread.start();
	}

	/**
	 * Perform user deletion in background
	 */
	private void performUserDeletion(User user, long startTime) {
		try {
			boolean success = userService.deleteUser(user.getId());
			Platform.runLater(() -> handleDeletionResult(user, success, startTime));
		} catch (Exception e) {
			Platform.runLater(() -> handleDeletionError(user, e, startTime));
		}
	}

	/**
	 * Handle successful deletion
	 */
	private void handleDeletionResult(User user, boolean success, long startTime) {
		if (success) {
			// Remove from the list
			loaderDelegate.getUsersList().remove(user);

			// Show success message
			uiDelegate.showStatusMessage("User deleted successfully", false);

			// Refresh the list
			controller.loadUsers();

			// Notify the parent view of the change
			if (controller.getParentView() != null) {
				controller.getParentView().refresh();
			}

			long duration = System.currentTimeMillis() - startTime;
			LoggingUtility.logPerformance(logger, "delete_user", duration, "success");
			LoggingUtility.logAction(logger, "delete",
					controller.getCurrentAdmin() != null ? controller.getCurrentAdmin().getUsername() : "system",
					"user:" + user.getUsername(), "success");
		} else {
			uiDelegate.showStatusMessage("Failed to delete user", true);

			long duration = System.currentTimeMillis() - startTime;
			LoggingUtility.logPerformance(logger, "delete_user", duration, "failed");
			LoggingUtility.logAction(logger, "delete",
					controller.getCurrentAdmin() != null ? controller.getCurrentAdmin().getUsername() : "system",
					"user:" + user.getUsername(), "failed");
		}
	}

	/**
	 * Handle deletion error
	 */
	private void handleDeletionError(User user, Exception e, long startTime) {
		logger.error("Error deleting user: {}", e.getMessage(), e);
		uiDelegate.showStatusMessage("Error deleting user: " + e.getMessage(), true);

		long duration = System.currentTimeMillis() - startTime;
		LoggingUtility.logPerformance(logger, "delete_user", duration, "error");
		LoggingUtility.logAction(logger, "delete",
				controller.getCurrentAdmin() != null ? controller.getCurrentAdmin().getUsername() : "system",
				"user:" + user.getUsername(), "error: " + e.getMessage());
	}
}
