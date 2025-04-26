package com.lingotower.ui.controllers;

import org.slf4j.Logger;

import com.lingotower.model.User;
import com.lingotower.ui.controllers.profile.LearnedWordsTabHandler;
import com.lingotower.ui.controllers.profile.ProfileTabHandler;
import com.lingotower.ui.controllers.profile.ProgressTabHandler;
import com.lingotower.ui.controllers.profile.UIStateManager;
import com.lingotower.utils.LoggingUtility;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the User Profile screen. Coordinates between different tab
 * handlers and manages shared UI state.
 */
public class UserProfileController {

	private static final Logger logger = LoggingUtility.getLogger(UserProfileController.class);

	// FXML Components - Main layout
	@FXML
	private BorderPane view;
	@FXML
	private Button profileTabButton;
	@FXML
	private Button progressTabButton;
	@FXML
	private Button wordsTabButton;
	@FXML
	private StackPane contentStack;
	@FXML
	private VBox profileContent;
	@FXML
	private VBox progressContent;
	@FXML
	private VBox wordsContent;
	@FXML
	private Label welcomeLabel;

	// FXML Components - Profile tab
	@FXML
	private TextField usernameField;
	@FXML
	private TextField emailField;
	@FXML
	private ComboBox<String> languageComboBox;
	@FXML
	private PasswordField passwordField;
	@FXML
	private PasswordField confirmPasswordField;
	@FXML
	private Button saveButton;
	@FXML
	private Label statusLabel;

	// FXML Components - Progress tab
	@FXML
	private PieChart progressChart;
	@FXML
	private Label wordsLearnedLabel;
	@FXML
	private Label totalWordsLabel;
	@FXML
	private Label precentLabel;
	@FXML
	private Label recommendationLabel1;
	@FXML
	private Label recommendationLabel2;
	@FXML
	private Label recommendationLabel3;

	// FXML Components - Learned words tab
	@FXML
	private ComboBox<String> categoryFilter;
	@FXML
	private Button filterButton;
	@FXML
	private ListView<String> wordsList;

	// Handler classes for each tab
	private ProfileTabHandler profileTabHandler;
	private ProgressTabHandler progressTabHandler;
	private LearnedWordsTabHandler learnedWordsTabHandler;

	// UI state manager
	private UIStateManager uiStateManager;

	// Current user
	private User currentUser;

	/**
	 * Initialize controller with FXML components.
	 */
	@FXML
	private void initialize() {
		logger.debug("Initializing UserProfileController");

		// Initialize UI state manager
		uiStateManager = new UIStateManager(profileTabButton, progressTabButton, wordsTabButton, profileContent,
				progressContent, wordsContent, statusLabel);

		// Initialize tab handlers
		profileTabHandler = new ProfileTabHandler(usernameField, emailField, languageComboBox, passwordField,
				confirmPasswordField, saveButton, uiStateManager);

		progressTabHandler = new ProgressTabHandler(progressChart, wordsLearnedLabel, totalWordsLabel, precentLabel,
				recommendationLabel1, recommendationLabel2, recommendationLabel3);

		learnedWordsTabHandler = new LearnedWordsTabHandler(categoryFilter, filterButton, wordsList, uiStateManager);

		// Default to profile tab being active
		uiStateManager.showProfileTab();

		// Setup responsive behavior
		setupResponsiveLayout();

		logger.debug("UserProfileController initialization complete");
	}

	/**
	 * Tab navigation methods
	 */
	@FXML
	private void switchToProfileTab() {
		logger.debug("Switching to profile tab");
		uiStateManager.showProfileTab();
	}

	@FXML
	private void switchToProgressTab() {
		logger.debug("Switching to progress tab");
		uiStateManager.showProgressTab();
	}

	@FXML
	private void switchToWordsTab() {
		logger.debug("Switching to words tab");
		uiStateManager.showWordsTab();
	}

	/**
	 * Sets the user for this controller and initializes data.
	 * 
	 * @param user The user object
	 */
	public void setUser(User user) {
		this.currentUser = user;
		if (user != null) {
			// Update welcome message
			welcomeLabel.setText("Welcome, " + user.getUsername() + "!");

			logger.debug("User set in UserProfileController: ID={}, Username={}, Email={}, Language={}", user.getId(),
					user.getUsername(), user.getEmail(), user.getLanguage());

			// Initialize handlers with user data
			profileTabHandler.setUser(user);
			progressTabHandler.setUser(user);
			learnedWordsTabHandler.setUser(user);

			// Load initial data
			loadUserData();
		} else {
			logger.warn("Null user set in UserProfileController");
		}
	}

	/**
	 * Load all user data across tabs
	 */
	private void loadUserData() {
		// First ensure we have a user
		if (currentUser == null) {
			logger.error("Cannot load user data: User is null");
			uiStateManager.showErrorMessage("User data unavailable. Please log in again.");
			return;
		}

		// Load data for each tab
		profileTabHandler.loadData(currentUser);
		progressTabHandler.loadData(currentUser);
		learnedWordsTabHandler.loadData(currentUser);

		logger.debug("User data loaded successfully");
	}

	/**
	 * Handle save button click from profile tab
	 */
	@FXML
	private void handleSaveButtonClick() {
		profileTabHandler.handleSave(currentUser, this::refresh);
	}

	/**
	 * Handle filter button click from learned words tab
	 */
	@FXML
	private void handleFilterButtonClick() {
		learnedWordsTabHandler.handleFilter();
	}

	/**
	 * Sets up responsive behavior for the profile view
	 */
	private void setupResponsiveLayout() {
		uiStateManager.setupResponsiveLayout(view);
	}

	/**
	 * Refresh all data
	 */
	public void refresh() {
		logger.debug("Refreshing user profile view");
		if (currentUser != null) {
			profileTabHandler.refreshData(currentUser);
			progressTabHandler.refreshData(currentUser);
			learnedWordsTabHandler.refreshData(currentUser);
			logger.debug("User data refreshed successfully");
		} else {
			logger.warn("Cannot refresh: User is null");
		}
	}

	/**
	 * Returns the view
	 */
	public BorderPane getView() {
		return view;
	}
}