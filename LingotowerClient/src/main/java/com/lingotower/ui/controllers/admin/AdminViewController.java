package com.lingotower.ui.controllers.admin;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AdminViewController {
	private static final Logger logger = Logger.getLogger(AdminViewController.class.getName());
	private static final String APPLICATION_CSS = "/styles/application.css";
	private static final String ADMIN_STYLES_CSS = "/styles/admin-styles.css";

	private static final String CSS_NOT_FOUND_MESSAGE = "Could not find CSS at /styles path, trying root path.";

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

	// Use final for services that are initialized once
	private final UserService userService;
	private final CategoryService categoryService;
	private final WordService wordService;
	private final AdminService adminService;

	public AdminViewController() {
		userService = new UserService();
		categoryService = new CategoryService();
		wordService = new WordService();
		adminService = new AdminService();
		logger.log(Level.CONFIG, "AdminViewController services initialized.");
	}

	@FXML
	private void initialize() {
		errorMessageLabel.setVisible(false);
		loadSystemStats();
	}

	public void setAdmin(Admin admin) {
		this.currentAdmin = admin;
		if (admin != null) {
			adminNameLabel.setText("Admin: " + admin.getUsername());
			logger.log(Level.INFO, "Admin set in view controller: {0}", admin.getUsername());
		} else {
			logger.log(Level.WARNING, "Admin set to null in view controller.");
			adminNameLabel.setText("Admin: N/A");
		}
		loadSystemStats();
	}

	public void setPrimaryStage(Stage stage) {
		logger.log(Level.INFO, "Setting primary stage in AdminViewController: {0}",
				(stage != null ? "Not null" : "NULL"));
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
		logger.log(Level.INFO, "Logout requested by admin: {0}",
				(currentAdmin != null ? currentAdmin.getUsername() : "N/A"));
		if (onLogout != null) {
			onLogout.run();
		} else {
			logger.log(Level.WARNING, "onLogout handler is null.");
		}
	}

	@FXML
	private void handleUserManagementClick() {
		try {
			logger.log(Level.INFO, "Manage Users button clicked");
			TokenStorage.logTokenStatus("Before loading user management");

			if (primaryStage == null) {
				logger.log(Level.SEVERE, "ERROR: Primary stage is null in handleUserManagementClick!");
				showError("Cannot open User Management: Primary stage is null");
				return;
			}

			UserManagementView userManagementView = new UserManagementView().setAdmin(currentAdmin)
					.setAdminService(adminService).setReturnToDashboard(() -> {
						if (primaryStage != null) {
							primaryStage.setScene(view.getScene());
							loadSystemStats();
						} else {
							logger.log(Level.WARNING, "Primary stage is null in return callback from User Management");
						}
					});

			Scene scene = new Scene(userManagementView.createView(), 1250, 680);
			applyStylesheets(scene);

			primaryStage.setScene(scene);
			primaryStage.setTitle("LingoTower Admin - User Management");
			userManagementView.loadUsers();

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error loading user management view: {0}", e.getMessage());
			logger.log(Level.SEVERE, "Exception details:", e);
			showError(String.format("Error loading user management: %s", e.getMessage()));
		}
	}

	@FXML
	private void handleContentManagementClick() {
		try {
			logger.log(Level.INFO, "Manage Content button clicked");
			if (primaryStage == null) {
				logger.log(Level.SEVERE, "ERROR: Primary stage is null in handleContentManagementClick!");
				showError("Cannot open Content Management: Primary stage is null");
				return;
			}

			ContentManagementView contentManagementView = new ContentManagementView().setAdmin(currentAdmin)
					.setReturnToDashboard(() -> {
						if (primaryStage != null) {
							primaryStage.setScene(view.getScene());
							loadSystemStats();
						} else {
							logger.log(Level.WARNING,
									"Primary stage is null in return callback from Content Management");
						}
					});

			Scene scene = new Scene(contentManagementView.createView(), 1250, 680);
			applyStylesheets(scene);

			primaryStage.setScene(scene);
			primaryStage.setTitle("LingoTower Admin - Content Management");

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error loading content management view: {0}", e.getMessage());
			logger.log(Level.SEVERE, "Exception details:", e);
			showError(String.format("Error loading content management: %s", e.getMessage()));
		}
	}

	@FXML
	private void handleAdminManagementClick() {
		try {
			logger.log(Level.INFO, "Manage Admins button clicked");
			TokenStorage.logTokenStatus("Before loading admin management");

			if (primaryStage == null) {
				logger.log(Level.SEVERE, "ERROR: Primary stage is null in handleAdminManagementClick!");
				showError("Cannot open Admin Management: Primary stage is null");
				return;
			}

			AdminManagementView adminManagementView = new AdminManagementView().setAdmin(currentAdmin)
					.setReturnToDashboard(() -> {
						if (primaryStage != null) {
							primaryStage.setScene(view.getScene());
							loadSystemStats();
						} else {
							logger.log(Level.WARNING, "Primary stage is null in return callback from Admin Management");
						}
					});

			Scene scene = new Scene(adminManagementView.createView(), 1250, 680);
			applyStylesheets(scene);

			primaryStage.setScene(scene);
			primaryStage.setTitle("LingoTower Admin - Admin Management");
			adminManagementView.refresh();

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error loading admin management view: {0}", e.getMessage());
			logger.log(Level.SEVERE, "Exception details:", e);
			showError(String.format("Error loading admin management: %s", e.getMessage()));
		}
	}

	/**
	 * Helper method to apply stylesheets to a scene, with fallback logic.
	 * 
	 * @param scene The scene to apply stylesheets to.
	 */
	private void applyStylesheets(Scene scene) {
		try {
			scene.getStylesheets().add(getClass().getResource(APPLICATION_CSS).toExternalForm());
			scene.getStylesheets().add(getClass().getResource(ADMIN_STYLES_CSS).toExternalForm());
		} catch (NullPointerException e) {
			logger.log(Level.WARNING, CSS_NOT_FOUND_MESSAGE, e);
			try {
				// Trying root path directly
				scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
				scene.getStylesheets().add(getClass().getResource("/admin-styles.css").toExternalForm());
			} catch (NullPointerException ex) {
				logger.log(Level.WARNING, "CSS not found at root path either, continuing without styles.", ex);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error loading CSS files: {0}", e.getMessage());
			logger.log(Level.WARNING, "Exception details:", e);
		}
	}

	/**
	 * Loads system statistics (users, categories, words) in a background thread.
	 */
	private void loadSystemStats() {
		// Check if there's already a loading thread running
		if (Thread.getAllStackTraces().keySet().stream()
				.anyMatch(t -> "SystemStatsLoader".equals(t.getName()) && t.isAlive())) {
			logger.log(Level.FINE, "Stats loading already in progress, skipping duplicate request");
			return; // Skip creating another thread if one is already running
		}

		Thread statsThread = new Thread(() -> {
			try {
				logger.log(Level.INFO, "Background thread: Loading system stats...");

				Platform.runLater(() -> {
					totalUsersLabel.setText("Loading...");
					totalCategoriesLabel.setText("Loading...");
					totalWordsLabel.setText("Loading...");
				});

				// Use the existing service instances for thread safety
				final UserService localUserService = userService;
				final CategoryService localCategoryService = categoryService;
				final WordService localWordService = wordService;
				final AdminService localAdminService = adminService;

				// --- Load users ---
				List<User> users = null;
				int userCount = 0;
				try {
					logger.log(Level.FINE, "Background thread: Trying to load users with AdminService...");
					users = localAdminService.getAllUsers();
					if (users == null) {
						logger.log(Level.FINE,
								"Background thread: AdminService returned null user list, trying UserService...");
						users = localUserService.getAllUsers();
					} else if (users.isEmpty()) {
						logger.log(Level.FINE,
								"Background thread: AdminService returned empty user list, trying UserService...");
						users = localUserService.getAllUsers();
					}
					userCount = (users != null) ? users.size() : 0;
				} catch (Exception e) {
					logger.log(Level.WARNING,
							"Background thread: Error loading users with AdminService: {0}. Falling back to UserService.",
							e.getMessage());
					try {
						logger.log(Level.FINE, "Background thread: Falling back to UserService...");
						users = localUserService.getAllUsers();
						userCount = (users != null) ? users.size() : 0;
					} catch (Exception ex) {
						logger.log(Level.SEVERE,
								"Background thread: Error loading users with UserService after fallback.", ex);
						userCount = 0;
					}
				}
				logger.log(Level.INFO, "Background thread: User count calculated: {0}", userCount);

				// --- Load categories ---
				List<Category> categories = null;
				int categoryCount = 0;
				try {
					categories = localCategoryService.getAllCategories();
					categoryCount = (categories != null) ? categories.size() : 0;
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Background thread: Error loading categories: {0}", e.getMessage());
					logger.log(Level.SEVERE, "Exception details:", e);
					categoryCount = 0;
				}
				// Only log once at INFO level, subsequent or detail logs at FINE
				logger.log(Level.FINE, "Background thread: Category count calculated: {0}", categoryCount);

				// --- Load words ---
				List<Word> words = null;
				int wordCount = 0;
				try {
					words = localWordService.getAllWords();
					wordCount = (words != null) ? words.size() : 0;
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Background thread: Error loading words: {0}", e.getMessage());
					logger.log(Level.SEVERE, "Exception details:", e);
					wordCount = 0;
				}
				// Only log once at INFO level, subsequent or detail logs at FINE
				logger.log(Level.FINE, "Background thread: Word count calculated: {0}", wordCount);

				// --- Update UI labels ---
				final int finalUserCount = userCount;
				final int finalCategoryCount = categoryCount;
				final int finalWordCount = wordCount;

				// Log the summary once at INFO level
				logger.log(Level.INFO,
						"Background thread: Stats collection complete - {0} users, {1} categories, {2} words",
						new Object[] { finalUserCount, finalCategoryCount, finalWordCount });

				Platform.runLater(() -> {
					totalUsersLabel.setText(String.valueOf(finalUserCount));
					totalCategoriesLabel.setText(String.valueOf(finalCategoryCount));
					totalWordsLabel.setText(String.valueOf(finalWordCount));
					// Use FINE level for UI update logs to reduce noise
					logger.log(Level.FINE, "System stats UI updated");
				});

			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error during background system stats loading: {0}", e.getMessage());
				logger.log(Level.SEVERE, "Exception details:", e);
				Platform.runLater(() -> {
					showError(String.format("Error loading system statistics: %s", e.getMessage()));
					totalUsersLabel.setText("Error");
					totalCategoriesLabel.setText("Error");
					totalWordsLabel.setText("Error");
				});
			}
		});
		statsThread.setDaemon(true);
		statsThread.setName("SystemStatsLoader");
		statsThread.start();
	}

	private void showError(String message) {
		if (Platform.isFxApplicationThread()) {
			errorMessageLabel.setText(message);
			errorMessageLabel.setVisible(true);
		} else {
			Platform.runLater(() -> {
				errorMessageLabel.setText(message);
				errorMessageLabel.setVisible(true);
			});
		}
	}
}