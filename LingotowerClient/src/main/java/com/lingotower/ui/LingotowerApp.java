package com.lingotower.ui;

import java.io.IOException;

import com.lingotower.model.User;
import com.lingotower.ui.controllers.DashboardViewController;
import com.lingotower.ui.controllers.MainApplicationController;
import com.lingotower.ui.views.DashboardView;
import com.lingotower.ui.views.LoginView;
import com.lingotower.ui.views.RegisterView;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LingotowerApp extends Application {

	private Stage primaryStage;
	private LoginView loginView;
	private RegisterView registerView;
	private DashboardView dashboardView;
//	private LearnWordsView learnWordsView;
//	private QuizView quizView;
//	private UserProfileView userProfileView;

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

	private void showLoginScreen() {
		// Initialize login and register views with callbacks
		loginView = new LoginView(
				// On login success
				user -> {
					this.currentUser = user;
					System.out.println("User logged in: " + user.getUsername());

					showMainApplication();

				},
				// On switch to register
				this::showRegisterScreen);

		// Create scene for login
		Scene loginScene = new Scene(loginView.createView(), 800, 600);
//		loginScene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());

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

	private void showRegisterScreen() {
		// Initialize register view if not already done
		if (registerView == null) {
			registerView = new RegisterView(
					// On register success
					user -> {
						this.currentUser = user;
						System.out.println("User logged in: " + user.getUsername());

						showMainApplication();

					},
					// On switch to login
					this::showLoginScreen);
		}

		// Create scene for register
		Scene registerScene = new Scene(registerView.createView(), 800, 800);
		registerScene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());

		// Set scene to stage
		primaryStage.setScene(registerScene);
		primaryStage.setTitle("LingoTower - Regiser");

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

			// IMPORTANT: Get the dashboard controller and set the main controller reference
			FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/fxml/DashboardView.fxml"));
			Parent dashboardRoot = dashboardLoader.load();
			DashboardViewController dashboardController = dashboardLoader.getController();
			dashboardController.setMainController(controller);

			// Store the dashboardRoot in the dashboardView
			dashboardView.setRoot(dashboardRoot);
			dashboardView.setController(dashboardController);

			controller.setOnLogout(() -> {
				currentUser = null;
				showLoginScreen();
			});

			// Initialize the controller (this will show the dashboard)
			controller.initialize();

			// Set up the scene
			Scene scene = new Scene(root, 800, 600);
			scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());

			// Set scene to stage
			primaryStage.setScene(scene);
			primaryStage.setTitle("LingoTower - Welcome " + currentUser.getUsername());

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error loading main application: " + e.getMessage());
		}
	}

//	private void loadCategories() {
//		try {
//			// Fetch categories from server
//			java.util.List<Category> categories = categoryService.getAllCategories();
//			if (categories != null && !categories.isEmpty()) {
//				// Update dashboard with categories
//				dashboardView.updateCategories(categories);
//			} else {
//				// Create some mock categories for testing
//				java.util.List<Category> mockCategories = new java.util.ArrayList<>();
//				mockCategories.add(createMockCategory(1L, "Basics"));
//				mockCategories.add(createMockCategory(2L, "Food"));
//				mockCategories.add(createMockCategory(3L, "Travel"));
//				mockCategories.add(createMockCategory(4L, "Business"));
//				dashboardView.updateCategories(mockCategories);
//			}
//		} catch (Exception e) {
//			System.err.println("Error loading categories: " + e.getMessage());
//			e.printStackTrace();
//
//			// Create some mock categories for testing
//			java.util.List<Category> mockCategories = new java.util.ArrayList<>();
//			mockCategories.add(createMockCategory(1L, "Basics"));
//			mockCategories.add(createMockCategory(2L, "Food"));
//			mockCategories.add(createMockCategory(3L, "Travel"));
//			mockCategories.add(createMockCategory(4L, "Business"));
//			dashboardView.updateCategories(mockCategories);
//		}
//	}
//
//	private Category createMockCategory(Long id, String name) {
//		Category category = new Category();
//		category.setId(id);
//		category.setName(name);
//		return category;
//	}

	public static void main(String[] args) {
		launch(args);
	}
}