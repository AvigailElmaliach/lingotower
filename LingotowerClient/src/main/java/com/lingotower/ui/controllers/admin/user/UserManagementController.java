package com.lingotower.ui.controllers.admin.user;

import org.slf4j.Logger;

import com.lingotower.model.Admin;
import com.lingotower.model.User;
import com.lingotower.service.AdminService;
import com.lingotower.ui.components.ActionButtonCell;
import com.lingotower.ui.views.admin.UserManagementView;
import com.lingotower.utils.LoggingUtility;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class UserManagementController {
	private static final Logger logger = LoggingUtility.getLogger(UserManagementController.class);

	// FXML injected components
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

	// Delegate objects for specific functionality
	private UserLoaderDelegate userLoader;
	private UserEditorDelegate userEditor;
	private UserUIDelegate uiDelegate;
	private UserManagementView parentView;

	// Core state
	private Runnable returnToDashboard;
	private Admin currentAdmin;

	/**
	 * Constructor with default initialization
	 */
	public UserManagementController() {
		logger.debug("UserManagementController initialized");
	}

	/**
	 * JavaFX initialization method
	 */
	@FXML
	private void initialize() {
		// Initialize delegates
		this.userLoader = new UserLoaderDelegate(this);
		this.userEditor = new UserEditorDelegate(this);
		this.uiDelegate = new UserUIDelegate(this);

		// Initialize UI components
		initializeTableColumns();
		uiDelegate.setupUIComponents();

		logger.debug("UserManagementController UI initialization completed");
	}

	/**
	 * Initialize table columns with cell factories
	 */
	private void initializeTableColumns() {
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
					userEditor.showEditForm(user);
				},
				// Delete button handler
				event -> {
					User user = (User) event.getSource();
					userEditor.showDeleteConfirmation(user);
				}));
	}

	/**
	 * Sets the parent view reference
	 */
	public void setParentView(UserManagementView view) {
		this.parentView = view;
		logger.debug("Parent view set in UserManagementController");
	}

	/**
	 * Sets the admin service in the loader delegate
	 */
	public void setAdminService(AdminService adminService) {
		userLoader.setAdminService(adminService);
	}

	/**
	 * Sets the currently logged-in admin
	 */
	public void setAdmin(Admin admin) {
		this.currentAdmin = admin;
		logger.info("Current admin set: {}", admin != null ? admin.getUsername() : "null");
	}

	/**
	 * Sets the callback for returning to dashboard
	 */
	public void setReturnToDashboard(Runnable callback) {
		this.returnToDashboard = callback;
		logger.debug("Return to dashboard callback set");
	}

	/**
	 * Load users through the delegate
	 */
	public void loadUsers() {
		userLoader.loadUsers();
	}

	/**
	 * Back button handler
	 */
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

	/**
	 * Refresh button handler
	 */
	@FXML
	private void handleRefreshButton() {
		logger.info("Refresh button clicked");
		loadUsers();
	}

	/**
	 * Search button handler
	 */
	@FXML
	private void handleSearchButton() {
		userLoader.handleSearch(searchField.getText());
	}

	/**
	 * Cancel edit button handler
	 */
	@FXML
	private void handleCancelEdit() {
		userEditor.cancelEdit();
	}

	/**
	 * Save user button handler
	 */
	@FXML
	private void handleSaveUser() {
		userEditor.saveUser(usernameField.getText().trim(), emailField.getText().trim(), languageField.getText().trim(),
				passwordField.getText());
	}

	/**
	 * Cancel delete button handler
	 */
	@FXML
	public void handleCancelDelete() {
		userEditor.cancelDelete();
	}

	/**
	 * Confirm delete button handler
	 */
	@FXML
	public void handleConfirmDelete() {
		userEditor.confirmDelete();
	}

	// Getters for delegates to access controller components

	Admin getCurrentAdmin() {
		return currentAdmin;
	}

	UserManagementView getParentView() {
		return parentView;
	}

	TableView<User> getUserTableView() {
		return userTableView;
	}

	VBox getEditUserForm() {
		return editUserForm;
	}

	VBox getConfirmationDialog() {
		return confirmationDialog;
	}

	Label getStatusLabel() {
		return statusLabel;
	}

	TextField getUsernameField() {
		return usernameField;
	}

	TextField getEmailField() {
		return emailField;
	}

	TextField getLanguageField() {
		return languageField;
	}

	TextField getPasswordField() {
		return passwordField;
	}
}
