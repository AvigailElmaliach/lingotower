package com.lingotower.ui.controllers.admin;

import java.io.IOException;

import com.lingotower.model.Admin;
import com.lingotower.security.TokenStorage;
import com.lingotower.service.AdminService;
import com.lingotower.service.CategoryService;
import com.lingotower.service.UserService;
import com.lingotower.service.WordService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AdminViewController {

	@FXML
	private BorderPane view;

	@FXML
	private Label adminNameLabel;

	@FXML
	private Button logoutButton;

	@FXML
	private Label totalUsersLabel;

	@FXML
	private Label totalCategoriesLabel;

	@FXML
	private Label totalWordsLabel;

	@FXML
	private Label errorMessageLabel;

	private Admin currentAdmin;
	private Runnable onLogout;
	private Stage primaryStage;

	private UserService userService;
	private CategoryService categoryService;
	private WordService wordService;
	private AdminService adminService;

	public AdminViewController() {
		// Initialize services
		userService = new UserService();
		categoryService = new CategoryService();
		wordService = new WordService();
		adminService = new AdminService();
	}

	@FXML
	private void initialize() {
		// This method is automatically called after the FXML is loaded
		loadSystemStats();
	}

	public void setAdmin(Admin admin) {
		this.currentAdmin = admin;

		// Update UI with admin info
		if (admin != null) {
			adminNameLabel.setText("Admin: " + admin.getUsername());
		}

		// Refresh stats
		loadSystemStats();
	}

	public void setPrimaryStage(Stage stage) {
		this.primaryStage = stage;
	}

	public void setOnLogout(Runnable onLogout) {
		this.onLogout = onLogout;
	}

	@FXML
	private void handleLogout() {
		if (onLogout != null) {
			onLogout.run();
		}
	}

	@FXML
	private void handleUserManagementClick() {
	    try {
	        System.out.println("Manage Users button clicked");
	        TokenStorage.logTokenStatus("Before loading user management");
	        
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/UserManagementView.fxml"));
	        Parent userManagementRoot = loader.load();

	        UserManagementController controller = loader.getController();
	        controller.setAdmin(currentAdmin);

	        // Important: Pass the token to the controller
	        controller.setAdminService(new AdminService());
	        
	        // Pass the stage to the controller for proper back navigation
	        controller.setReturnToDashboard(() -> {
	            if (primaryStage != null) {
	                primaryStage.setScene(view.getScene());
	            } else {
	                System.err.println("Primary stage is null");
	            }
	        });

	        // Load users list with token checking first
	        TokenStorage.logTokenStatus("Before controller.loadUsers()");
	        controller.loadUsers();
	        
	        // Show user management view
	        if (primaryStage != null) {
	            Scene scene = new Scene(userManagementRoot, 800, 600);

				// Add stylesheets
				try {
					scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
					scene.getStylesheets().add(getClass().getResource("/styles/admin-styles.css").toExternalForm());
				} catch (Exception e) {
					// Try with different paths
					try {
						scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
						scene.getStylesheets().add(getClass().getResource("/admin-styles.css").toExternalForm());
					} catch (Exception ex) {
						System.out.println("CSS not found, continuing without styles: " + ex.getMessage());
					}
				}

				primaryStage.setScene(scene);
				primaryStage.setTitle("LingoTower Admin - User Management");
			} else {
				System.err.println("Primary stage is null, cannot show UserManagementView");
			}

		} catch (IOException e) {
			System.err.println("Error loading user management view: " + e.getMessage());
			e.printStackTrace();
			showError("Error loading user management: " + e.getMessage());
		}
	}

	@FXML
	private void handleContentManagementClick() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/ContentManagementView.fxml"));
			Parent contentManagementRoot = loader.load();

			ContentManagementController controller = loader.getController();
			controller.setAdmin(currentAdmin);

			// Pass the stage to the controller for proper back navigation
			controller.setReturnToDashboard(() -> {
				if (primaryStage != null) {
					primaryStage.setScene(view.getScene());
				} else {
					System.err.println("Primary stage is null in ContentManagementController");
				}
			});

			// Load initial content
			controller.initialize();

			// Show content management view
			if (primaryStage != null) {
				Scene scene = new Scene(contentManagementRoot, 800, 600);

				// Add stylesheets
				try {
					scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
					scene.getStylesheets().add(getClass().getResource("/styles/admin-styles.css").toExternalForm());
				} catch (Exception e) {
					// Try with different paths
					try {
						scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
						scene.getStylesheets().add(getClass().getResource("/admin-styles.css").toExternalForm());
					} catch (Exception ex) {
						System.out.println("CSS not found, continuing without styles: " + ex.getMessage());
					}
				}

				primaryStage.setScene(scene);
				primaryStage.setTitle("LingoTower Admin - Content Management");
			} else {
				System.err.println("Primary stage is null, cannot show ContentManagementView");
			}

		} catch (IOException e) {
			System.err.println("Error loading content management view: " + e.getMessage());
			e.printStackTrace();
			showError("Error loading content management: " + e.getMessage());
		}
	}

	private void loadSystemStats() {
		try {
			// Get counts from services
			int userCount = userService.getAllUsers().size();
			int categoryCount = categoryService.getAllCategories().size();
			int wordCount = wordService.getAllWords().size();

			// Update UI labels
			totalUsersLabel.setText(String.valueOf(userCount));
			totalCategoriesLabel.setText(String.valueOf(categoryCount));
			totalWordsLabel.setText(String.valueOf(wordCount));

		} catch (Exception e) {
			System.err.println("Error loading system stats: " + e.getMessage());
			e.printStackTrace();
			showError("Error loading system statistics: " + e.getMessage());
		}
	}

	private void showError(String message) {
		errorMessageLabel.setText(message);
		errorMessageLabel.setVisible(true);
	}
}