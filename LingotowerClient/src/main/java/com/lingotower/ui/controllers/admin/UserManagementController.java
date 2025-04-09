// Modified UserManagementController with improved UI handling
package com.lingotower.ui.controllers.admin;

import java.util.List;

import com.lingotower.model.Admin;
import com.lingotower.model.User;
import com.lingotower.security.TokenStorage;
import com.lingotower.service.AdminService;
import com.lingotower.service.UserService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
		// Initialize services
		this.userService = new UserService();
	}

	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
		System.out.println("AdminService set in UserManagementController");
		TokenStorage.logTokenStatus("After setting AdminService");
	}

	@FXML
	private void initialize() {
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

		// Setup actions column with buttons
		actionsColumn.setCellFactory(column -> new TableCell<User, String>() {
			private final Button editButton = new Button("Edit");
			private final Button deleteButton = new Button("Delete");
			private final HBox pane = new HBox(5);

			{
				editButton.getStyleClass().add("small-button");
				deleteButton.getStyleClass().add("small-button");
				deleteButton.getStyleClass().add("danger-button");

				editButton.setOnAction(event -> {
					User user = getTableView().getItems().get(getIndex());
					showEditForm(user);
				});

				deleteButton.setOnAction(event -> {
					User user = getTableView().getItems().get(getIndex());
					showDeleteConfirmation(user);
				});

				pane.getChildren().addAll(editButton, deleteButton);
			}

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(pane);
				}
			}
		});

		// Set table items
		userTableView.setItems(usersList);

		// Add search field listener for real-time filtering
		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			handleSearchButton();
		});
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

				if (adminService == null) {
					System.err.println("AdminService not set, creating new instance");
					adminService = new AdminService();
				}

				// Load all users using the admin service
				List<User> users = adminService.getAllUsers();
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
		confirmationDialog.setVisible(false);

		// Store the selected user
		this.selectedUser = user;

		// Fill the form fields
		usernameField.setText(user.getUsername());
		emailField.setText(user.getEmail());
		languageField.setText(user.getLanguage());

		// Show the edit form
		editUserForm.setVisible(true);
	}

	@FXML
	private void handleCancelEdit() {
		editUserForm.setVisible(false);
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
						editUserForm.setVisible(false);

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

	private void showDeleteConfirmation(User user) {
		// Hide edit form if visible
		editUserForm.setVisible(false);

		// Store the selected user
		this.selectedUser = user;

		// Update the confirmation message
		confirmUserInfoLabel.setText("Username: " + user.getUsername() + ", Email: " + user.getEmail());

		// Show the confirmation dialog
		confirmationDialog.setVisible(true);
	}

	@FXML
	private void handleCancelDelete() {
		confirmationDialog.setVisible(false);
		this.selectedUser = null;
	}

	@FXML
	private void handleConfirmDelete() {
		if (selectedUser == null) {
			showStatusMessage("No user selected", true);
			return;
		}

		// Show loading status
		showStatusMessage("Deleting user...", false);

		// Delete in background thread
		Thread deleteThread = new Thread(() -> {
			try {
				// Delete the user
				boolean success = userService.deleteUser(selectedUser.getId());

				// Update UI on JavaFX thread
				Platform.runLater(() -> {
					if (success) {
						// Hide the dialog
						confirmationDialog.setVisible(false);

						// Remove from the list
						usersList.remove(selectedUser);

						// Reset selection
						selectedUser = null;

						// Show success message
						showStatusMessage("User deleted successfully", false);
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

	private void showStatusMessage(String message, boolean isError) {
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