package com.lingotower.ui.controllers.admin;

import java.util.List;

import com.lingotower.model.Admin;
import com.lingotower.model.Category;
import com.lingotower.model.User;
import com.lingotower.model.Word;
import com.lingotower.security.TokenStorage;
import com.lingotower.service.AdminService;
import com.lingotower.service.CategoryService;
import com.lingotower.service.UserService;
import com.lingotower.service.WordService;
import com.lingotower.ui.views.admin.AdminManagementView;
import com.lingotower.ui.views.admin.ContentManagementView;
import com.lingotower.ui.views.admin.UserManagementView;

import javafx.fxml.FXML;
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
		// Hide error message initially
		errorMessageLabel.setVisible(false);

		// Load system stats in a background thread
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
		System.out.println("Setting primary stage in AdminViewController: " + (stage != null ? "Not null" : "NULL"));
		this.primaryStage = stage;
	}

	public Stage getPrimaryStage() {
		return this.primaryStage;
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

			// Debug check for null stage
			if (primaryStage == null) {
				System.err.println("ERROR: Primary stage is null in handleUserManagementClick!");
				showError("Cannot open User Management: Primary stage is null");
				return;
			}

			// Create the user management view using our new View class
			UserManagementView userManagementView = new UserManagementView().setAdmin(currentAdmin)
					.setAdminService(adminService).setReturnToDashboard(() -> {
						if (primaryStage != null) {
							primaryStage.setScene(view.getScene());
							// Refresh stats when returning
							loadSystemStats();
						} else {
							System.err.println("Primary stage is null in return callback");
						}
					});

			// Create scene using the view's root
			Scene scene = new Scene(userManagementView.createView(), 1250, 680);

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

			// Set the scene and title
			primaryStage.setScene(scene);
			primaryStage.setTitle("LingoTower Admin - User Management");

			// Load users
			userManagementView.loadUsers();

		} catch (Exception e) {
			System.err.println("Error loading user management view: " + e.getMessage());
			e.printStackTrace();
			showError("Error loading user management: " + e.getMessage());
		}
	}

	@FXML
	private void handleContentManagementClick() {
		try {
			// Debug check for null stage
			if (primaryStage == null) {
				System.err.println("ERROR: Primary stage is null in handleContentManagementClick!");
				showError("Cannot open Content Management: Primary stage is null");
				return;
			}

			// Create the content management view using the View class
			ContentManagementView contentManagementView = new ContentManagementView().setAdmin(currentAdmin)
					.setReturnToDashboard(() -> {
						if (primaryStage != null) {
							primaryStage.setScene(view.getScene());
							// Refresh stats when returning
							loadSystemStats();
						} else {
							System.err.println("Primary stage is null in ContentManagementController");
						}
					});

			// Create scene using the view's root
			Scene scene = new Scene(contentManagementView.createView(), 1250, 680);

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

			// Set the scene and title
			primaryStage.setScene(scene);
			primaryStage.setTitle("LingoTower Admin - Content Management");

		} catch (Exception e) {
			System.err.println("Error loading content management view: " + e.getMessage());
			e.printStackTrace();
			showError("Error loading content management: " + e.getMessage());
		}
	}

	@FXML
	private void handleAdminManagementClick() {
		try {
			System.out.println("Manage Admins button clicked");
			TokenStorage.logTokenStatus("Before loading admin management");

			// Debug check for null stage
			if (primaryStage == null) {
				System.err.println("ERROR: Primary stage is null in handleAdminManagementClick!");
				showError("Cannot open Admin Management: Primary stage is null");
				return;
			}

			// Create the admin management view using our new View class
			AdminManagementView adminManagementView = new AdminManagementView().setAdmin(currentAdmin)
					.setReturnToDashboard(() -> {
						if (primaryStage != null) {
							primaryStage.setScene(view.getScene());
							// Refresh stats when returning
							loadSystemStats();
						} else {
							System.err.println("Primary stage is null");
						}
					});

			// Create scene using the view's root
			Scene scene = new Scene(adminManagementView.createView(), 1250, 680);

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

			// Set the scene and title, then load admins
			primaryStage.setScene(scene);
			primaryStage.setTitle("LingoTower Admin - Admin Management");

			// Load admins
			adminManagementView.refresh();

		} catch (Exception e) {
			System.err.println("Error loading admin management view: " + e.getMessage());
			e.printStackTrace();
			showError("Error loading admin management: " + e.getMessage());
		}
	}

	private void loadSystemStats() {
		try {
			System.out.println("Loading system stats...");

			// Clear current values
			totalUsersLabel.setText("Loading...");
			totalCategoriesLabel.setText("Loading...");
			totalWordsLabel.setText("Loading...");

			// Get counts from services
			UserService userService = new UserService();
			CategoryService categoryService = new CategoryService();
			WordService wordService = new WordService();

			// Load users
			List<User> users = null;
			try {
				// First try with AdminService
				System.out.println("Trying to load users with AdminService...");
				if (adminService == null) {
					adminService = new AdminService();
				}
				users = adminService.getAllUsers();

				// If that fails, try with UserService
				if (users == null || users.isEmpty()) {
					System.out.println("AdminService returned no users, trying UserService...");
					users = userService.getAllUsers();
				}
			} catch (Exception e) {
				System.err.println("Error loading users with AdminService: " + e.getMessage());
				System.out.println("Falling back to UserService...");
				try {
					users = userService.getAllUsers();
				} catch (Exception ex) {
					System.err.println("Error loading users with UserService: " + ex.getMessage());
				}
			}

			int userCount = users != null ? users.size() : 0;
			System.out.println("User count: " + userCount);

			// Load categories
			List<Category> categories = null;
			try {
				categories = categoryService.getAllCategories();
			} catch (Exception e) {
				System.err.println("Error loading categories: " + e.getMessage());
			}
			int categoryCount = categories != null ? categories.size() : 0;

			// Load words
			List<Word> words = null;
			try {
				words = wordService.getAllWords();
			} catch (Exception e) {
				System.err.println("Error loading words: " + e.getMessage());
			}
			int wordCount = words != null ? words.size() : 0;

			// Update UI labels
			totalUsersLabel.setText(String.valueOf(userCount));
			totalCategoriesLabel.setText(String.valueOf(categoryCount));
			totalWordsLabel.setText(String.valueOf(wordCount));

			System.out.println("System stats loaded: " + userCount + " users, " + categoryCount + " categories, "
					+ wordCount + " words");

		} catch (Exception e) {
			System.err.println("Error loading system stats: " + e.getMessage());
			e.printStackTrace();
			showError("Error loading system statistics: " + e.getMessage());

			// Set default values
			totalUsersLabel.setText("Error");
			totalCategoriesLabel.setText("Error");
			totalWordsLabel.setText("Error");
		}
	}

	private void showError(String message) {
		errorMessageLabel.setText(message);
		errorMessageLabel.setVisible(true);
	}
}