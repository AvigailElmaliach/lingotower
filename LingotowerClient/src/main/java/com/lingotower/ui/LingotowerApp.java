package com.lingotower.ui;

import com.lingotower.ui.views.LoginView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LingotowerApp extends Application {

	private Stage primaryStage;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;

		// Set window title
		primaryStage.setTitle("LingoTower - Language Learning");

		// Show the login screen
		showLoginScreen();

		// Display the window
		primaryStage.show();
	}

	private void showLoginScreen() {
		// Create login view with callbacks
		LoginView loginView = new LoginView(
				// On login success
				user -> {
					System.out.println("User logged in: " + user.getUsername());
					// In the future, we'll show the main application here
				},
				// On switch to register
				() -> {
					System.out.println("Switch to register screen");
					// In the future, we'll show the register screen here
				});

		// Create and set scene
		Scene scene = new Scene(loginView.getView(), 800, 600);

		// Try to load CSS, but don't crash if it doesn't exist yet
		try {
			scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
		} catch (Exception e) {
			System.out.println("CSS not found, continuing without styles");
		}

		// Set scene to stage
		primaryStage.setScene(scene);
	}

	public static void main(String[] args) {
		launch(args);
	}
}