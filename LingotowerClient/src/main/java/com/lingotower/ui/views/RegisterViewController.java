package com.lingotower.ui.views;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import com.lingotower.model.User;
import com.lingotower.service.UserService;

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

	private UserService userService;
	private Consumer<User> onRegisterSuccess;
	private Runnable onSwitchToLogin;

	public RegisterViewController() {
		this.userService = new UserService();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Initialize language combo box
		languageComboBox.setItems(FXCollections.observableArrayList("English", "Hebrew", "Spanish", "French"));
		languageComboBox.setValue("English");

		// Set up event handlers
		registerButton.setOnAction(this::handleRegister);
		loginLink.setOnAction(this::handleSwitchToLogin);
	}

	public void setCallbacks(Consumer<User> onRegisterSuccess, Runnable onSwitchToLogin) {
		this.onRegisterSuccess = onRegisterSuccess;
		this.onSwitchToLogin = onSwitchToLogin;
	}

	private void handleRegister(ActionEvent event) {
		String username = usernameField.getText().trim();
		String email = emailField.getText().trim();
		String password = passwordField.getText();
		String confirmPassword = confirmPasswordField.getText();
		String language = languageComboBox.getValue();

		// Validate fields
		if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
			showError("All fields are required");
			return;
		}

		if (!password.equals(confirmPassword)) {
			showError("Passwords do not match");
			return;
		}

		if (!isValidEmail(email)) {
			showError("Please enter a valid email address");
			return;
		}

		// In a real application, register with the server
		if (registerUser(username, email, password, language)) {
			// Create user object for the UI
			User user = new User();
			user.setUsername(username);
			user.setEmail(email);
			user.setLanguage(language);

			// Notify success handler
			if (onRegisterSuccess != null) {
				onRegisterSuccess.accept(user);
			}
		} else {
			showError("Registration failed. Username may already be taken.");
		}
	}

	private void handleSwitchToLogin(ActionEvent event) {
		System.out.println("Switch to login button clicked");
		if (onSwitchToLogin != null) {
			System.out.println("Callback is not null, running it now");
			onSwitchToLogin.run();
		} else {
			System.out.println("ERROR: onSwitchToLogin callback is null!");
		}
	}

	private boolean isValidEmail(String email) {
		// Simple email validation
		return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
	}

	private boolean registerUser(String username, String email, String password, String language) {
		// In a real application, send registration to server
		// For demo purposes, we'll simulate a successful registration
		try {
			User user = new User();
			user.setUsername(username);
			user.setEmail(email);
			user.setPassword(password);
			user.setLanguage(language);

			// In a real application, you would call:
			// User createdUser = userService.createUser(user);
			// return createdUser != null;

			return true; // Simulated success
		} catch (Exception e) {
			System.err.println("Registration error: " + e.getMessage());
			return false;
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