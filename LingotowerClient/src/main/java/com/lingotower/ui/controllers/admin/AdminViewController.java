package com.lingotower.ui.controllers.admin;

import java.util.List;

import org.slf4j.Logger;

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
import com.lingotower.utils.LoggingUtility;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AdminViewController {
	private static final Logger logger = LoggingUtility.getLogger(AdminViewController.class);
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

	private static final String LOADING_TEXT = "Loading...";
	private static final String ERROR_TEXT = "Error";
	private static final String UNKNOWN_TEXT = "unknown";
	private static final String EXCEPTION_DETAILS_TEXT = "Exception details:";
	private static final String ACTION_NAVIGATE_TEXT = "navigate";

	public AdminViewController() {
		userService = new UserService();
		categoryService = new CategoryService();
		wordService = new WordService();
		adminService = new AdminService();
		logger.debug("AdminViewController services initialized.");
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
			logger.info("Admin set in view controller: {}", admin.getUsername());
		} else {
			logger.warn("Admin set to null in view controller.");
			adminNameLabel.setText("Admin: N/A");
		}
		loadSystemStats();
	}

	public void setPrimaryStage(Stage stage) {
		logger.info("Setting primary stage in AdminViewController: {}", (stage != null ? "Not null" : "NULL"));
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
		logger.info("Logout requested by admin: {}", (currentAdmin != null ? currentAdmin.getUsername() : "N/A"));

		if (onLogout != null) {
			LoggingUtility.logAction(logger, "logout",
					(currentAdmin != null ? currentAdmin.getUsername() : UNKNOWN_TEXT), "system", "initiated");
			onLogout.run();
		} else {
			logger.warn("onLogout handler is null.");
		}
	}

	@FXML
	private void handleUserManagementClick() {
		try {
			logger.info("Manage Users button clicked");
			TokenStorage.logTokenStatus("Before loading user management");

			long startTime = System.currentTimeMillis();

			if (primaryStage == null) {
				logger.error("ERROR: Primary stage is null in handleUserManagementClick!");
				showError("Cannot open User Management: Primary stage is null");
				return;
			}

			UserManagementView userManagementView = new UserManagementView().setAdmin(currentAdmin)
					.setAdminService(adminService).setReturnToDashboard(() -> {
						if (primaryStage != null) {
							primaryStage.setScene(view.getScene());
							loadSystemStats();
						} else {
							logger.warn("Primary stage is null in return callback from User Management");
						}
					});

			Scene scene = new Scene(userManagementView.createView(), 1250, 680);
			applyStylesheets(scene);

			primaryStage.setScene(scene);
			primaryStage.setTitle("LingoTower Admin - User Management");
			userManagementView.loadUsers();

			long duration = System.currentTimeMillis() - startTime;
			LoggingUtility.logPerformance(logger, "load_user_management", duration, "success");
			LoggingUtility.logAction(logger, ACTION_NAVIGATE_TEXT,
					currentAdmin != null ? currentAdmin.getUsername() : UNKNOWN_TEXT, "user_management", "success");

		} catch (Exception e) {
			logger.error("Error loading user management view: {}", e.getMessage());
			logger.error(EXCEPTION_DETAILS_TEXT, e);
			LoggingUtility.logAction(logger, ACTION_NAVIGATE_TEXT,
					currentAdmin != null ? currentAdmin.getUsername() : UNKNOWN_TEXT, "user_management",
					ERROR_TEXT + ":" + e.getMessage());
			showError(String.format("Error loading user management: %s", e.getMessage()));
		}
	}

	@FXML
	private void handleContentManagementClick() {
		try {
			logger.info("Manage Content button clicked");
			long startTime = System.currentTimeMillis();

			if (primaryStage == null) {
				logger.error("ERROR: Primary stage is null in handleContentManagementClick!");
				showError("Cannot open Content Management: Primary stage is null");
				return;
			}

			ContentManagementView contentManagementView = new ContentManagementView().setAdmin(currentAdmin)
					.setReturnToDashboard(() -> {
						if (primaryStage != null) {
							primaryStage.setScene(view.getScene());
							loadSystemStats();
						} else {
							logger.warn("Primary stage is null in return callback from Content Management");
						}
					});

			Scene scene = new Scene(contentManagementView.createView(), 1250, 680);
			applyStylesheets(scene);

			primaryStage.setScene(scene);
			primaryStage.setTitle("LingoTower Admin - Content Management");

			long duration = System.currentTimeMillis() - startTime;
			LoggingUtility.logPerformance(logger, "load_content_management", duration, "success");
			LoggingUtility.logAction(logger, ACTION_NAVIGATE_TEXT,
					currentAdmin != null ? currentAdmin.getUsername() : UNKNOWN_TEXT, "content_management", "success");

		} catch (Exception e) {
			logger.error("Error loading content management view: {}", e.getMessage());
			logger.error(UNKNOWN_TEXT, e);
			LoggingUtility.logAction(logger, ACTION_NAVIGATE_TEXT,
					currentAdmin != null ? currentAdmin.getUsername() : UNKNOWN_TEXT, "content_management",
					"error: " + e.getMessage());
			showError(String.format("Error loading content management: %s", e.getMessage()));
		}
	}

	@FXML
	private void handleAdminManagementClick() {
		try {
			logger.info("Manage Admins button clicked");
			TokenStorage.logTokenStatus("Before loading admin management");

			long startTime = System.currentTimeMillis();

			if (primaryStage == null) {
				logger.error("ERROR: Primary stage is null in handleAdminManagementClick!");
				showError("Cannot open Admin Management: Primary stage is null");
				return;
			}

			AdminManagementView adminManagementView = new AdminManagementView().setAdmin(currentAdmin)
					.setReturnToDashboard(() -> {
						if (primaryStage != null) {
							primaryStage.setScene(view.getScene());
							loadSystemStats();
						} else {
							logger.warn("Primary stage is null in return callback from Admin Management");
						}
					});

			Scene scene = new Scene(adminManagementView.createView(), 1250, 680);
			applyStylesheets(scene);

			primaryStage.setScene(scene);
			primaryStage.setTitle("LingoTower Admin - Admin Management");
			adminManagementView.refresh();

			long duration = System.currentTimeMillis() - startTime;
			LoggingUtility.logPerformance(logger, "load_admin_management", duration, "success");
			LoggingUtility.logAction(logger, ACTION_NAVIGATE_TEXT,
					currentAdmin != null ? currentAdmin.getUsername() : UNKNOWN_TEXT, "admin_management", "success");

		} catch (Exception e) {
			logger.error("Error loading admin management view: {}", e.getMessage());
			logger.error(UNKNOWN_TEXT, e);
			LoggingUtility.logAction(logger, ACTION_NAVIGATE_TEXT,
					currentAdmin != null ? currentAdmin.getUsername() : UNKNOWN_TEXT, "admin_management",
					"error: " + e.getMessage());
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
			logger.warn(CSS_NOT_FOUND_MESSAGE, e);
			try {
				// Trying root path directly
				scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
				scene.getStylesheets().add(getClass().getResource("/admin-styles.css").toExternalForm());
			} catch (NullPointerException ex) {
				logger.warn("CSS not found at root path either, continuing without styles.", ex);
			}
		} catch (Exception e) {
			logger.warn("Error loading CSS files: {}", e.getMessage());
			logger.debug(UNKNOWN_TEXT, e);
		}
	}

	/**
	 * Loads system statistics (users, categories, words) in a background thread.
	 */
	private void loadSystemStats() {
		// Check if there's already a loading thread running
		if (Thread.getAllStackTraces().keySet().stream()
				.anyMatch(t -> "SystemStatsLoader".equals(t.getName()) && t.isAlive())) {
			logger.debug("Stats loading already in progress, skipping duplicate request");
			return; // Skip creating another thread if one is already running
		}

		Thread statsThread = new Thread(() -> {
			try {
				logger.info("Background thread: Loading system stats...");
				long startTime = System.currentTimeMillis();

				// Set initial loading state in UI
				updateStatsLabels("Loading...", "Loading...", "Loading...");

				// Load statistics in parallel
				StatsData statsData = loadAllStats();

				// Update UI with loaded data
				Platform.runLater(() -> {
					totalUsersLabel.setText(String.valueOf(statsData.userCount));
					totalCategoriesLabel.setText(String.valueOf(statsData.categoryCount));
					totalWordsLabel.setText(String.valueOf(statsData.wordCount));
					logger.debug("System stats UI updated");
				});

				// Log performance
				long duration = System.currentTimeMillis() - startTime;
				LoggingUtility.logPerformance(logger, "load_system_stats", duration,
						String.format("users:%d,categories:%d,words:%d", statsData.userCount, statsData.categoryCount,
								statsData.wordCount));

			} catch (Exception e) {
				logger.error("Error during background system stats loading: {}", e.getMessage(), e);
				LoggingUtility.logEvent(logger, "stats_loading", "FAILURE", e.getMessage());

				Platform.runLater(() -> {
					showError(String.format("Error loading system statistics: %s", e.getMessage()));
					updateStatsLabels("Error", "Error", "Error");
				});
			}
		});

		statsThread.setDaemon(true);
		statsThread.setName("SystemStatsLoader");
		statsThread.start();
	}

	/**
	 * Helper class to store statistics data
	 */
	private static class StatsData {
		int userCount = 0;
		int categoryCount = 0;
		int wordCount = 0;
	}

	/**
	 * Updates all statistics labels with the given values
	 */
	private void updateStatsLabels(String userCount, String categoryCount, String wordCount) {
		if (Platform.isFxApplicationThread()) {
			totalUsersLabel.setText(userCount);
			totalCategoriesLabel.setText(categoryCount);
			totalWordsLabel.setText(wordCount);
		} else {
			Platform.runLater(() -> {
				totalUsersLabel.setText(userCount);
				totalCategoriesLabel.setText(categoryCount);
				totalWordsLabel.setText(wordCount);
			});
		}
	}

	/**
	 * Loads all statistics data
	 */
	private StatsData loadAllStats() {
		StatsData data = new StatsData();

		// Load users count
		data.userCount = loadUserCount();
		logger.info("User count calculated: {}", data.userCount);

		// Load categories count
		data.categoryCount = loadCategoryCount();
		logger.debug("Category count calculated: {}", data.categoryCount);

		// Load words count
		data.wordCount = loadWordCount();
		logger.debug("Word count calculated: {}", data.wordCount);

		// Log the summary
		logger.info("Stats collection complete - {} users, {} categories, {} words", data.userCount, data.categoryCount,
				data.wordCount);

		return data;
	}

	/**
	 * Loads the user count
	 */
	private int loadUserCount() {
		try {
			logger.debug("Background thread: Trying to load users with AdminService...");
			List<User> users = adminService.getAllUsers();

			if (users == null || users.isEmpty()) {
				logger.debug("Background thread: AdminService returned empty user list, trying UserService...");
				users = userService.getAllUsers();
			}

			return users != null ? users.size() : 0;
		} catch (Exception e) {
			logger.warn("Error loading users: {}", e.getMessage());
			return 0;
		}
	}

	/**
	 * Loads the category count
	 */
	private int loadCategoryCount() {
		try {
			List<Category> categories = categoryService.getAllCategories();
			return categories != null ? categories.size() : 0;
		} catch (Exception e) {
			logger.error("Error loading categories: {}", e.getMessage());
			return 0;
		}
	}

	/**
	 * Loads the word count
	 */
	private int loadWordCount() {
		try {
			List<Word> words = wordService.getAllWords();
			return words != null ? words.size() : 0;
		} catch (Exception e) {
			logger.error("Error loading words: {}", e.getMessage());
			return 0;
		}
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