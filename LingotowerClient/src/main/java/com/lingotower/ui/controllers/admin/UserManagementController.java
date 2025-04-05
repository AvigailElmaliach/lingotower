package com.lingotower.ui.controllers.admin;

import java.util.List;

import com.lingotower.model.Admin;
import com.lingotower.model.User;
import com.lingotower.service.UserService;

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

	private UserService userService;
	private ObservableList<User> usersList = FXCollections.observableArrayList();

	public UserManagementController() {
		// Initialize services
		userService = new UserService();
	}

	@FXML
	private void initialize() {
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

		// Set table items and configure row selection
		userTableView.setItems(usersList);
	}

	public void setAdmin(Admin admin) {
		this.currentAdmin = admin;
	}

	public void setReturnToDashboard(Runnable callback) {
		this.returnToDashboard = callback;
	}

	public void loadUsers() {
		try {
			// Clear current list and load all users
			usersList.clear();
			List<User> users = userService.getAllUsers();
			usersList.addAll(users);

			// Show success message
			showStatusMessage("Loaded " + users.size() + " users", false);
		} catch (Exception e) {
			System.err.println("Error loading users: " + e.getMessage());
			e.printStackTrace();
			showStatusMessage("Error loading users: " + e.getMessage(), true);
		}
	}

	@FXML
	private void handleBackButton() {
		if (returnToDashboard != null) {
			returnToDashboard.run();
		}
	}

	@FXML
	private void handleRefreshButton() {
		loadUsers();
	}

	@FXML
	private void handleSearchButton() {
		String searchText = searchField.getText().trim().toLowerCase();

		if (searchText.isEmpty()) {
			loadUsers(); // If search is empty, reload all
			return;
		}

		try {
			// Filter the users list based on search text
			List<User> allUsers = userService.getAllUsers();
			List<User> filteredUsers = allUsers.stream()
					.filter(user -> user.getUsername().toLowerCase().contains(searchText)
							|| user.getEmail().toLowerCase().contains(searchText))
					.toList();

			// Update the table
			usersList.clear();
			usersList.addAll(filteredUsers);

			// Show status message
			showStatusMessage("Found " + filteredUsers.size() + " matching users", false);
		} catch (Exception e) {
			System.err.println("Error searching users: " + e.getMessage());
			e.printStackTrace();
			showStatusMessage("Error searching users: " + e.getMessage(), true);
		}
	}

	private void showEditForm(User user) {
		// Store the selected user
		this.selectedUser = user;

		// Fill the form fields
		usernameField.setText(user.getUsername());
		emailField.setText(user.getEmail());
		languageField.setText(user.getLanguage());

		// Show the edit form
		editUserForm.setVisible(true);
		confirmationDialog.setVisible(false);
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

		try {
			// Update user data
			selectedUser.setUsername(usernameField.getText().trim());
			selectedUser.setEmail(emailField.getText().trim());
			selectedUser.setLanguage(languageField.getText().trim());

			// Save the user
			boolean success = userService.updateUser(selectedUser);

			if (success) {
				// Hide the form
				editUserForm.setVisible(false);

				// Refresh the list
				loadUsers();

				// Show success message
				showStatusMessage("User updated successfully", false);
			} else {
				showStatusMessage("Failed to update user", true);
			}
		} catch (Exception e) {
			System.err.println("Error updating user: " + e.getMessage());
			e.printStackTrace();
			showStatusMessage("Error updating user: " + e.getMessage(), true);
		}
	}

	private void showDeleteConfirmation(User user) {
		// Store the selected user
		this.selectedUser = user;

		// Update the confirmation message
		confirmUserInfoLabel.setText("Username: " + user.getUsername() + ", Email: " + user.getEmail());

		// Show the confirmation dialog
		confirmationDialog.setVisible(true);
		editUserForm.setVisible(false);
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

		try {
			// Delete the user
			boolean success = userService.deleteUser(selectedUser.getId());

			if (success) {
				// Hide the dialog
				confirmationDialog.setVisible(false);

				// Refresh the list
				loadUsers();

				// Show success message
				showStatusMessage("User deleted successfully", false);
			} else {
				showStatusMessage("Failed to delete user", true);
			}
		} catch (Exception e) {
			System.err.println("Error deleting user: " + e.getMessage());
			e.printStackTrace();
			showStatusMessage("Error deleting user: " + e.getMessage(), true);
		}
	}

	private void showStatusMessage(String message, boolean isError) {
		statusLabel.setText(message);
		statusLabel.getStyleClass().removeAll("error-message", "success-message");
		statusLabel.getStyleClass().add(isError ? "error-message" : "success-message");
		statusLabel.setVisible(true);
	}
}