package com.lingotower.ui;

import java.io.IOException;

import com.lingotower.model.Admin;
import com.lingotower.model.Role;
import com.lingotower.ui.controllers.admin.AdminViewController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A simple test application that directly loads the Admin UI for testing
 * purposes
 */
public class AdminTestUI extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			// Load admin dashboard layout
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/AdminView.fxml"));
			Parent root = loader.load();

			// Get controller and configure it
			AdminViewController controller = loader.getController();

			// Create a mock admin
			Admin mockAdmin = createMockAdmin();
			controller.setAdmin(mockAdmin);

			// Store the primaryStage for later use in controllers
			controller.setPrimaryStage(primaryStage);

			// Set logout callback (optional, just prints to console)
			controller.setOnLogout(() -> {
				System.out.println("Admin logged out");
				primaryStage.close();
			});

			// Create scene
			Scene scene = new Scene(root, 800, 600);

			// Add stylesheets
			try {
				scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
				scene.getStylesheets().add(getClass().getResource("/styles/admin-styles.css").toExternalForm());
				// Fallback to the correct locations if the above fails
				if (scene.getStylesheets().isEmpty()) {
					scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
					scene.getStylesheets().add(getClass().getResource("/admin-styles.css").toExternalForm());
				}
			} catch (Exception e) {
				System.out.println("CSS not found, continuing without styles: " + e.getMessage());
			}

			// Set scene to stage
			primaryStage.setScene(scene);
			primaryStage.setTitle("LingoTower Admin - " + mockAdmin.getUsername());
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error loading admin dashboard: " + e.getMessage());
		}
	}

	/**
	 * Create a mock admin for testing purposes
	 */
	private Admin createMockAdmin() {
		Admin admin = new Admin();
		admin.setId(1L);
		admin.setUsername("admin");
		admin.setEmail("admin@lingotower.com");
		admin.setRole(Role.ADMIN.toString());
		return admin;
	}

	public static void main(String[] args) {
		launch(args);
	}
}