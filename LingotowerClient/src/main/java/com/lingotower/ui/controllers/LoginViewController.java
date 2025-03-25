package com.lingotower.ui.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import com.lingotower.model.User;
import com.lingotower.service.AuthService;
import com.lingotower.service.UserService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class LoginViewController implements Initializable {

	@FXML
	private BorderPane view;

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private Button loginButton;

	@FXML
	private Hyperlink forgotPasswordLink;

	@FXML
	private Hyperlink registerLink;

	@FXML
	private Label errorLabel;

	private UserService userService;
	private Consumer<User> onLoginSuccess;
	private Runnable onSwitchToRegister;

	public LoginViewController() {
		this.userService = new UserService();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Set up event handlers
		loginButton.setOnAction(this::handleLogin);
		forgotPasswordLink.setOnAction(this::handleForgotPassword);
		registerLink.setOnAction(this::handleRegister);
	}

	public void setCallbacks(Consumer<User> onLoginSuccess, Runnable onSwitchToRegister) {
		this.onLoginSuccess = onLoginSuccess;
		this.onSwitchToRegister = onSwitchToRegister;
	}

//	private void handleLogin(ActionEvent event) {
//		String username = usernameField.getText().trim();
//		String password = passwordField.getText();
//
//		if (username.isEmpty() || password.isEmpty()) {
//			showError("Please enter both username and password");
//			return;
//		}
//
//		// In a real application, authenticate with server
//		// For demo purposes, we'll use a mock authentication
//		if (authenticateUser(username, password)) {
//			// Create a mock user for demonstration
//			User user = new User();
//			user.setId(1L); // Mock ID
//			user.setUsername(username);
//			user.setLanguage("English"); // Default language
//
//			// Notify success handler
//			if (onLoginSuccess != null) {
//				onLoginSuccess.accept(user);
//			}
//		} else {
//			showError("Invalid username or password");
//		}
//	}

	@FXML
	private void handleLogin(ActionEvent event) {
		String username = usernameField.getText().trim();
		String password = passwordField.getText();

		if (username.isEmpty() || password.isEmpty()) {
			showError("נא להזין שם משתמש וסיסמה");
			return;
		}

		try {
			AuthService authService = new AuthService();
			User user = authService.login(username, password);

			if (user != null) {
				// התחברות הצליחה
				resetError();

				// הפעלת callback להתחברות מוצלחת
				if (onLoginSuccess != null) {
					onLoginSuccess.accept(user);
				}
			} else {
				showError("שם משתמש או סיסמה שגויים");
			}
		} catch (Exception e) {
			showError("שגיאה בהתחברות: " + e.getMessage());
		}
	}

	private void handleForgotPassword(ActionEvent event) {
		// Handle password recovery
		System.out.println("Password recovery requested");
	}

	private void handleRegister(ActionEvent event) {
		if (onSwitchToRegister != null) {
			onSwitchToRegister.run();
		}
	}

	private boolean authenticateUser(String username, String password) {
		// In a real application, call the server to authenticate
		// For demo purposes, accept a test user
		return username.equals("demo") && password.equals("password");
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