package com.lingotower.ui;

import java.io.IOException;

import com.lingotower.model.Admin;
import com.lingotower.model.User;
import com.lingotower.service.AdminAuthService;
import com.lingotower.ui.controllers.DashboardViewController;
import com.lingotower.ui.controllers.MainApplicationController;
import com.lingotower.ui.views.DashboardView;
import com.lingotower.ui.views.LoginView;
import com.lingotower.ui.views.RegisterView;
import com.lingotower.ui.views.admin.AdminView;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class LingotowerApp extends Application {

	private Stage primaryStage;
	private LoginView loginView;
	private RegisterView registerView;
	private User currentUser;
	private Admin currentAdmin;
	private DashboardView dashboardView;
	private AdminAuthService adminAuthService = new AdminAuthService();

	@Override
	public void start(Stage primaryStage) {
		try {
			this.primaryStage = primaryStage;

			// Debug primary stage
			System.out.println("Primary Stage set in LingotowerApp: " + (primaryStage != null ? "Not null" : "NULL"));

			// Configure stage
			primaryStage.setTitle("LingoTower - Language Learning");

			// Start with login screen
			showLoginScreen();

			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showLoginScreen() {
		// Initialize login view with callbacks
		loginView = new LoginView(
				// On login success
				user -> {
					this.currentUser = user;
					System.out.println("User logged in: " + user.getUsername());

					// Determine if the user is an admin and show the appropriate screen
					if (user.getRole() != null && "ADMIN".equalsIgnoreCase(user.getRole())) {
						// Convert user to admin and show admin dashboard
						Admin admin = new Admin();
						admin.setId(user.getId());
						admin.setUsername(user.getUsername());
						admin.setEmail(user.getEmail());
						admin.setRole(user.getRole());

						this.currentAdmin = admin;
						showAdminDashboard();
					} else {
						// Show regular user dashboard
						showMainApplication();
					}
				},
				// On switch to register
				this::showRegisterScreen);

		// Create scene for login
		Scene loginScene = new Scene(loginView.createView(), 800, 600);

		// Try to load CSS
		try {
			loginScene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
		} catch (Exception e) {
			System.out.println("CSS not found, continuing without styles");
		}

		// Set scene to stage
		primaryStage.setScene(loginScene);
		primaryStage.setTitle("LingoTower - Login");
	}

	private void showAdminDashboard() {
		try {
			// Debug primary stage before creating admin view
			System.out.println("Primary Stage in showAdminDashboard: " + (primaryStage != null ? "Not null" : "NULL"));

			// Create AdminView using the new View class
			AdminView adminView = new AdminView().setAdmin(currentAdmin).setPrimaryStage(primaryStage) // Make sure this
																										// passes the
																										// primary stage
					.setOnLogout(() -> {
						currentAdmin = null;
						currentUser = null;
						// Make sure to use the admin auth service for logout
						adminAuthService.logout();
						showLoginScreen();
					});

			// Get root from view
			Parent root = adminView.createView();

			// Debug - verify controller has primaryStage
			if (adminView.getController() != null) {
				Stage controllerStage = adminView.getController().getPrimaryStage();
				System.out.println("Controller's primaryStage: " + (controllerStage != null ? "Not null" : "NULL"));
			}

			// Create scene
			Scene scene = new Scene(root, 1250, 680);

			// Add stylesheets
			try {
				scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
				scene.getStylesheets().add(getClass().getResource("/styles/admin-styles.css").toExternalForm());
				scene.getStylesheets().add(getClass().getResource("/styles/quiz-styles.css").toExternalForm());
			} catch (Exception e) {
				System.out.println("CSS not found, continuing without styles: " + e.getMessage());
			}

			// Set scene to stage
			primaryStage.setScene(scene);
			primaryStage.setTitle("LingoTower Admin - " + currentAdmin.getUsername());

		} catch (Exception e) {
			e.printStackTrace();
			showError("Error loading admin dashboard: " + e.getMessage());
		}
	}

	private void showRegisterScreen() {
		// Initialize register view if not already done
		if (registerView == null) {
			registerView = new RegisterView(
					// On register success
					user -> {
						this.currentUser = user;
						System.out.println("Registration successful! Please log in.: " + user.getUsername());
						showLoginScreen();
					},
					// On switch to login
					this::showLoginScreen);
		}

		// Create scene for register
		Scene registerScene = new Scene(registerView.createView(), 800, 800);

		try {
			registerScene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
		} catch (Exception e) {
			System.out.println("CSS not found, continuing without styles");
		}

		// Set scene to stage
		primaryStage.setScene(registerScene);
		primaryStage.setTitle("LingoTower - Register");
	}

	/**
	 * Shows the main application screen after successful login/registration
	 */
	private void showMainApplication() {
		try {
			// Create dashboard view
			dashboardView = new DashboardView();

			// Load main application layout
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainApplication.fxml"));
			Parent root = loader.load();

			// Get controller and configure it
			MainApplicationController controller = loader.getController();
			controller.setUser(currentUser);
			controller.setDashboardView(dashboardView);

			// Create the dashboard view first
			Parent dashboardRoot = dashboardView.createView();

			// Get the dashboard controller and set the main controller reference
			// using the type-safe getter on the dashboard view
			DashboardViewController dashboardController = dashboardView.getController();
			if (dashboardController != null) {
				dashboardController.setMainController(controller);
			}

			controller.setOnLogout(() -> {
				currentUser = null;
				showLoginScreen();
			});

			// Initialize the controller (this will show the dashboard)
			controller.initialize();

			// Set up the scene
			Scene scene = new Scene(root, 1160, 750);
			scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());

			// Set scene to stage
			primaryStage.setScene(scene);
			primaryStage.setTitle("LingoTower - Welcome " + currentUser.getUsername());

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error loading main application: " + e.getMessage());
		}
	}

	/**
	 * Shows an error dialog
	 * 
	 * @param message The error message
	 */
	private void showError(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("An error occurred");
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);
	}
}