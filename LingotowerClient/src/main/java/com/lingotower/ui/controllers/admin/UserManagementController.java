package com.lingotower.ui.controllers.admin;

import java.util.List;

import com.lingotower.model.Admin;
import com.lingotower.model.User;
import com.lingotower.security.TokenStorage;
import com.lingotower.service.AdminService;
import com.lingotower.service.UserService;
import com.lingotower.ui.components.ActionButtonCell;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class UserManagementController {

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

	private Admin currentAdmin;
	private User selectedUser;
	private Runnable returnToDashboard;
	private AdminService adminService;
	private UserService userService;
	private ObservableList<User> usersList = FXCollections.observableArrayList();

	public UserManagementController() {
		// Initialize the UserService in the constructor
		this.userService = new UserService();
	}

	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
		System.out.println("AdminService set in UserManagementController");
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

		// Set up the actions column with our custom cell factory
		actionsColumn.setCellFactory(column -> new ActionButtonCell(
				// Edit button handler
				event -> {
					if (event.getSource() instanceof User) {
						showEditForm((User) event.getSource());
					}
				},
				// Delete button handler
				event -> {
					if (event.getSource() instanceof User) {
						handleDeleteButtonClick((User) event.getSource());
					}
				}));

		// Set table items
		userTableView.setItems(usersList);

		// Add search field listener for real-time filtering
		if (searchField != null) {
			searchField.textProperty().addListener((observable, oldValue, newValue) -> {
				handleSearchButton();
			});
		}
	}

	public void setAdmin(Admin admin) {
		this.currentAdmin = admin;
	}

	public void setReturnToDashboard(Runnable callback) {
		this.returnToDashboard = callback;
	}

	public void loadUsers() {
		showStatusMessage("Loading users...", false);

		// Create a background thread to load users
		Thread loadThread = new Thread(() -> {
			try {
				System.out.println("Loading users in background thread...");
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

				System.out.println("Users loaded: " + (users != null ? users.size() : "null"));

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
				});
			} catch (Exception e) {
				System.err.println("Error loading users: " + e.getMessage());
				e.printStackTrace();

				// Show error on JavaFX thread
				Platform.runLater(() -> {
					showStatusMessage("Error loading users: " + e.getMessage(), true);
				});
			}
		});

		// Start the background thread
		loadThread.setDaemon(true);
		loadThread.start();
	}

	public void handleDeleteButtonClick(User user) {
		if (user == null) {
			showStatusMessage("Cannot delete: User is null", true);
			return;
		}

		System.out.println("Delete button clicked for user: " + user.getUsername() + " (ID: " + user.getId() + ")");

		// Simply call the existing method to show the confirmation dialog
		showDeleteConfirmation(user);
	}

	@FXML
	private void handleBackButton() {
		if (returnToDashboard != null) {
			try {
				returnToDashboard.run();
			} catch (Exception e) {
				System.err.println("Error executing dashboard callback: " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			System.err.println("Dashboard callback is not set.");
		}
	}

	@FXML
	private void handleRefreshButton() {
		loadUsers();
	}

	@FXML
	private void handleSearchButton() {
		if (searchField == null)
			return;

		String searchText = searchField.getText().trim().toLowerCase();

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
	}

	private void showEditForm(User user) {
		// Hide confirmation dialog if visible
		if (confirmationDialog != null) {
			confirmationDialog.setVisible(false);
		}

		// Store the selected user
		this.selectedUser = user;
		// debug can delete later
		System.out.println("Selected user set to: " + this.selectedUser.getUsername() + " (ID: "
				+ this.selectedUser.getId() + ")");

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
		if (editUserForm != null) {
			editUserForm.setVisible(false);
		}
		this.selectedUser = null;
	}

	@FXML
	private void handleSaveUser() {
		if (selectedUser == null) {
			showStatusMessage("No user selected", true);
			return;
		}

		// Get updated values from form
		String username = usernameField.getText().trim();
		String email = emailField.getText().trim();
		String language = languageField.getText().trim();

		// Validate inputs
		if (username.isEmpty() || email.isEmpty()) {
			showStatusMessage("Username and email are required", true);
			return;
		}

		// Show loading status
		showStatusMessage("Updating user...", false);

		// Update in background thread
		Thread updateThread = new Thread(() -> {
			try {
				// Make sure userService is initialized
				if (userService == null) {
					userService = new UserService();
				}

				// Update user data
				selectedUser.setUsername(username);
				selectedUser.setEmail(email);
				selectedUser.setLanguage(language);

				// Save the user
				boolean success = userService.updateUser(selectedUser);

				// Update UI on JavaFX thread
				Platform.runLater(() -> {
					if (success) {
						// Hide the form
						if (editUserForm != null) {
							editUserForm.setVisible(false);
						}

						// Refresh the table (maintain full list)
						int index = usersList.indexOf(selectedUser);
						if (index >= 0) {
							usersList.set(index, selectedUser);
						}

						// Reset selection
						selectedUser = null;

						// Show success message
						showStatusMessage("User updated successfully", false);
					} else {
						showStatusMessage("Failed to update user", true);
					}
				});
			} catch (Exception e) {
				System.err.println("Error updating user: " + e.getMessage());
				e.printStackTrace();

				// Show error on JavaFX thread
				Platform.runLater(() -> {
					showStatusMessage("Error updating user: " + e.getMessage(), true);
				});
			}
		});

		// Start the background thread
		updateThread.setDaemon(true);
		updateThread.start();
	}

	/**
	 * Replace the showDeleteConfirmation method with this implementation that
	 * creates a completely new Dialog instead of using the FXML one
	 */
	private void showDeleteConfirmation(User user) {
		if (user == null) {
			System.err.println("Cannot show confirmation for null user");
			return;
		}

		System.out.println("Creating programmatic confirmation dialog for: " + user.getUsername());

		// Store selected user for later use
		this.selectedUser = user;

		// Create a custom confirmation dialog
		javafx.scene.control.Dialog<ButtonType> dialog = new javafx.scene.control.Dialog<>();
		dialog.setTitle("Confirm Delete");
		dialog.setHeaderText("Delete User");
		dialog.setContentText("Are you sure you want to delete user '" + user.getUsername() + "'?");

		// Set dialog buttons
		ButtonType deleteButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
		ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().addAll(deleteButtonType, cancelButtonType);

		// Style the delete button
		Button deleteButton = (Button) dialog.getDialogPane().lookupButton(deleteButtonType);
		deleteButton.getStyleClass().add("delete-button");

		// Show dialog and handle the result
		dialog.showAndWait().ifPresent(buttonType -> {
			if (buttonType == deleteButtonType) {
				System.out.println("Delete confirmed through dialog");

				// Delete the user directly
				deleteUserDirectly(user);
			} else {
				System.out.println("Delete canceled through dialog");
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

		// Show loading status
		showStatusMessage("Deleting user...", false);

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
					} else {
						showStatusMessage("Failed to delete user", true);
					}
				});
			} catch (Exception e) {
				System.err.println("Error deleting user: " + e.getMessage());
				e.printStackTrace();

				// Show error on JavaFX thread
				Platform.runLater(() -> {
					showStatusMessage("Error deleting user: " + e.getMessage(), true);
				});
			}
		});

		// Start the background thread
		deleteThread.setDaemon(true);
		deleteThread.start();
	}

	@FXML
	public void handleCancelDelete() {
		System.out.println("Delete canceled");

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

		System.out.println(
				"Confirming deletion of user: " + selectedUser.getUsername() + " (ID: " + selectedUser.getId() + ")");

		// Ensure we have a valid ID
		if (selectedUser.getId() == null) {
			showStatusMessage("Invalid user ID", true);
			return;
		}

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

				System.out.println("Delete operation result: " + success);

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
					} else {
						showStatusMessage("Failed to delete user. Please check permissions and try again.", true);
					}
				});
			} catch (Exception e) {
				System.err.println("Error deleting user: " + e.getMessage());
				e.printStackTrace();

				// Show error on JavaFX thread
				Platform.runLater(() -> {
					showStatusMessage("Error deleting user: " + e.getMessage(), true);
				});
			}
		});

		// Start the background thread
		deleteThread.setDaemon(true);
		deleteThread.start();
	}

	private void showStatusMessage(String message, boolean isError) {
		if (statusLabel == null)
			return;

		statusLabel.setText(message);
		statusLabel.getStyleClass().removeAll("error-message", "success-message");
		statusLabel.getStyleClass().add(isError ? "error-message" : "success-message");
		statusLabel.setVisible(true);

		// Automatically hide success messages after 5 seconds
		if (!isError) {
			new Thread(() -> {
				try {
					Thread.sleep(5000);
					Platform.runLater(() -> statusLabel.setVisible(false));
				} catch (InterruptedException e) {
					// Ignore
				}
			}).start();
		}
	}

}