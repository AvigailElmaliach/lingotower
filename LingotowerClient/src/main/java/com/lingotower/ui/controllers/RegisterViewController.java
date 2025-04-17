package com.lingotower.ui.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lingotower.model.User;
import com.lingotower.service.UserAuthService;
import com.lingotower.utils.ValidationUtility;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class RegisterViewController implements Initializable {

	// Add logger as a static field
	private static final Logger logger = LoggerFactory.getLogger(RegisterViewController.class);

	@FXML
	private BorderPane view;

	@FXML
	private TextField usernameField;

	@FXML
	private TextField emailField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private PasswordField confirmPasswordField;

	@FXML
	private ComboBox<String> languageComboBox;

	@FXML
	private Button registerButton;

	@FXML
	private Hyperlink loginLink;

	@FXML
	private Label errorLabel;

	private Consumer<User> onRegisterSuccess;
	private Runnable onSwitchToLogin;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		logger.debug("Initializing RegisterViewController");

		// Initialize language combo box with only the supported languages
		languageComboBox.setItems(FXCollections.observableArrayList("English", "Hebrew"));
		languageComboBox.setValue("English");

		// Set up event handlers
		registerButton.setOnAction(this::handleRegister);
		loginLink.setOnAction(this::handleSwitchToLogin);

		logger.debug("RegisterViewController initialization complete");
	}

	public void setCallbacks(Consumer<User> onRegisterSuccess, Runnable onSwitchToLogin) {
		logger.debug("Setting callbacks on RegisterViewController");
		this.onRegisterSuccess = onRegisterSuccess;
		this.onSwitchToLogin = onSwitchToLogin;
	}

	@FXML
	private void handleRegister(ActionEvent event) {
		logger.info("Registration attempt initiated");

		String username = usernameField.getText().trim();
		String email = emailField.getText().trim();
		String password = passwordField.getText();
		String confirmPassword = confirmPasswordField.getText();
		String selectedLanguage = languageComboBox.getValue();

		// Get the language code for the language the user wants to learn
		String targetLanguage = mapUiLanguageToCode(selectedLanguage);

		// Set the source language as the opposite of the target language
		String sourceLanguage = targetLanguage.equals("en") ? "he" : "en";

		logger.debug("Registration details - Username: {}, Email: {}, Target Language: {}, Source Language: {}",
				username, email, targetLanguage, sourceLanguage);

		// Validate input fields using the ValidationUtility
		if (!isInputValid(username, email, password, confirmPassword, selectedLanguage)) {
			// Error message already displayed by isInputValid method
			return;
		}

		try {
			logger.info("Calling authentication service to register user: {}", username);
			UserAuthService authService = new UserAuthService();

			// Note: We're passing sourceLanguage as the parameter - this is what the user
			// already knows
			User user = authService.register(username, password, email, sourceLanguage);

			if (user != null) {
				// Registration successful
				logger.info("User registration successful for: {}", username);
				resetError();

				// Call the success callback
				if (onRegisterSuccess != null) {
					logger.debug("Executing onRegisterSuccess callback");
					onRegisterSuccess.accept(user);
				}
			} else {
				logger.error("Registration failed for user: {}. Username may already exist", username);
				showError("Registration failed. Username may already exist.");
			}
		} catch (Exception e) {
			logger.error("Exception during registration: {}", e.getMessage(), e);
			showError("Error during registration: " + e.getMessage());
		}
	}

	/**
	 * Validates all input fields using the ValidationUtility
	 * 
	 * @return true if all fields are valid, false otherwise
	 */
	private boolean isInputValid(String username, String email, String password, String confirmPassword,
			String language) {
		// Use the ValidationUtility to perform comprehensive validation
		String validationError = ValidationUtility.validateRegistration(username, email, password, confirmPassword,
				language);

		if (validationError != null) {
			logger.warn("Registration validation failed: {}", validationError);
			showError(validationError);
			return false;
		}

		return true;
	}

	/**
	 * Converts UI-friendly language name to language code
	 */
	private String mapUiLanguageToCode(String uiLanguage) {
		if (uiLanguage == null) {
			logger.warn("UI language is null, defaulting to 'en'");
			return "en";
		}

		return switch (uiLanguage) {
		case "Hebrew" -> "he";
		default -> "en"; // Default to English
		};
	}

	private void handleSwitchToLogin(ActionEvent event) {
		logger.debug("Switch to login requested");
		if (onSwitchToLogin != null) {
			onSwitchToLogin.run();
		}
	}

	public void showError(String message) {
		logger.debug("Showing error message: {}", message);
		errorLabel.setText(message);
		errorLabel.setVisible(true);
	}

	public void resetError() {
		logger.trace("Resetting error messages");
		errorLabel.setVisible(false);
	}

	public BorderPane getView() {
		return view;
	}
}