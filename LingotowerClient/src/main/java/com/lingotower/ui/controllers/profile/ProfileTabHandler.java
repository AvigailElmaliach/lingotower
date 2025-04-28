package com.lingotower.ui.controllers.profile;

import org.slf4j.Logger;

import com.lingotower.model.User;
import com.lingotower.service.UserService;
import com.lingotower.utils.LoggingUtility;
import com.lingotower.utils.ValidationUtility;

import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Handles the Profile Information tab functionality
 */
public class ProfileTabHandler {
	private static final Logger logger = LoggingUtility.getLogger(ProfileTabHandler.class);

	// UI Components
	private final TextField usernameField;
	private final TextField emailField;
	private final ComboBox<String> languageComboBox;
	private final PasswordField passwordField;
	private final PasswordField confirmPasswordField;
	private final Button saveButton;

	// UI state manager
	private final UIStateManager uiStateManager;

	// Service
	private final UserService userService;

	/**
	 * Constructor with UI components
	 */
	public ProfileTabHandler(TextField usernameField, TextField emailField, ComboBox<String> languageComboBox,
			PasswordField passwordField, PasswordField confirmPasswordField, Button saveButton,
			UIStateManager uiStateManager) {

		this.usernameField = usernameField;
		this.emailField = emailField;
		this.languageComboBox = languageComboBox;
		this.passwordField = passwordField;
		this.confirmPasswordField = confirmPasswordField;
		this.saveButton = saveButton;
		this.uiStateManager = uiStateManager;

		// Initialize service
		this.userService = new UserService();

		// Initialize language options
		initializeLanguageOptions();
	}

	/**
	 * Sets up the language options in the combo box
	 */
	private void initializeLanguageOptions() {
		// Set up language options - Hebrew-English only
		languageComboBox.setItems(FXCollections.observableArrayList("Hebrew", "English"));
	}

	/**
	 * Sets the user for this handler
	 */
	public void setUser(User user) {
		// User is handled at load time
	}

	/**
	 * Loads data for this tab
	 */
	public void loadData(User user) {
		// Populate fields from user object
		usernameField.setText(user.getUsername() != null ? user.getUsername() : "");
		emailField.setText(user.getEmail() != null ? user.getEmail() : "");

		// Handle language field which might be null
		String userLanguage = user.getLanguage();
		if (userLanguage != null && !userLanguage.isEmpty()) {
			// Convert language code to display value
			if ("en".equals(userLanguage)) {
				languageComboBox.setValue("Hebrew");
			} else if ("he".equals(userLanguage)) {
				languageComboBox.setValue("English");
			} else {
				languageComboBox.setValue(userLanguage);
			}
		} else {
			// Default to English if no language is set
			languageComboBox.setValue("English");
		}

		// Clear password fields
		passwordField.clear();
		confirmPasswordField.clear();

		logger.debug("Profile tab data loaded for user: {}", user.getUsername());
	}

	/**
	 * Refresh data for this tab
	 */
	public void refreshData(User user) {
		loadData(user);
	}

	/**
	 * Handles save button click
	 */
	public void handleSave(User user, Runnable onSaveSuccess) {
		logger.info("Save profile button clicked");

		// Get values from form
		String username = usernameField.getText().trim();
		String email = emailField.getText().trim();
		String language = languageComboBox.getValue();
		String password = passwordField.getText();
		String confirmPassword = confirmPasswordField.getText();

		if (user == null) {
			logger.error("Cannot save profile: User data is missing");
			uiStateManager.showErrorMessage("User data is missing. Please log in again.");
			return;
		}

		// Validate inputs
		if (!validateInputs(username, email, password, confirmPassword, language)) {
			return;
		}
		// Convert UI language value to language code if needed
		String languageCode = convertToLanguageCode(language);

		// Update user model with new values
		updateUserModel(user, username, email, languageCode, password);

		// Save changes to server
		saveChangesToServer(user, password, onSaveSuccess);
	}

	/**
	 * Validates form inputs
	 */
	private boolean validateInputs(String username, String email, String password, String confirmPassword,
			String language) {
		// For password validation, if fields are empty, use dummy valid values
		String passwordToValidate = password.isEmpty() ? "ValidDummy1" : password;
		String confirmPasswordToValidate = confirmPassword.isEmpty() ? "ValidDummy1" : confirmPassword;

		// Use ValidationUtility for all fields
		String validationError = ValidationUtility.validateRegistration(username, email, passwordToValidate,
				confirmPasswordToValidate, language);

		if (validationError != null) {
			logger.warn("Profile validation failed: {}", validationError);
			uiStateManager.showErrorMessage(validationError);
			return false;
		}

		return true;
	}

	/**
	 * Converts UI language value to language code
	 */
	private String convertToLanguageCode(String language) {
		return "English".equals(language) ? "he" : "Hebrew".equals(language) ? "en" : language;
	}

	/**
	 * Updates the user model with form values
	 */
	private void updateUserModel(User user, String username, String email, String languageCode, String password) {
		user.setUsername(username);
		user.setEmail(email);
		user.setLanguage(languageCode);
		if (password != null && !password.isEmpty()) {
			user.setPassword(password);
		}
	}

	/**
	 * Saves changes to the server and handles the result.
	 */
	private void saveChangesToServer(User user, String password, Runnable onSaveSuccess) {
		try {
			boolean updatedSuccessfully = false;

			if (user.getId() == null) {
				logger.error("Cannot update profile: User ID is missing");
				uiStateManager.showErrorMessage(
						"Cannot update profile: User ID is missing. Please log out and log in again.");
				return;
			}
			String oldUsername = user.getUsername();
			logger.info("Updating user profile for ID: {}", user.getId());
			updatedSuccessfully = userService.updateUser(oldUsername, user);
			if (updatedSuccessfully) {
				logger.info("Profile updated successfully");
				uiStateManager
						.showSuccessMessage("Profile updated successfully! Log out and log in again to see changes");
				passwordField.clear();
				confirmPasswordField.clear();
				if (onSaveSuccess != null) {
					onSaveSuccess.run();
				}
			} else {
				logger.error("Failed to update profile information");
				uiStateManager.showErrorMessage("Failed to update profile information");
			}

		} catch (Exception ex) {
			logger.error("Error updating profile: {}", ex.getMessage(), ex);
			uiStateManager.showErrorMessage("Error updating profile: " + ex.getMessage());
		}
	}

}