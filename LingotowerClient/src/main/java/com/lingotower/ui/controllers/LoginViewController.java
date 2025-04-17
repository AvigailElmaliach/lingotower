package com.lingotower.ui.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lingotower.model.Admin;
import com.lingotower.model.Role;
import com.lingotower.model.User;
import com.lingotower.service.AdminAuthService;
import com.lingotower.service.UserAuthService;

import javafx.concurrent.Task;
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

	// Add logger as a static field
	private static final Logger logger = LoggerFactory.getLogger(LoginViewController.class);

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

	// Services for both user and admin authentication
	private UserAuthService userAuthService;
	private AdminAuthService adminAuthService;
	private Consumer<User> onLoginSuccess;
	private Runnable onSwitchToRegister;

	public LoginViewController() {
		// Initialize both services
		logger.debug("Initializing LoginViewController");
		this.userAuthService = new UserAuthService();
		this.adminAuthService = new AdminAuthService();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		logger.debug("Setting up event handlers in LoginViewController");
		// Set up event handlers
		loginButton.setOnAction(this::handleLogin);
		forgotPasswordLink.setOnAction(this::handleForgotPassword);
		registerLink.setOnAction(this::handleRegister);
	}

	public void setCallbacks(Consumer<User> onLoginSuccess, Runnable onSwitchToRegister) {
		logger.debug("Setting callbacks on LoginViewController");
		this.onLoginSuccess = onLoginSuccess;
		this.onSwitchToRegister = onSwitchToRegister;
	}

	@FXML
	private void handleLogin(ActionEvent event) {
		String username = usernameField.getText().trim();
		String password = passwordField.getText();

		logger.info("Login attempt initiated for username: {}", username);

		if (username.isEmpty() || password.isEmpty()) {
			logger.warn("Login validation failed: Empty credentials");
			showError("נא להזין שם משתמש וסיסמה / Please enter a username and password");
			return;
		}

		// Disable button and show loading indicator during login attempt
		loginButton.setDisable(true);
		resetError(); // Clear previous errors

		// Create a background task for authentication
		Task<User> loginTask = new Task<User>() {
			@Override
			protected User call() throws Exception {
				// This runs on a background thread
				logger.debug("Executing login task in background thread");

				// 1. Try Admin login first
				logger.debug("Attempting admin login for: {}", username);
				Admin admin = adminAuthService.login(username, password);
				if (admin != null) {
					// Admin found, create User representation with admin role
					logger.info("Admin login successful for: {}", username);
					User adminUserRepresentation = new User();
					adminUserRepresentation.setUsername(admin.getUsername());
					adminUserRepresentation.setRole(Role.ADMIN.toString());
					// Set other relevant fields if necessary
					return adminUserRepresentation;
				}

				// 2. If admin login failed, try regular user login
				logger.debug("Admin login failed, attempting regular user login for: {}", username);
				User user = userAuthService.login(username, password);
				if (user != null) {
					logger.info("User login successful for: {}", username);
				} else {
					logger.warn("User login failed for: {}", username);
				}
				return user; // Will be null if login failed
			}
		};

		// Handle task completion on the JavaFX Application Thread
		loginTask.setOnSucceeded(workerStateEvent -> {
			User loggedInUser = loginTask.getValue(); // Get result from task

			if (loggedInUser != null) {
				// Login successful (either User or Admin)
				logger.info("Login succeeded. User type: {}",
						loggedInUser.getRole() != null ? loggedInUser.getRole() : "Regular User");
				resetError(); // Ensure error is clear
				if (onLoginSuccess != null) {
					onLoginSuccess.accept(loggedInUser);
				}
			} else {
				// Login failed (invalid credentials)
				logger.warn("Login failed for username: {}", username);
				showError("שם משתמש או סיסמה שגויים / Invalid username or password");
			}

			// Re-enable button and hide indicator
			loginButton.setDisable(false);
		});

		loginTask.setOnFailed(workerStateEvent -> {
			Throwable exception = loginTask.getException(); // Get the exception
			logger.error("Login process error: {}", exception.getMessage(), exception);
			showError("שגיאה בהתחברות: " + exception.getMessage() + " / Login error: " + exception.getMessage());

			// Re-enable button and hide indicator
			loginButton.setDisable(false);
		});

		// Run the task on a separate thread
		new Thread(loginTask).start();
	}

	private void handleForgotPassword(ActionEvent event) {
		// Handle password recovery
		logger.info("Password recovery requested");
		System.out.println("Password recovery requested");
	}

	private void handleRegister(ActionEvent event) {
		logger.debug("Switch to register view requested");
		if (onSwitchToRegister != null) {
			onSwitchToRegister.run();
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
		errorLabel.setText(""); // Also clear text in case it was just hidden
	}

	public BorderPane getView() {
		return view;
	}
}