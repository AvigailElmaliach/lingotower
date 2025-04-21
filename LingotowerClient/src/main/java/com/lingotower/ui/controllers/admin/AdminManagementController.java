package com.lingotower.ui.controllers.admin;

import java.util.List;

import org.slf4j.Logger;

import com.lingotower.model.Admin;
import com.lingotower.security.TokenStorage;
import com.lingotower.service.AdminService;
import com.lingotower.ui.components.ActionButtonCell;
import com.lingotower.ui.views.admin.AdminManagementView;
import com.lingotower.utils.LoggingUtility;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class AdminManagementController {
	private static final Logger logger = LoggingUtility.getLogger(AdminManagementController.class);

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
	private AdminManagementView parentView; // Reference to parent view

	public AdminManagementController() {
		// Initialize the service in the constructor
		this.adminService = new AdminService();
		logger.debug("AdminManagementController initialized");
	}

	/**
	 * Sets the parent view reference
	 * 
	 * @param view The parent view
	 */
	public void setParentView(AdminManagementView view) {
		this.parentView = view;
		logger.debug("Parent view set in AdminManagementController");
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

		// Initialize role ComboBox
		roleComboBox.setItems(FXCollections.observableArrayList("ADMIN"));
		roleComboBox.setValue("ADMIN");

		// Set table items
		adminTableView.setItems(adminsList);

		// Add search field listener for real-time filtering
		if (searchField != null) {
			searchField.textProperty().addListener((observable, oldValue, newValue) -> {
				handleSearchButton();
			});
		}

		logger.info("AdminManagementController UI initialized");
	}

	public void setAdmin(Admin admin) {
		this.currentAdmin = admin;
		logger.info("Current admin set: {}", (admin != null ? admin.getUsername() : "null"));
	}

	public void setReturnToDashboard(Runnable callback) {
		this.returnToDashboard = callback;
		logger.debug("Return to dashboard callback set");
	}

	public void loadAdmins() {
		showStatusMessage("Loading admins...", false);
		long startTime = System.currentTimeMillis();

		// Create a background thread to load admins
		Thread loadThread = new Thread(() -> {
			try {
				logger.info("Loading admins in background thread...");
				TokenStorage.logTokenStatus("Before loading admins");

				// Get all admins through the service
				List<Admin> admins = adminService.getAllAdmins();

				logger.info("Admins loaded: {}", (admins != null ? admins.size() : "null"));

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

				long duration = System.currentTimeMillis() - startTime;
				LoggingUtility.logPerformance(logger, "load_admins", duration,
						"count: " + (admins != null ? admins.size() : 0));

			} catch (Exception e) {
				logger.error("Error loading admins: {}", e.getMessage(), e);

				long duration = System.currentTimeMillis() - startTime;
				LoggingUtility.logPerformance(logger, "load_admins", duration, "error: " + e.getMessage());

				// Show error on JavaFX thread
				Platform.runLater(() -> {
					showStatusMessage("Error loading admins: " + e.getMessage(), true);
				});
			}
		});

		// Start the background thread
		loadThread.setDaemon(true);
		loadThread.setName("AdminLoader");
		loadThread.start();
	}

	@FXML
	private void handleBackButton() {
		if (returnToDashboard != null) {
			try {
				logger.info("Returning to dashboard");
				LoggingUtility.logAction(logger, "navigate",
						currentAdmin != null ? currentAdmin.getUsername() : "unknown", "dashboard", "initiated");
				returnToDashboard.run();
			} catch (Exception e) {
				logger.error("Error executing dashboard callback: {}", e.getMessage(), e);
				logger.error("Exception details:", e);
			}
		} else {
			logger.warn("Dashboard callback is not set");
		}
	}

	@FXML
	private void handleRefreshButton() {
		logger.info("Refreshing admin list");
		loadAdmins();
	}

	@FXML
	private void handleSearchButton() {
		if (searchField == null)
			return;

		String searchText = searchField.getText().trim().toLowerCase();
		logger.info("Searching for admin with text: {}", searchText);

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
		logger.info("Found {} matching admins", filteredList.size());
		showStatusMessage("Found " + filteredList.size() + " matching admins", false);
	}

	@FXML
	private void handleAddButton() {
		logger.info("Add admin button clicked");
		LoggingUtility.logAction(logger, "add_admin", currentAdmin != null ? currentAdmin.getUsername() : "unknown",
				"admin", "initiated");

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
		if (admin == null) {
			logger.warn("Attempted to edit null admin");
			return;
		}

		logger.info("Showing edit form for admin: {}", admin.getUsername());
		LoggingUtility.logAction(logger, "edit_admin", currentAdmin != null ? currentAdmin.getUsername() : "unknown",
				"admin:" + admin.getUsername(), "initiated");

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
		logger.info("Edit admin cancelled");
		LoggingUtility.logAction(logger, isAddMode ? "add_admin" : "edit_admin",
				currentAdmin != null ? currentAdmin.getUsername() : "unknown", "admin", "cancelled");

		if (editAdminForm != null) {
			editAdminForm.setVisible(false);
		}
		this.selectedAdmin = null;
		isAddMode = false;
	}

	@FXML
	private void handleSaveAdmin() {
		logger.info("Save admin button clicked (mode: {})", isAddMode ? "add" : "edit");

		// Get values from form
		String username = usernameField.getText().trim();
		String email = emailField.getText().trim();
		String password = passwordField.getText().trim();
		String role = roleComboBox.getValue();

		// Validate inputs
		if (username.isEmpty() || email.isEmpty()) {
			logger.warn("Validation failed: username or email empty");
			showStatusMessage("Username and email are required", true);
			return;
		}

		// In add mode, password is required
		if (isAddMode && password.isEmpty()) {
			logger.warn("Validation failed: password required for new admin");
			showStatusMessage("Password is required for new admins", true);
			return;
		}

		// Show loading status
		showStatusMessage(isAddMode ? "Creating admin..." : "Updating admin...", false);
		long startTime = System.currentTimeMillis();

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

					logger.info("Creating new admin with username: {}", username);
					LoggingUtility.logAction(logger, "add_admin",
							currentAdmin != null ? currentAdmin.getUsername() : "unknown", "admin:" + username,
							"processing");

					// Save using service
					Admin createdAdmin = adminService.createAdmin(newAdmin);

					long duration = System.currentTimeMillis() - startTime;

					// Update UI on JavaFX thread
					Platform.runLater(() -> {
						if (createdAdmin != null) {
							logger.info("Admin created successfully with ID: {}", createdAdmin.getId());
							LoggingUtility.logAction(logger, "add_admin",
									currentAdmin != null ? currentAdmin.getUsername() : "unknown", "admin:" + username,
									"success");
							LoggingUtility.logPerformance(logger, "create_admin", duration, "success");

							// Add to list
							adminsList.add(createdAdmin);

							// Hide form
							editAdminForm.setVisible(false);

							// Reset state
							selectedAdmin = null;
							isAddMode = false;

							// Show success message
							showStatusMessage("Admin created successfully", false);

							// Notify parent view
							if (parentView != null) {
								parentView.refresh();
							}
						} else {
							logger.warn("Failed to create admin: service returned null");
							LoggingUtility.logAction(logger, "add_admin",
									currentAdmin != null ? currentAdmin.getUsername() : "unknown", "admin:" + username,
									"failed");
							LoggingUtility.logPerformance(logger, "create_admin", duration, "failed");
							showStatusMessage("Failed to create admin", true);
						}
					});
				} else {
					// Update existing admin
					if (selectedAdmin == null) {
						logger.warn("Update failed: No admin selected");
						Platform.runLater(() -> {
							showStatusMessage("No admin selected for update", true);
						});
						return;
					}

					logger.info("Updating admin with ID: {}, username: {}", selectedAdmin.getId(), username);
					LoggingUtility.logAction(logger, "edit_admin",
							currentAdmin != null ? currentAdmin.getUsername() : "unknown",
							"admin:" + selectedAdmin.getUsername(), "processing");

					// Update fields
					selectedAdmin.setUsername(username);
					selectedAdmin.setEmail(email);
					if (!password.isEmpty()) {
						selectedAdmin.setPassword(password);
					}
					selectedAdmin.setRole(role);

					// Save using service
					boolean success = adminService.updateAdmin(selectedAdmin.getId(), selectedAdmin);
					long duration = System.currentTimeMillis() - startTime;

					// Update UI on JavaFX thread
					Platform.runLater(() -> {
						if (success) {
							logger.info("Admin updated successfully");
							LoggingUtility.logAction(logger, "edit_admin",
									currentAdmin != null ? currentAdmin.getUsername() : "unknown", "admin:" + username,
									"success");
							LoggingUtility.logPerformance(logger, "update_admin", duration, "success");

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

							// Notify parent view
							if (parentView != null) {
								parentView.refresh();
							}
						} else {
							logger.warn("Failed to update admin: service returned false");
							LoggingUtility.logAction(logger, "edit_admin",
									currentAdmin != null ? currentAdmin.getUsername() : "unknown", "admin:" + username,
									"failed");
							LoggingUtility.logPerformance(logger, "update_admin", duration, "failed");
							showStatusMessage("Failed to update admin", true);
						}
					});
				}
			} catch (Exception e) {
				logger.error("Error saving admin: {}", e.getMessage(), e);
				LoggingUtility.logAction(logger, isAddMode ? "add_admin" : "edit_admin",
						currentAdmin != null ? currentAdmin.getUsername() : "unknown", "admin",
						"error: " + e.getMessage());

				long duration = System.currentTimeMillis() - startTime;
				LoggingUtility.logPerformance(logger, isAddMode ? "create_admin" : "update_admin", duration, "error");

				// Show error on JavaFX thread
				Platform.runLater(() -> {
					showStatusMessage("Error saving admin: " + e.getMessage(), true);
				});
			}
		});

		// Start the background thread
		saveThread.setDaemon(true);
		saveThread.setName("AdminSaver");
		saveThread.start();
	}

	/**
	 * Replace the showDeleteConfirmation method with this implementation that
	 * creates a completely new Dialog instead of using the FXML one
	 */
	private void showDeleteConfirmation(Admin admin) {
		if (admin == null) {
			logger.warn("Cannot show confirmation for null admin");
			return;
		}

		logger.info("Showing delete confirmation for admin: {} (ID: {})", admin.getUsername(), admin.getId());

		// Don't allow deleting yourself
		if (currentAdmin != null && admin.getId().equals(currentAdmin.getId())) {
			logger.warn("Attempted to delete own admin account");
			showStatusMessage("You cannot delete your own admin account", true);
			return;
		}

		// SET selectedAdmin before showing dialog
		this.selectedAdmin = admin;

		// Create a confirmation dialog
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Delete Confirmation");
		alert.setHeaderText("Are you sure you want to delete this admin?");
		alert.setContentText("Username: " + admin.getUsername() + "\nEmail: "
				+ (admin.getEmail() != null ? admin.getEmail() : "N/A"));

		// Add buttons
		ButtonType deleteButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
		ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(deleteButtonType, cancelButtonType);

		// Show the dialog
		alert.showAndWait().ifPresent(response -> {
			if (response == deleteButtonType) {
				logger.info("Delete confirmed through dialog");
				LoggingUtility.logAction(logger, "delete_admin",
						currentAdmin != null ? currentAdmin.getUsername() : "unknown", "admin:" + admin.getUsername(),
						"confirmed");

				// Delete the admin directly
				handleConfirmDelete();
			} else {
				logger.info("Delete cancelled through dialog");
				LoggingUtility.logAction(logger, "delete_admin",
						currentAdmin != null ? currentAdmin.getUsername() : "unknown", "admin:" + admin.getUsername(),
						"cancelled");
				handleCancelDelete();
			}
		});
	}

	@FXML
	public void handleCancelDelete() {
		logger.info("Admin deletion cancelled");

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
			logger.warn("No admin selected for deletion");
			showStatusMessage("No admin selected for deletion", true);
			return;
		}

		// Ensure we have a valid ID
		if (selectedAdmin.getId() == null) {
			logger.warn("Invalid admin ID for deletion");
			showStatusMessage("Invalid admin ID", true);
			return;
		}

		logger.info("Deleting admin with ID: {}, username: {}", selectedAdmin.getId(), selectedAdmin.getUsername());
		LoggingUtility.logAction(logger, "delete_admin", currentAdmin != null ? currentAdmin.getUsername() : "unknown",
				"admin:" + selectedAdmin.getUsername(), "processing");

		// Show loading status
		showStatusMessage("Deleting admin...", false);
		long startTime = System.currentTimeMillis();

		// Delete in background thread
		Thread deleteThread = new Thread(() -> {
			try {
				// Call the AdminService to perform the delete operation
				boolean success = adminService.deleteAdmin(selectedAdmin.getId());
				long duration = System.currentTimeMillis() - startTime;

				logger.info("Delete operation result: {}", success);

				// Update UI on JavaFX thread
				Platform.runLater(() -> {
					if (success) {
						LoggingUtility.logAction(logger, "delete_admin",
								currentAdmin != null ? currentAdmin.getUsername() : "unknown",
								"admin:" + selectedAdmin.getUsername(), "success");
						LoggingUtility.logPerformance(logger, "delete_admin", duration, "success");

						// Hide the confirmation dialog
						if (confirmationDialog != null) {
							confirmationDialog.setVisible(false);
						}

						// Remove the admin from the list
						adminsList.remove(selectedAdmin);

						// Clear selected admin
						selectedAdmin = null;

						// Show success message
						showStatusMessage("Admin deleted successfully", false);

						// Optionally refresh the list
						loadAdmins();

						// Notify the parent view of the change
						if (parentView != null) {
							parentView.refresh();
						}
					} else {
						logger.warn("Failed to delete admin: service returned false");
						LoggingUtility.logAction(logger, "delete_admin",
								currentAdmin != null ? currentAdmin.getUsername() : "unknown",
								"admin:" + selectedAdmin.getUsername(), "failed");
						LoggingUtility.logPerformance(logger, "delete_admin", duration, "failed");
						showStatusMessage("Failed to delete admin. Please check permissions and try again.", true);
					}
				});
			} catch (Exception e) {
				logger.error("Error deleting admin: {}", e.getMessage(), e);
				logger.error("Exception details:", e);

				long duration = System.currentTimeMillis() - startTime;
				LoggingUtility.logAction(logger, "delete_admin",
						currentAdmin != null ? currentAdmin.getUsername() : "unknown",
						"admin:" + selectedAdmin.getUsername(), "error: " + e.getMessage());
				LoggingUtility.logPerformance(logger, "delete_admin", duration, "error: " + e.getMessage());

				// Show error on JavaFX thread
				Platform.runLater(() -> {
					showStatusMessage("Error deleting admin: " + e.getMessage(), true);
				});
			}
		});

		// Start the background thread
		deleteThread.setDaemon(true);
		deleteThread.setName("AdminDeleter");
		deleteThread.start();
	}

	private void showStatusMessage(String message, boolean isError) {
		if (statusLabel == null)
			return;

		logger.debug("Status message: {}", message);

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