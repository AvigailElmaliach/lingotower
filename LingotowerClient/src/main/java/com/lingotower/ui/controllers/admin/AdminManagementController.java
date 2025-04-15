package com.lingotower.ui.controllers.admin;

import java.util.List;

import com.lingotower.model.Admin;
import com.lingotower.security.TokenStorage;
import com.lingotower.service.AdminService;
import com.lingotower.ui.components.ActionButtonCell;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class AdminManagementController {

	@FXML
	private BorderPane view;

	@FXML
	private TableView<Admin> adminTableView;

	@FXML
	private TableColumn<Admin, Long> idColumn;

	@FXML
	private TableColumn<Admin, String> usernameColumn;

	@FXML
	private TableColumn<Admin, String> emailColumn;

	@FXML
	private TableColumn<Admin, String> roleColumn;

	@FXML
	private TableColumn<Admin, String> actionsColumn;

	@FXML
	private VBox editAdminForm;

	@FXML
	private Label formTitleLabel;

	@FXML
	private TextField usernameField;

	@FXML
	private TextField emailField;

	@FXML
	private TextField passwordField;

	@FXML
	private ComboBox<String> roleComboBox;

	@FXML
	private VBox confirmationDialog;

	@FXML
	private Label confirmAdminInfoLabel;

	@FXML
	private Label statusLabel;

	@FXML
	private TextField searchField;

	private Admin currentAdmin; // The logged-in admin
	private Admin selectedAdmin; // The admin being edited/deleted
	private Runnable returnToDashboard;
	private AdminService adminService;
	private ObservableList<Admin> adminsList = FXCollections.observableArrayList();
	private boolean isAddMode = false;

	public AdminManagementController() {
		// Initialize the service in the constructor
		this.adminService = new AdminService();
	}

	@FXML
	private void initialize() {
		// Hide status message initially
		if (statusLabel != null) {
			statusLabel.setVisible(false);
		}

		// Hide forms initially
		if (editAdminForm != null) {
			editAdminForm.setVisible(false);
		}

		if (confirmationDialog != null) {
			confirmationDialog.setVisible(false);
		}

		// Initialize table columns
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
		emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
		roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

		// Set up the actions column using ActionButtonCell
		actionsColumn.setCellFactory(column -> new ActionButtonCell<>(
				// Edit button handler
				event -> {
					Admin admin = (Admin) event.getSource();
					showEditForm(admin);
				},
				// Delete button handler
				event -> {
					Admin admin = (Admin) event.getSource();
					showDeleteConfirmation(admin);
				}));

		// Set table items
		adminTableView.setItems(adminsList);

		// Initialize role ComboBox
		roleComboBox.setItems(FXCollections.observableArrayList("ADMIN"));
		roleComboBox.setValue("ADMIN");

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

	public void loadAdmins() {
		showStatusMessage("Loading admins...", false);

		// Create a background thread to load admins
		Thread loadThread = new Thread(() -> {
			try {
				System.out.println("Loading admins in background thread...");
				TokenStorage.logTokenStatus("Before loading admins");

				// Get all admins through the service
				List<Admin> admins = adminService.getAllAdmins();

				System.out.println("Admins loaded: " + (admins != null ? admins.size() : "null"));

				// Update UI on JavaFX thread
				Platform.runLater(() -> {
					// Clear current list
					adminsList.clear();

					if (admins != null && !admins.isEmpty()) {
						adminsList.addAll(admins);
						showStatusMessage("Loaded " + admins.size() + " admins", false);
					} else {
						showStatusMessage("No admins found", true);
					}
				});
			} catch (Exception e) {
				System.err.println("Error loading admins: " + e.getMessage());
				e.printStackTrace();

				// Show error on JavaFX thread
				Platform.runLater(() -> {
					showStatusMessage("Error loading admins: " + e.getMessage(), true);
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
		loadAdmins();
	}

	@FXML
	private void handleSearchButton() {
		if (searchField == null)
			return;

		String searchText = searchField.getText().trim().toLowerCase();

		// If search is empty, show all admins
		if (searchText.isEmpty()) {
			adminTableView.setItems(adminsList);
			return;
		}

		// Filter the admins list based on search text
		ObservableList<Admin> filteredList = FXCollections.observableArrayList();
		for (Admin admin : adminsList) {
			if (admin.getUsername().toLowerCase().contains(searchText)
					|| (admin.getEmail() != null && admin.getEmail().toLowerCase().contains(searchText))) {
				filteredList.add(admin);
			}
		}

		// Update the table view with filtered results
		adminTableView.setItems(filteredList);
		showStatusMessage("Found " + filteredList.size() + " matching admins", false);
	}

	@FXML
	private void handleAddButton() {
		// Reset form for new admin
		isAddMode = true;
		formTitleLabel.setText("Add New Admin");
		usernameField.setText("");
		emailField.setText("");
		passwordField.setText("");
		roleComboBox.setValue("ADMIN");
		selectedAdmin = null;

		// Show the form
		editAdminForm.setVisible(true);
	}

	private void showEditForm(Admin admin) {
		// Hide confirmation dialog if visible
		if (confirmationDialog != null) {
			confirmationDialog.setVisible(false);
		}

		// Set edit mode
		isAddMode = false;

		// Store the selected admin
		this.selectedAdmin = admin;

		// Set form title
		formTitleLabel.setText("Edit Admin");

		// Fill the form fields
		if (usernameField != null)
			usernameField.setText(admin.getUsername());
		if (emailField != null)
			emailField.setText(admin.getEmail());
		if (passwordField != null)
			passwordField.setText(""); // Don't show existing password
		if (roleComboBox != null)
			roleComboBox.setValue(admin.getRole());

		// Show the edit form
		if (editAdminForm != null) {
			editAdminForm.setVisible(true);
		}
	}

	@FXML
	private void handleCancelEdit() {
		if (editAdminForm != null) {
			editAdminForm.setVisible(false);
		}
		this.selectedAdmin = null;
		isAddMode = false;
	}

	@FXML
	private void handleSaveAdmin() {
		// Get values from form
		String username = usernameField.getText().trim();
		String email = emailField.getText().trim();
		String password = passwordField.getText().trim();
		String role = roleComboBox.getValue();

		// Validate inputs
		if (username.isEmpty() || email.isEmpty()) {
			showStatusMessage("Username and email are required", true);
			return;
		}

		// In add mode, password is required
		if (isAddMode && password.isEmpty()) {
			showStatusMessage("Password is required for new admins", true);
			return;
		}

		// Show loading status
		showStatusMessage(isAddMode ? "Creating admin..." : "Updating admin...", false);

		// Create or update in background thread
		Thread saveThread = new Thread(() -> {
			try {
				if (isAddMode) {
					// Create new admin
					Admin newAdmin = new Admin();
					newAdmin.setUsername(username);
					newAdmin.setEmail(email);
					newAdmin.setPassword(password);
					newAdmin.setRole(role);

					// Save using service
					Admin createdAdmin = adminService.createAdmin(newAdmin);

					// Update UI on JavaFX thread
					Platform.runLater(() -> {
						if (createdAdmin != null) {
							// Add to list
							adminsList.add(createdAdmin);

							// Hide form
							editAdminForm.setVisible(false);

							// Reset state
							selectedAdmin = null;
							isAddMode = false;

							// Show success message
							showStatusMessage("Admin created successfully", false);
						} else {
							showStatusMessage("Failed to create admin", true);
						}
					});
				} else {
					// Update existing admin
					if (selectedAdmin == null) {
						Platform.runLater(() -> {
							showStatusMessage("No admin selected for update", true);
						});
						return;
					}

					// Update fields
					selectedAdmin.setUsername(username);
					selectedAdmin.setEmail(email);
					if (!password.isEmpty()) {
						selectedAdmin.setPassword(password);
					}
					selectedAdmin.setRole(role);

					// Save using service
					boolean success = adminService.updateAdmin(selectedAdmin.getId(), selectedAdmin);

					// Update UI on JavaFX thread
					Platform.runLater(() -> {
						if (success) {
							// Hide form
							editAdminForm.setVisible(false);

							// Refresh the list
							int index = adminsList.indexOf(selectedAdmin);
							if (index >= 0) {
								adminsList.set(index, selectedAdmin);
							}

							// Reset state
							selectedAdmin = null;

							// Show success message
							showStatusMessage("Admin updated successfully", false);
						} else {
							showStatusMessage("Failed to update admin", true);
						}
					});
				}
			} catch (Exception e) {
				System.err.println("Error saving admin: " + e.getMessage());
				e.printStackTrace();

				// Show error on JavaFX thread
				Platform.runLater(() -> {
					showStatusMessage("Error saving admin: " + e.getMessage(), true);
				});
			}
		});

		// Start the background thread
		saveThread.setDaemon(true);
		saveThread.start();
	}

	private void showDeleteConfirmation(Admin admin) {
		if (admin == null) {
			System.err.println("Cannot show confirmation for null admin");
			return;
		}

		// Don't allow deleting yourself
		if (currentAdmin != null && admin.getId().equals(currentAdmin.getId())) {
			showStatusMessage("You cannot delete your own admin account", true);
			return;
		}

		// ✅ SET selectedAdmin before showing dialog
		this.selectedAdmin = admin;

		// Create a confirmation dialog
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Delete Confirmation");
		dialog.setHeaderText("Are you sure you want to delete this admin?");
		dialog.setContentText("Username: " + admin.getUsername() + "\nEmail: "
				+ (admin.getEmail() != null ? admin.getEmail() : "N/A"));

		// Add buttons
		ButtonType deleteButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
		ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().addAll(deleteButtonType, cancelButtonType);

		// Show the dialog
		dialog.showAndWait().ifPresent(response -> {
			if (response == deleteButtonType) {
				handleConfirmDelete();
			} else {
				handleCancelDelete();
			}
		});
	}

	@FXML
	public void handleCancelDelete() {
		// Hide the confirmation dialog
		if (confirmationDialog != null) {
			confirmationDialog.setVisible(false);
		}

		// Clear the selected admin
		this.selectedAdmin = null;
	}

	@FXML
	public void handleConfirmDelete() {
		// Check if we have a selected admin
		if (selectedAdmin == null) {
			showStatusMessage("No admin selected for deletion", true);
			return;
		}

		// Ensure we have a valid ID
		if (selectedAdmin.getId() == null) {
			showStatusMessage("Invalid admin ID", true);
			return;
		}

		// Show loading status
		showStatusMessage("Deleting admin...", false);

		// Delete in background thread
		Thread deleteThread = new Thread(() -> {
			try {
				// Call the AdminService to perform the delete operation
				boolean success = adminService.deleteAdmin(selectedAdmin.getId());

				// Update UI on JavaFX thread
				Platform.runLater(() -> {
					if (success) {
						// Remove the admin from the list
						adminsList.remove(selectedAdmin);

						// Clear selected admin
						selectedAdmin = null;

						// Show success message
						showStatusMessage("Admin deleted successfully", false);
					} else {
						showStatusMessage("Failed to delete admin. Please check permissions and try again.", true);
					}
				});
			} catch (Exception e) {
				System.err.println("Error deleting admin: " + e.getMessage());
				e.printStackTrace();

				// Show error on JavaFX thread
				Platform.runLater(() -> {
					showStatusMessage("Error deleting admin: " + e.getMessage(), true);
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