package com.lingotower.ui.controllers.admin;

import org.slf4j.Logger;

import com.lingotower.model.Admin;
import com.lingotower.ui.controllers.admin.admin.AdminFormValidator;
import com.lingotower.ui.controllers.admin.admin.AdminFormValidator.ValidationResult;
import com.lingotower.ui.controllers.admin.admin.AdminRepository;
import com.lingotower.ui.controllers.admin.admin.DialogHelper;
import com.lingotower.ui.views.admin.AdminManagementView;
import com.lingotower.utils.LoggingUtility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Admin Management screen. Uses extracted classes for CRUD operations,
 * validation, and dialogs.
 */
public class AdminManagementController {
	private static final Logger logger = LoggingUtility.getLogger(AdminManagementController.class);

	// FXML Components
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
	private TextField oldPasswordField;
	@FXML
	private TextField passwordField;
	@FXML
	private ComboBox<String> roleComboBox;
	@FXML
	private Label statusLabel;
	@FXML
	private TextField searchField;

	// Instance variables
	private Admin currentAdmin; // The logged-in admin
	private Admin selectedAdmin; // The admin being edited/deleted
	private boolean isAddMode = false;
	private Runnable returnToDashboard;
	private AdminManagementView parentView;

	// Repository for CRUD operations
	private AdminRepository adminRepository;

	/**
	 * Constructor - initializes the repository
	 */
	public AdminManagementController() {
		this.adminRepository = new AdminRepository();
		logger.debug("RefactoredAdminManagementController initialized");
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

	/**
	 * FXML initialize method - called after all @FXML fields are injected
	 */
	@FXML
	private void initialize() {
		// Initialize table columns
		setupTableColumns();

		// Hide status message and forms initially
		statusLabel.setVisible(false);
		editAdminForm.setVisible(false);

		// Initialize role ComboBox
		roleComboBox.setItems(FXCollections.observableArrayList("ADMIN"));
		roleComboBox.setValue("ADMIN");

		// Set table items from repository
		adminTableView.setItems(adminRepository.getAdminsList());

		// Add search field listener for real-time filtering
		setupSearchFieldListener();

		logger.info("RefactoredAdminManagementController UI initialized");
	}

	/**
	 * Sets up the table columns with cell factories
	 */
	private void setupTableColumns() {
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
		emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
		roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

		// Setup action buttons in a separate method for better readability
		setupActionColumn();
	}

	/**
	 * Sets up the actions column with edit/delete buttons
	 */
	private void setupActionColumn() {
		// Use ActionButtonCell from the original code
		// This creates edit and delete buttons in each row
		actionsColumn.setCellFactory(column -> new com.lingotower.ui.components.ActionButtonCell<>(
				// Edit button handler
				event -> {
					Admin admin = (Admin) event.getSource();
					showEditForm(admin);
				},
				// Delete button handler
				event -> {
					Admin admin = (Admin) event.getSource();
					handleDeleteButtonClick(admin);
				}));
	}

	/**
	 * Sets up the search field listener for filtering
	 */
	private void setupSearchFieldListener() {
		if (searchField != null) {
			searchField.textProperty().addListener((observable, oldValue, newValue) -> {
				handleSearchButton();
			});
		}
	}

	/**
	 * Sets the current admin
	 * 
	 * @param admin The current admin
	 */
	public void setAdmin(Admin admin) {
		this.currentAdmin = admin;
		logger.info("Current admin set: {}", (admin != null ? admin.getUsername() : "null"));
	}

	/**
	 * Sets the callback for returning to dashboard
	 * 
	 * @param callback The callback
	 */
	public void setReturnToDashboard(Runnable callback) {
		this.returnToDashboard = callback;
		logger.debug("Return to dashboard callback set");
	}

	/**
	 * Loads admins from the repository
	 */
	public void loadAdmins() {
		// Show loading status
		showStatusMessage("Loading admins...", false);

		// Use the repository to load admins
		adminRepository.loadAdmins(
				// Success callback
				() -> showStatusMessage("Loaded " + adminRepository.getAdminsList().size() + " admins", false),
				// Error callback
				this::showStatusMessage);
	}

	/**
	 * Handles the back button click
	 */
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

	/**
	 * Handles the refresh button click
	 */
	@FXML
	private void handleRefreshButton() {
		logger.info("Refreshing admin list");
		loadAdmins();
	}

	/**
	 * Handles the search button click
	 */
	@FXML
	private void handleSearchButton() {
		if (searchField == null)
			return;

		String searchText = searchField.getText().trim();
		logger.info("Searching for admin with text: {}", searchText);

		// If search is empty, show all admins
		if (searchText.isEmpty()) {
			adminTableView.setItems(adminRepository.getAdminsList());
			return;
		}

		// Use repository to filter admins
		ObservableList<Admin> filteredList = adminRepository.filterAdmins(searchText);

		// Update the table view with filtered results
		adminTableView.setItems(filteredList);

		logger.info("Found {} matching admins", filteredList.size());
		showStatusMessage("Found " + filteredList.size() + " matching admins", false);
	}

	/**
	 * Handles the add admin button click
	 */
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
		oldPasswordField.setText("");
		roleComboBox.setValue("ADMIN");
		selectedAdmin = null;

		// Show the form
		editAdminForm.setVisible(true);
	}

	/**
	 * Displays the edit form for an admin
	 * 
	 * @param admin The admin to edit
	 */
	private void showEditForm(Admin admin) {
		if (admin == null) {
			logger.warn("Attempted to edit null admin");
			return;
		}

		logger.info("Showing edit form for admin: {}", admin.getUsername());
		LoggingUtility.logAction(logger, "edit_admin", currentAdmin != null ? currentAdmin.getUsername() : "unknown",
				"admin:" + admin.getUsername(), "initiated");

		// Set edit mode
		isAddMode = false;

		// Store the selected admin
		this.selectedAdmin = admin;

		// Set form title
		formTitleLabel.setText("Edit Admin");

		// Fill the form fields
		usernameField.setText(admin.getUsername());
		emailField.setText(admin.getEmail());
		passwordField.setText(""); // Don't show existing password
		oldPasswordField.setText("");
		roleComboBox.setValue(admin.getRole());

		// Show the edit form
		editAdminForm.setVisible(true);
	}

	/**
	 * Handles the cancel edit button click
	 */
	@FXML
	private void handleCancelEdit() {
		logger.info("Edit admin cancelled");
		LoggingUtility.logAction(logger, isAddMode ? "add_admin" : "edit_admin",
				currentAdmin != null ? currentAdmin.getUsername() : "unknown", "admin", "cancelled");

		editAdminForm.setVisible(false);
		this.selectedAdmin = null;
		isAddMode = false;
	}

	/**
	 * Handles the save admin button click
	 */
	@FXML
	private void handleSaveAdmin() {
		logger.info("Save admin button clicked (mode: {})", isAddMode ? "add" : "edit");

		// Get values from form
		String username = usernameField.getText().trim();
		String email = emailField.getText().trim();
		String password = passwordField.getText().trim();
		String role = roleComboBox.getValue();

		// Validate inputs using the validator
		ValidationResult validationResult = AdminFormValidator.validateAdminForm(username, email, password, role,
				isAddMode);

		if (!validationResult.isValid()) {
			logger.warn("Admin form validation failed: {}", validationResult.getErrorMessage());
			showStatusMessage(validationResult.getErrorMessage(), true);
			return;
		}

		if (isAddMode) {
			// Create new admin
			Admin newAdmin = new Admin();
			newAdmin.setUsername(username);
			newAdmin.setEmail(email);
			newAdmin.setPassword(password);
			newAdmin.setRole(role);

			// Use repository to create admin
			adminRepository.createAdmin(newAdmin,
					// Success callback
					createdAdmin -> {
						editAdminForm.setVisible(false);
						selectedAdmin = null;
						isAddMode = false;
						showStatusMessage("Admin created successfully", false);
						if (parentView != null) {
							parentView.refresh();
						}
					},
					// Error callback
					this::showStatusMessage);
		} else {
			// Update existing admin
			if (selectedAdmin == null) {
				showStatusMessage("No admin selected for update", true);
				return;
			}

			// Update fields
			selectedAdmin.setUsername(username);
			selectedAdmin.setEmail(email);
			if (!password.isEmpty()) {
				selectedAdmin.setPassword(password);
			}
			selectedAdmin.setRole(role);

			// Use repository to update admin
			adminRepository.updateAdmin(selectedAdmin,
					// Success callback
					() -> {
						editAdminForm.setVisible(false);
						selectedAdmin = null;
						showStatusMessage("Admin updated successfully", false);
						if (parentView != null) {
							parentView.refresh();
						}
					},
					// Error callback
					this::showStatusMessage);
		}
	}

	/**
	 * Initiates the delete process for an admin
	 * 
	 * @param admin The admin to delete
	 */
	private void handleDeleteButtonClick(Admin admin) {
		// Validate admin deletion
		ValidationResult validationResult = AdminFormValidator.validateAdminDeletion(admin, currentAdmin);

		if (!validationResult.isValid()) {
			showStatusMessage(validationResult.getErrorMessage(), true);
			return;
		}

		// Store the selected admin
		this.selectedAdmin = admin;

		// Use DialogHelper to show confirmation dialog
		Scene scene = view.getScene();
		DialogHelper.showDeleteConfirmation(admin, scene != null ? scene.getWindow() : null, this::handleConfirmDelete);
	}

	/**
	 * Handles the confirmation of admin deletion
	 */
	private void handleConfirmDelete() {
		if (selectedAdmin == null) {
			logger.warn("No admin selected for deletion");
			showStatusMessage("No admin selected for deletion", true);
			return;
		}

		// Use repository to delete admin
		adminRepository.deleteAdmin(selectedAdmin,
				// Success callback
				() -> {
					selectedAdmin = null;
					showStatusMessage("Admin deleted successfully", false);
					loadAdmins();
					if (parentView != null) {
						parentView.refresh();
					}
				},
				// Error callback
				this::showStatusMessage);
	}

	/**
	 * Shows a status message
	 * 
	 * @param message The message to show
	 * @param isError Whether the message is an error
	 */
	private void showStatusMessage(String message, boolean isError) {
		DialogHelper.showStatusMessage(statusLabel, message, isError);

		// Auto-hide success messages
		if (!isError) {
			DialogHelper.autoHideStatusMessage(statusLabel, 5000);
		}
	}
}