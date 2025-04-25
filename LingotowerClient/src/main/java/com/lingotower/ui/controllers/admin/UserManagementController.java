package com.lingotower.ui.controllers.admin;

import java.util.List;

import org.slf4j.Logger;

import com.lingotower.model.Admin;
import com.lingotower.model.User;
import com.lingotower.security.TokenStorage;
import com.lingotower.service.AdminService;
import com.lingotower.service.UserService;
import com.lingotower.ui.components.ActionButtonCell;
import com.lingotower.ui.views.admin.UserManagementView;
import com.lingotower.utils.LoggingUtility;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class UserManagementController {
	// Initialize logger using LoggingUtility
	private static final Logger logger = LoggingUtility.getLogger(UserManagementController.class);

	@FXML
	private BorderPane view;

	@FXML
	private TableView<User> userTableView;

	@FXML
	private TableColumn<User, Long> idColumn;

	@FXML
	private TableColumn<User, String> usernameColumn;

	@FXML
	private TableColumn<User, String> emailColumn;

	@FXML
	private TableColumn<User, String> languageColumn;

	@FXML
	private TableColumn<User, String> actionsColumn;

	@FXML
	private VBox editUserForm;

	@FXML
	private TextField usernameField;

	@FXML
	private TextField emailField;

	@FXML
	private TextField languageField;

	@FXML
	private VBox confirmationDialog;

	@FXML
	private Label confirmUserInfoLabel;

	@FXML
	private Label statusLabel;

	@FXML
	private TextField searchField;

	@FXML
	private TextField passwordField;

	private Admin currentAdmin; // The logged-in admin
	private User selectedUser; // The user being edited/deleted
	private Runnable returnToDashboard;
	private AdminService adminService;
	private UserService userService;
	private ObservableList<User> usersList = FXCollections.observableArrayList();
	private UserManagementView parentView; // Reference to the parent view

	public UserManagementController() {
		// Initialize the UserService in the constructor
		this.userService = new UserService();
		logger.debug("UserManagementController initialized");
	}

	/**
	 * Sets the parent view reference
	 * 
	 * @param view The parent view
	 */
	public void setParentView(UserManagementView view) {
		this.parentView = view;
		logger.debug("Parent view set in UserManagementController");
	}

	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
		logger.debug("AdminService set in UserManagementController");
		TokenStorage.logTokenStatus("After setting AdminService");
	}

	@FXML
	private void initialize() {
		// Make sure userService is initialized
		if (userService == null) {
			userService = new UserService();
		}

		// Hide status message initially
		if (statusLabel != null) {
			statusLabel.setVisible(false);
		}

		// Hide forms initially
		if (editUserForm != null) {
			editUserForm.setVisible(false);
		}

		if (confirmationDialog != null) {
			confirmationDialog.setVisible(false);
		}

		// Initialize table columns
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
		emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
		languageColumn.setCellValueFactory(new PropertyValueFactory<>("language"));

		// Set up the actions column using ActionButtonCell
		actionsColumn.setCellFactory(column -> new ActionButtonCell<>(
				// Edit button handler
				event -> {
					User user = (User) event.getSource();
					showEditForm(user);
				},
				// Delete button handler
				event -> {
					User user = (User) event.getSource();
					handleDeleteButtonClick(user);
				}));
		// Set table items
		userTableView.setItems(usersList);

		// Add search field listener for real-time filtering
		if (searchField != null) {
			searchField.textProperty().addListener((observable, oldValue, newValue) -> {
				handleSearchButton();
			});
		}

		logger.debug("UserManagementController UI initialization completed");
	}

	public void setAdmin(Admin admin) {
		this.currentAdmin = admin;
		logger.info("Current admin set: {}", admin != null ? admin.getUsername() : "null");
	}

	public void setReturnToDashboard(Runnable callback) {
		this.returnToDashboard = callback;
		logger.debug("Return to dashboard callback set");
	}

	public void loadUsers() {
		showStatusMessage("Loading users...", false);
		long startTime = System.currentTimeMillis();

		// Create a background thread to load users
		Thread loadThread = new Thread(() -> {
			try {
				logger.info("Loading users in background thread...");
				TokenStorage.logTokenStatus("Before loading users");

				List<User> users;

				// Try to load users with AdminService first, then fallback to UserService
				if (adminService != null) {
					users = adminService.getAllUsers();
				} else {
					if (userService == null) {
						userService = new UserService();
					}
					users = userService.getAllUsers();
				}

				logger.info("Users loaded: {}", users != null ? users.size() : "null");

				// Update UI on JavaFX thread
				Platform.runLater(() -> {
					// Clear current list
					usersList.clear();

					if (users != null && !users.isEmpty()) {
						usersList.addAll(users);
						showStatusMessage("Loaded " + users.size() + " users", false);
					} else {
						showStatusMessage("No users found", true);
					}

					long duration = System.currentTimeMillis() - startTime;
					LoggingUtility.logPerformance(logger, "load_users", duration, "success");
					LoggingUtility.logAction(logger, "load",
							currentAdmin != null ? currentAdmin.getUsername() : "system", "users", "success");
				});
			} catch (Exception e) {
				logger.error("Error loading users: {}", e.getMessage(), e);

				// Show error on JavaFX thread
				Platform.runLater(() -> {
					showStatusMessage("Error loading users: " + e.getMessage(), true);

					long duration = System.currentTimeMillis() - startTime;
					LoggingUtility.logPerformance(logger, "load_users", duration, "failed");
					LoggingUtility.logAction(logger, "load",
							currentAdmin != null ? currentAdmin.getUsername() : "system", "users",
							"error: " + e.getMessage());
				});
			}
		});

		// Start the background thread
		loadThread.setDaemon(true);
		loadThread.setName("UserLoader");
		loadThread.start();
	}

	public void handleDeleteButtonClick(User user) {
		if (user == null) {
			showStatusMessage("Cannot delete: User is null", true);
			return;
		}

		logger.info("Delete button clicked for user: {} (ID: {})", user.getUsername(), user.getId());

		// Simply call the existing method to show the confirmation dialog
		showDeleteConfirmation(user);
	}

	@FXML
	private void handleBackButton() {
		if (returnToDashboard != null) {
			try {
				logger.info("Navigating back to dashboard");
				returnToDashboard.run();
				LoggingUtility.logAction(logger, "navigation",
						currentAdmin != null ? currentAdmin.getUsername() : "system", "dashboard", "success");
			} catch (Exception e) {
				logger.error("Error executing dashboard callback: {}", e.getMessage(), e);
			}
		} else {
			logger.error("Dashboard callback is not set.");
		}
	}

	@FXML
	private void handleRefreshButton() {
		logger.info("Refresh button clicked");
		loadUsers();
	}

	@FXML
	private void handleSearchButton() {
		if (searchField == null)
			return;

		String searchText = searchField.getText().trim().toLowerCase();
		logger.debug("Searching for users with text: '{}'", searchText);

		// If search is empty, show all users
		if (searchText.isEmpty()) {
			userTableView.setItems(usersList);
			return;
		}

		// Filter the users list based on search text
		ObservableList<User> filteredList = FXCollections.observableArrayList();
		for (User user : usersList) {
			if (user.getUsername().toLowerCase().contains(searchText)
					|| (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchText))) {
				filteredList.add(user);
			}
		}

		// Update the table view with filtered results
		userTableView.setItems(filteredList);
		showStatusMessage("Found " + filteredList.size() + " matching users", false);
		logger.info("Search completed: found {} matching users", filteredList.size());
	}

	private void showEditForm(User user) {
		// Hide confirmation dialog if visible
		if (confirmationDialog != null) {
			confirmationDialog.setVisible(false);
		}

		// Store the selected user
		this.selectedUser = user;
		// Log the action
		logger.info("Showing edit form for user: {} (ID: {})", user.getUsername(), user.getId());

		// Fill the form fields
		if (usernameField != null)
			usernameField.setText(user.getUsername());
		if (emailField != null)
			emailField.setText(user.getEmail());
		if (languageField != null)
			languageField.setText(user.getLanguage());

		// Show the edit form
		if (editUserForm != null) {
			editUserForm.setVisible(true);
		}
	}

	@FXML
	private void handleCancelEdit() {
		logger.debug("Edit cancelled");
		if (editUserForm != null) {
			editUserForm.setVisible(false);
		}
		this.selectedUser = null;
	}

	@FXML
	private void handleSaveUser() {
		// Handles the action when the user clicks the "Save Changes" button.
		if (selectedUser == null) {
			showStatusMessage("No user selected", true);
			return;
		}

		long startTime = System.currentTimeMillis();

		String username = usernameField.getText().trim();
		String email = emailField.getText().trim();
		String language = languageField.getText().trim();
		String newPassword = passwordField.getText();
		System.out.println("Attempting to save user with username: " + username);

		// Validate required fields
		if (!validateInput(username, email, language)) {
			return; // Validation failed
		}

		// Update the selectedUser object with the new values
		updateSelectedUser(username, email, language, newPassword);

		showStatusMessage("Updating user...", false);
		logger.info("Attempting to update user: {} (ID: {})", username, selectedUser.getId());

		// Perform user update in a background thread
		Thread updateThread = new Thread(() -> performUserUpdate(startTime, username));
		updateThread.start();
	}

	/**
	 * Validates the input fields.
	 */
	private boolean validateInput(String username, String email, String language) {
		if (username.isEmpty() || email.isEmpty()) {
			showStatusMessage("Username and email are required", true);
			return false;
		}
		if (!language.equals("en") && !language.equals("he")) {
			showStatusMessage("Language must be 'en' or 'he'", true);
			return false;
		}
		return true;
	}

	/**
	 * Updates the selectedUser object.
	 */
	private void updateSelectedUser(String username, String email, String language, String newPassword) {
		selectedUser.setUsername(username);
		selectedUser.setEmail(email);
		selectedUser.setLanguage(language); // Ensure field name matches your User model

		if (newPassword != null && !newPassword.isEmpty()) {
			selectedUser.setPassword(newPassword);
		} else {
			selectedUser.setPassword(null);
		}
	}

	/**
	 * Performs the user update.
	 */
	private void performUserUpdate(long startTime, String username) {
		try {
			if (userService == null) {
				userService = new UserService();
			}

			boolean success = userService.updateUser(selectedUser);

			Platform.runLater(() -> handleUpdateResult(success, startTime, username));

		} catch (Exception e) {
			Platform.runLater(() -> handleUpdateError(e, startTime, username));
		}
	}

	/**
	 * Handles the update result on the UI thread.
	 */
	private void handleUpdateResult(boolean success, long startTime, String username) {
		if (success) {
			if (editUserForm != null) {
				editUserForm.setVisible(false);
			}
			int index = usersList.indexOf(selectedUser);
			if (index >= 0) {
				usersList.set(index, selectedUser);
			}
			selectedUser = null;
			showStatusMessage("User updated successfully", false);
			passwordField.clear();
			long duration = System.currentTimeMillis() - startTime;
			LoggingUtility.logPerformance(logger, "update_user", duration, "success");
			LoggingUtility.logAction(logger, "update", currentAdmin != null ? currentAdmin.getUsername() : "system",
					"user:" + username, "success");
		} else {
			showStatusMessage("Failed to update user", true);
			long duration = System.currentTimeMillis() - startTime;
			LoggingUtility.logPerformance(logger, "update_user", duration, "failed");
			LoggingUtility.logAction(logger, "update", currentAdmin != null ? currentAdmin.getUsername() : "system",
					"user:" + username, "failed");
		}
	}

	/**
	 * Handles errors during the update process on the UI thread.
	 */
	private void handleUpdateError(Exception e, long startTime, String username) {
		logger.error("Error updating user: {}", e.getMessage(), e);
		showStatusMessage("Error updating user: " + e.getMessage(), true);
		long duration = System.currentTimeMillis() - startTime;
		LoggingUtility.logPerformance(logger, "update_user", duration, "error");
		LoggingUtility.logAction(logger, "update", currentAdmin != null ? currentAdmin.getUsername() : "system",
				"user:" + username, "error: " + e.getMessage());
	}

	/**
	 * Replace the showDeleteConfirmation method with this implementation that
	 * creates a completely new Dialog instead of using the FXML one
	 */
	private void showDeleteConfirmation(User user) {
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
				// Delete the user directly
				deleteUserDirectly(user);
			} else {
				logger.info("Delete canceled through dialog for user: {}", user.getUsername());
			}
		});
	}

	/**
	 * Add this method to perform direct deletion without using the confirmation
	 * dialog
	 */
	private void deleteUserDirectly(User user) {
		if (user == null || user.getId() == null) {
			showStatusMessage("Invalid user", true);
			return;
		}

		long startTime = System.currentTimeMillis();

		// Show loading status
		showStatusMessage("Deleting user...", false);
		logger.info("Attempting to delete user: {} (ID: {})", user.getUsername(), user.getId());

		// Initialize service if needed
		if (userService == null) {
			userService = new UserService();
		}

		// Delete in background thread
		Thread deleteThread = new Thread(() -> {
			try {
				boolean success = userService.deleteUser(user.getId());

				// Update UI on JavaFX thread
				Platform.runLater(() -> {
					if (success) {
						// Remove from the list
						usersList.remove(user);

						// Show success message
						showStatusMessage("User deleted successfully", false);

						// Refresh the list
						loadUsers();

						long duration = System.currentTimeMillis() - startTime;
						LoggingUtility.logPerformance(logger, "delete_user", duration, "success");
						LoggingUtility.logAction(logger, "delete",
								currentAdmin != null ? currentAdmin.getUsername() : "system",
								"user:" + user.getUsername(), "success");
					} else {
						showStatusMessage("Failed to delete user", true);

						long duration = System.currentTimeMillis() - startTime;
						LoggingUtility.logPerformance(logger, "delete_user", duration, "failed");
						LoggingUtility.logAction(logger, "delete",
								currentAdmin != null ? currentAdmin.getUsername() : "system",
								"user:" + user.getUsername(), "failed");
					}
				});
			} catch (Exception e) {
				logger.error("Error deleting user: {}", e.getMessage(), e);

				// Show error on JavaFX thread
				Platform.runLater(() -> {
					showStatusMessage("Error deleting user: " + e.getMessage(), true);

					long duration = System.currentTimeMillis() - startTime;
					LoggingUtility.logPerformance(logger, "delete_user", duration, "error");
					LoggingUtility.logAction(logger, "delete",
							currentAdmin != null ? currentAdmin.getUsername() : "system", "user:" + user.getUsername(),
							"error: " + e.getMessage());
				});
			}
		});

		// Start the background thread
		deleteThread.setDaemon(true);
		deleteThread.setName("UserDeleter");
		deleteThread.start();
	}

	@FXML
	public void handleCancelDelete() {
		logger.info("Delete operation canceled");

		// Hide the confirmation dialog
		if (confirmationDialog != null) {
			confirmationDialog.setVisible(false);
		}

		// Clear the selected user
		this.selectedUser = null;
	}

	@FXML
	public void handleConfirmDelete() {
		// Check if we have a selected user
		if (selectedUser == null) {
			showStatusMessage("No user selected for deletion", true);
			return;
		}

		logger.info("Confirming deletion of user: {} (ID: {})", selectedUser.getUsername(), selectedUser.getId());

		// Ensure we have a valid ID
		if (selectedUser.getId() == null) {
			showStatusMessage("Invalid user ID", true);
			return;
		}

		long startTime = System.currentTimeMillis();

		// Show loading status
		showStatusMessage("Deleting user...", false);

		// Make sure userService is initialized
		if (userService == null) {
			userService = new UserService();
		}

		// Delete in background thread
		Thread deleteThread = new Thread(() -> {
			try {
				// Call the UserService to perform the delete operation
				boolean success = userService.deleteUser(selectedUser.getId());

				logger.debug("Delete operation result: {}", success ? "success" : "failed");

				// Update UI on JavaFX thread
				Platform.runLater(() -> {
					if (success) {
						// Hide the confirmation dialog
						if (confirmationDialog != null) {
							confirmationDialog.setVisible(false);
						}

						// Remove the user from the list
						usersList.remove(selectedUser);

						// Clear selected user
						selectedUser = null;

						// Show success message
						showStatusMessage("User deleted successfully", false);

						// Optionally refresh the list
						loadUsers();

						// Notify the parent view of the change
						if (parentView != null) {
							parentView.refresh();
						}

						long duration = System.currentTimeMillis() - startTime;
						LoggingUtility.logPerformance(logger, "confirm_delete_user", duration, "success");
						LoggingUtility.logAction(logger, "delete",
								currentAdmin != null ? currentAdmin.getUsername() : "system",
								"user:" + selectedUser.getUsername(), "success");
					} else {
						showStatusMessage("Failed to delete user. Please check permissions and try again.", true);

						long duration = System.currentTimeMillis() - startTime;
						LoggingUtility.logPerformance(logger, "confirm_delete_user", duration, "failed");
						LoggingUtility.logAction(logger, "delete",
								currentAdmin != null ? currentAdmin.getUsername() : "system",
								"user:" + selectedUser.getUsername(), "failed");
					}
				});
			} catch (Exception e) {
				logger.error("Error deleting user: {}", e.getMessage(), e);

				// Show error on JavaFX thread
				Platform.runLater(() -> {
					showStatusMessage("Error deleting user: " + e.getMessage(), true);

					long duration = System.currentTimeMillis() - startTime;
					LoggingUtility.logPerformance(logger, "confirm_delete_user", duration, "error");
					LoggingUtility.logAction(logger, "delete",
							currentAdmin != null ? currentAdmin.getUsername() : "system",
							"user:" + selectedUser.getUsername(), "error: " + e.getMessage());
				});
			}
		});

		// Start the background thread
		deleteThread.setDaemon(true);
		deleteThread.setName("UserDeleter");
		deleteThread.start();
	}

	private void showStatusMessage(String message, boolean isError) {
		if (statusLabel == null)
			return;

		logger.debug("Status message: {} (isError: {})", message, isError);

		statusLabel.setText(message);
		statusLabel.getStyleClass().removeAll("error-message", "success-message");
		statusLabel.getStyleClass().add(isError ? "error-message" : "success-message");
		statusLabel.setVisible(true);

		// Automatically hide success messages after 5 seconds
		if (!isError) {
			Thread timerThread = new Thread(() -> {
				try {
					Thread.sleep(5000);
					Platform.runLater(() -> statusLabel.setVisible(false));
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