package com.lingotower.ui.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import com.lingotower.model.User;
import com.lingotower.service.UserAuthService;

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
		// Initialize language combo box with only the supported languages
		languageComboBox.setItems(FXCollections.observableArrayList("English", "Hebrew"));
		languageComboBox.setValue("English");

		// Set up event handlers
		registerButton.setOnAction(this::handleRegister);
		loginLink.setOnAction(this::handleSwitchToLogin);
	}

	public void setCallbacks(Consumer<User> onRegisterSuccess, Runnable onSwitchToLogin) {
		this.onRegisterSuccess = onRegisterSuccess;
		this.onSwitchToLogin = onSwitchToLogin;
	}

	@FXML
	private void handleRegister(ActionEvent event) {
		String username = usernameField.getText().trim();
		String email = emailField.getText().trim();
		String password = passwordField.getText();
		String confirmPassword = confirmPasswordField.getText();

		// Get the language code for the language the user wants to learn
		String targetLanguage = mapUiLanguageToCode(languageComboBox.getValue());

		// Set the source language as the opposite of the target language
		// If user wants to learn Hebrew (target="he"), then source is English
		// (source="en")
		// If user wants to learn English (target="en"), then source is Hebrew
		// (source="he")
		String sourceLanguage = targetLanguage.equals("en") ? "he" : "en";

		// Validate fields
		if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
			showError("All fields are required");
			return;
		}

		if (!password.equals(confirmPassword)) {
			showError("Passwords do not match");
			return;
		}

		try {
			UserAuthService authService = new UserAuthService();

			// Note: We're passing sourceLanguage as the parameter - this is what the user
			// already knows
			User user = authService.register(username, password, email, sourceLanguage);

			if (user != null) {
				// Registration successful
				resetError();

				// Call the success callback
				if (onRegisterSuccess != null) {
					onRegisterSuccess.accept(user);
				}
			} else {
				showError("Registration failed. Username may already exist.");
			}
		} catch (Exception e) {
			showError("Error during registration: " + e.getMessage());
		}
	}

	/**
	 * Converts UI-friendly language name to language code
	 */
	private String mapUiLanguageToCode(String uiLanguage) {
		if (uiLanguage == null)
			return "en";

		return switch (uiLanguage) {
		case "Hebrew" -> "he";
		default -> "en"; // Default to English
		};
	}

	private void handleSwitchToLogin(ActionEvent event) {
		if (onSwitchToLogin != null) {
			onSwitchToLogin.run();
		}
	}

	public void showError(String message) {
		errorLabel.setText(message);
		errorLabel.setVisible(true);
	}

	public void resetError() {
		errorLabel.setVisible(false);
	}

	public BorderPane getView() {
		return view;
	}
}