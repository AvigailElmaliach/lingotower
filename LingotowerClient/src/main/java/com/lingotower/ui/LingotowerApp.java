package com.lingotower.ui;

import com.lingotower.model.User;
import com.lingotower.ui.views.LoginView;
import com.lingotower.ui.views.RegisterView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LingotowerApp extends Application {

	private Stage primaryStage;
	private LoginView loginView;
	private RegisterView registerView;

	// Current user
	private User currentUser;

	@Override
	public void start(Stage primaryStage) {
		try {
			this.primaryStage = primaryStage;

			// Initialize services
//			categoryService = new CategoryService();

			// Configure stage
			primaryStage.setTitle("LingoTower - Language Learning");

			// Start with login screen
			showLoginScreen();

			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	private void showLoginScreen() {
//		// Create login view with callbacks
//		if (loginView == null) {
//			loginView = new LoginView(
//					// On login success
//					user -> {
//						System.out.println("User logged in: " + user.getUsername());
//						// In the future, we'll show the main application here
//					},
//					// On switch to register
//					this::showRegisterScreen);
//		} else {
//			// Important: Refresh the view each time it's displayed
//			loginView.refresh();
//		}
//
//		// Create a NEW scene each time - don't reuse the old scene
//		Scene scene = new Scene(loginView.getView(), 800, 600);
//
//		// Try to load CSS
//		try {
//			scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
//		} catch (Exception e) {
//			System.out.println("CSS not found, continuing without styles");
//		}
//
//		// Set scene to stage
//		primaryStage.setScene(scene);
//		primaryStage.setTitle("LingoTower - Login");
//	}

	private void showLoginScreen() {
		// Initialize login and register views with callbacks
		loginView = new LoginView(
				// On login success
				user -> {
					this.currentUser = user;
					System.out.println("User logged in: " + user.getUsername());
					// In the future, we'll show the main application here

//                  showMainApplication();

				},
				// On switch to register
				this::showRegisterScreen);

		// Create scene for login
		Scene loginScene = new Scene(loginView.getView(), 800, 600);
//		loginScene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());

		// Try to load CSS
		try {
			loginScene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
		} catch (Exception e) {
			System.out.println("CSS not found, continuing without styles");
		}

		// Set scene to stage
		primaryStage.setScene(loginScene);
	}

	private void showRegisterScreen() {
		// Initialize register view if not already done
		if (registerView == null) {
			registerView = new RegisterView(
					// On register success
					user -> {
						this.currentUser = user;
						System.out.println("User logged in: " + user.getUsername());
						// In the future, we'll show the main application here

//                  showMainApplication();

					},
					// On switch to login
					this::showLoginScreen);
		}

		// Create scene for register
		Scene registerScene = new Scene(registerView.getView(), 800, 600);
		registerScene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());

		// Set scene to stage
		primaryStage.setScene(registerScene);
	}

	public static void main(String[] args) {
		launch(args);
	}
}