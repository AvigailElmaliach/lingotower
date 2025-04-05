package com.lingotower.ui.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

// Import necessary classes
import com.lingotower.model.Admin;
import com.lingotower.model.Role;
import com.lingotower.model.User;
import com.lingotower.service.AdminAuthService;
import com.lingotower.service.UserAuthService;
// Removed unused import: import com.lingotower.service.UserService; // Assuming it's not used for login

import javafx.concurrent.Task; // Import Task
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
// Consider adding a ProgressIndicator or similar in FXML and connecting it
// import javafx.scene.control.ProgressIndicator;
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

	// Optional: Add a progress indicator to your FXML and connect it
	// @FXML
	// private ProgressIndicator loadingIndicator;

	// Services for both user and admin authentication
	private UserAuthService userAuthService;
	private AdminAuthService adminAuthService;
	private Consumer<User> onLoginSuccess;
	private Runnable onSwitchToRegister;

	public LoginViewController() {
		// Initialize both services
		this.userAuthService = new UserAuthService();
		this.adminAuthService = new AdminAuthService();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Set up event handlers
		loginButton.setOnAction(this::handleLogin);
		forgotPasswordLink.setOnAction(this::handleForgotPassword);
		registerLink.setOnAction(this::handleRegister);
		// Ensure indicator is initially hidden if you add one
		// if (loadingIndicator != null) {
		// loadingIndicator.setVisible(false);
		// }
	}

	public void setCallbacks(Consumer<User> onLoginSuccess, Runnable onSwitchToRegister) {
		this.onLoginSuccess = onLoginSuccess;
		this.onSwitchToRegister = onSwitchToRegister;
	}

	@FXML
	private void handleLogin(ActionEvent event) {
		String username = usernameField.getText().trim();
		String password = passwordField.getText();

		if (username.isEmpty() || password.isEmpty()) {
			showError("נא להזין שם משתמש וסיסמה");
			return;
		}

		// Disable button and show loading indicator during login attempt
		loginButton.setDisable(true);
		resetError(); // Clear previous errors
		// if (loadingIndicator != null) {
		// loadingIndicator.setVisible(true);
		// }

		// Create a background task for authentication
		Task<User> loginTask = new Task<User>() {
			@Override
			protected User call() throws Exception {
				// This runs on a background thread

				// 1. Try User login
				User user = userAuthService.login(username, password);
				if (user != null) {
					return user; // User found
				}

				// 2. Try Admin login if User login failed
				Admin admin = adminAuthService.login(username, password);
				if (admin != null) {
					// Admin found, create User representation
					User adminUserRepresentation = new User();
					adminUserRepresentation.setUsername(admin.getUsername());
					adminUserRepresentation.setRole(Role.ADMIN.toString());
					// Set other relevant fields if necessary
					return adminUserRepresentation;
				}

				// 3. Neither User nor Admin found
				return null;
			}
		};

		// Handle task completion on the JavaFX Application Thread
		loginTask.setOnSucceeded(workerStateEvent -> {
			User loggedInUser = loginTask.getValue(); // Get result from task

			if (loggedInUser != null) {
				// Login successful (either User or Admin)
				resetError(); // Ensure error is clear
				if (onLoginSuccess != null) {
					onLoginSuccess.accept(loggedInUser);
				}
			} else {
				// Login failed (invalid credentials)
				showError("שם משתמש או סיסמה שגויים");
			}

			// Re-enable button and hide indicator
			loginButton.setDisable(false);
			// if (loadingIndicator != null) {
			// loadingIndicator.setVisible(false);
			// }
		});

		loginTask.setOnFailed(workerStateEvent -> {
			Throwable exception = loginTask.getException(); // Get the exception
			exception.printStackTrace(); // Log the exception
			showError("שגיאה בהתחברות: " + exception.getMessage());

			// Re-enable button and hide indicator
			loginButton.setDisable(false);
			// if (loadingIndicator != null) {
			// loadingIndicator.setVisible(false);
			// }
		});

		// Optional: Handle cancellation if you add a cancel button
		// loginTask.setOnCancelled(workerStateEvent -> {
		// loginButton.setDisable(false);
		// if (loadingIndicator != null) {
		// loadingIndicator.setVisible(false);
		// }
		// // Maybe show a "Login cancelled" message
		// });

		// Run the task on a separate thread
		new Thread(loginTask).start();
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

	public void showError(String message) {
		errorLabel.setText(message);
		errorLabel.setVisible(true);
	}

	public void resetError() {
		errorLabel.setVisible(false);
		errorLabel.setText(""); // Also clear text in case it was just hidden
	}

	public BorderPane getView() {
		return view;
	}
}