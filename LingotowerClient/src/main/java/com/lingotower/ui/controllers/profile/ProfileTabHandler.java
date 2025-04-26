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
		updateUserModel(user, username, email, languageCode);

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
	private void updateUserModel(User user, String username, String email, String languageCode) {
		user.setUsername(username);
		user.setEmail(email);
		user.setLanguage(languageCode);
	}

	/**
	 * Saves changes to the server
	 */
	private void saveChangesToServer(User user, String password, Runnable onSaveSuccess) {
		try {
			boolean profileUpdated = false;
			boolean passwordUpdated = true; // Default to true if no password update needed

			// Ensure user ID is present for profile update
			if (user.getId() == null) {
				logger.error("Cannot update profile: User ID is missing");
				uiStateManager.showErrorMessage(
						"Cannot update profile: User ID is missing. Please log out and log in again.");
				return;
			}

			// Update profile
			logger.info("Updating user profile for ID: {}", user.getId());
			profileUpdated = userService.updateUser(user);

			// Handle password update separately - works with just the JWT token
			if (!password.isEmpty()) {
				logger.info("Updating password using JWT token");
				passwordUpdated = userService.updateUserPassword(password);
			}

			// Show appropriate message based on results
			handleSaveResults(profileUpdated, passwordUpdated, password, onSaveSuccess);

		} catch (Exception ex) {
			logger.error("Error updating profile: {}", ex.getMessage(), ex);
			uiStateManager.showErrorMessage("Error updating profile: " + ex.getMessage());
		}
	}

	/**
	 * Handles the results of the save operation
	 */
	private void handleSaveResults(boolean profileUpdated, boolean passwordUpdated, String password,
			Runnable onSaveSuccess) {
		if (profileUpdated && passwordUpdated) {
			logger.info("Profile and password updated successfully");
			uiStateManager.showSuccessMessage("Profile updated successfully! Log out and log in again to see changes");
			passwordField.clear();
			confirmPasswordField.clear();

			// Call the success callback
			if (onSaveSuccess != null) {
				onSaveSuccess.run();
			}
		} else if (!profileUpdated && !passwordUpdated) {
			logger.error("Failed to update profile and password");
			uiStateManager.showErrorMessage("Failed to update profile and password");
		} else if (!profileUpdated) {
			logger.error("Failed to update profile information");
			uiStateManager.showErrorMessage("Failed to update profile information"
					+ (password.isEmpty() ? "" : ", but password was updated successfully"));
		} else {
			logger.error("Profile information updated successfully, but password update failed");
			uiStateManager.showErrorMessage("Profile information updated successfully, but password update failed");
		}
	}
}