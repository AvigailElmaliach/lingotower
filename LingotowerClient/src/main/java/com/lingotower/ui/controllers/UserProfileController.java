package com.lingotower.ui.controllers;

import java.util.List;

import org.slf4j.Logger;

import com.lingotower.dto.user.UserProgressDTO;
import com.lingotower.model.Category;
import com.lingotower.model.User;
import com.lingotower.model.Word;
import com.lingotower.service.CategoryService;
import com.lingotower.service.UserService;
import com.lingotower.service.WordService;
import com.lingotower.utils.LoggingUtility;
import com.lingotower.utils.ValidationUtility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

public class UserProfileController {

	private static final Logger logger = LoggingUtility.getLogger(UserProfileController.class);

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

	// Profile tab
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

	// Progress tab
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

	// Learned words tab
	@FXML
	private ComboBox<String> categoryFilter;
	@FXML
	private Button filterButton;
	@FXML
	private ListView<String> wordsList;

	private UserService userService;
	private CategoryService categoryService;
	private WordService wordService;
	private User currentUser;

	@FXML
	private void initialize() {
		logger.debug("Initializing UserProfileController");

		// Initialize services
		this.userService = new UserService();
		this.categoryService = new CategoryService();
		this.wordService = new WordService();

		// Set up language options - Hebrew-English only
		languageComboBox.setItems(FXCollections.observableArrayList("Hebrew", "English"));

		// Hide status message initially
		if (statusLabel != null) {
			statusLabel.setVisible(false);
		}

		// Set up empty progress chart
		ObservableList<PieChart.Data> emptyChartData = FXCollections
				.observableArrayList(new PieChart.Data("Learned", 0), new PieChart.Data("To Learn", 100));
		progressChart.setData(emptyChartData);
		progressChart.setTitle("");

		// Default to profile tab being active
		showProfileTab();

		// Set up responsive behavior
		setupResponsiveLayout();

		logger.debug("UserProfileController initialization complete");
	}

	@FXML
	private void switchToProfileTab(ActionEvent event) {
		logger.debug("Switching to profile tab");
		showProfileTab();
	}

	@FXML
	private void switchToProgressTab(ActionEvent event) {
		logger.debug("Switching to progress tab");
		showProgressTab();
	}

	@FXML
	private void switchToWordsTab(ActionEvent event) {
		logger.debug("Switching to words tab");
		showWordsTab();
	}

	private void showProfileTab() {
		// Update active tab styling
		profileTabButton.getStyleClass().add("custom-tab-active");
		progressTabButton.getStyleClass().remove("custom-tab-active");
		wordsTabButton.getStyleClass().remove("custom-tab-active");

		// Show/hide content
		profileContent.setVisible(true);
		progressContent.setVisible(false);
		wordsContent.setVisible(false);
	}

	private void showProgressTab() {
		// Update active tab styling
		profileTabButton.getStyleClass().remove("custom-tab-active");
		progressTabButton.getStyleClass().add("custom-tab-active");
		wordsTabButton.getStyleClass().remove("custom-tab-active");

		// Show/hide content
		profileContent.setVisible(false);
		progressContent.setVisible(true);
		wordsContent.setVisible(false);
	}

	private void showWordsTab() {
		// Update active tab styling
		profileTabButton.getStyleClass().remove("custom-tab-active");
		progressTabButton.getStyleClass().remove("custom-tab-active");
		wordsTabButton.getStyleClass().add("custom-tab-active");

		// Show/hide content
		profileContent.setVisible(false);
		progressContent.setVisible(false);
		wordsContent.setVisible(true);
	}

	public void setUser(User user) {
		this.currentUser = user;
		if (user != null) {
			// Update welcome message
			welcomeLabel.setText("Welcome, " + user.getUsername() + "!");

			// Debug logging for user information
			logger.debug("User set in UserProfileController: ID={}, Username={}, Email={}, Language={}", user.getId(),
					user.getUsername(), user.getEmail(), user.getLanguage());

			// Load user data
			loadUserData();
		} else {
			logger.warn("Null user set in UserProfileController");
		}
	}

	private void loadUserData() {
		// First ensure we have a user
		if (currentUser == null) {
			logger.error("Cannot load user data: User is null");
			showErrorMessage("User data unavailable. Please log in again.");
			return;
		}

		// If we don't have a user ID, try to fetch complete user details
		if (currentUser.getId() == null) {
			logger.info("User ID is missing, attempting to get current user details");
			User fullUser = userService.getCurrentUser();
			if (fullUser != null && fullUser.getId() != null) {
				// Update the current user reference with the full user data
				currentUser = fullUser;
				logger.info("Successfully retrieved user with ID: {}", currentUser.getId());
			} else {
				logger.warn("Failed to retrieve user ID");
				showErrorMessage("Could not retrieve your user ID. Some features may be limited.");
			}
		}

		// Profile tab - populate fields from user object
		usernameField.setText(currentUser.getUsername() != null ? currentUser.getUsername() : "");
		emailField.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");

		// Handle language field which might be null
		String userLanguage = currentUser.getLanguage();
		if (userLanguage != null && !userLanguage.isEmpty()) {
			// Convert language code to display value if needed
			if (userLanguage.equals("en")) {
				languageComboBox.setValue("Hebrew");
			} else if (userLanguage.equals("he")) {
				languageComboBox.setValue("English");
			} else {
				languageComboBox.setValue(userLanguage);
			}
		} else {
			// Default to English if no language is set
			languageComboBox.setValue("English");
		}

		// Clear password fields
		passwordField.clear();
		confirmPasswordField.clear();

		// Load progress data
		loadProgressData();

		// Load learned words
		loadLearnedWords();

		logger.debug("User data loaded successfully");
	}

	private void loadProgressData() {
		try {
			logger.debug("Loading user progress data");
			// Get user learning progress
			UserProgressDTO progressDTO = userService.getUserProgress();
			Double learningProgress = (progressDTO != null) ? progressDTO.getProgressPercentage() : 0.0;

			// Update progress chart
			ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
					new PieChart.Data("Learned", learningProgress),
					new PieChart.Data("To Learn", 100 - learningProgress));
			progressChart.setData(pieChartData);

			// Get statistics
			List<Word> learnedWords = userService.getLearnedWords();
			int learnedWordsCount = learnedWords != null ? learnedWords.size() : 0;
			wordsLearnedLabel.setText(String.valueOf(learnedWordsCount));

			List<Word> allWords = wordService.getAllWords();
			int totalWordsCount = allWords != null ? allWords.size() : 0;
			totalWordsLabel.setText(String.valueOf(totalWordsCount));

			// Update percentage label
			precentLabel.setText(String.format("%.2f%%", learningProgress));

			// Update recommendations based on user progress
			updateRecommendations(learnedWordsCount, totalWordsCount);

			logger.debug("Progress data loaded: learned={}, total={}, progress={}%", learnedWordsCount, totalWordsCount,
					learningProgress);

		} catch (Exception e) {
			logger.error("Error loading progress data: {}", e.getMessage(), e);
		}
	}

	private void updateRecommendations(int learnedCount, int totalCount) {
		try {
			logger.debug("Updating learning recommendations");
			List<Category> categories = categoryService.getAllCategories();

			if (categories != null && !categories.isEmpty()) {
				// Basic recommendations
				recommendationLabel1.setText("• Complete the \"" + categories.get(0).getName() + "\" category");

				if (categories.size() > 1) {
					recommendationLabel2
							.setText("• Review words in the \"" + categories.get(1).getName() + "\" category");
				}

				recommendationLabel3.setText("• Try available quizzes to test your knowledge");
				logger.debug("Recommendations updated based on {} categories", categories.size());
			} else {
				logger.warn("No categories available for recommendations");
			}
		} catch (Exception e) {
			logger.error("Error updating recommendations: {}", e.getMessage(), e);
		}
	}

	private void loadLearnedWords() {
		try {
			logger.debug("Loading categories for filter");
			// Get categories for filter
			List<Category> categories = categoryService.getAllCategories();
			ObservableList<String> categoryOptions = FXCollections.observableArrayList();
			categoryOptions.add("All Categories");

			if (categories != null) {
				for (Category category : categories) {
					categoryOptions.add(category.getName());
				}
				logger.debug("Loaded {} categories for filter", categories.size());
			}

			categoryFilter.setItems(categoryOptions);
			categoryFilter.setValue("All Categories");

			// Load user's learned words
			loadUserLearnedWords();
		} catch (Exception e) {
			logger.error("Error loading category filter: {}", e.getMessage(), e);
			categoryFilter.setItems(FXCollections.observableArrayList("All Categories"));
			categoryFilter.setValue("All Categories");
		}
	}

	private void loadUserLearnedWords() {
		try {
			logger.info("Loading learned words for user");
			List<Word> learnedWords = userService.getLearnedWords();
			ObservableList<String> wordItems = FXCollections.observableArrayList();

			if (learnedWords != null) {
				logger.debug("Received {} learned words", learnedWords.size());
				for (Word word : learnedWords) {
					// Format: English - Hebrew
					wordItems.add(word.getWord() + " - " + word.getTranslatedText());
				}
			} else {
				logger.warn("No learned words returned from server");
			}

			wordsList.setItems(wordItems);
		} catch (Exception e) {
			logger.error("Error loading learned words: {}", e.getMessage(), e);
			wordsList.setItems(FXCollections.observableArrayList());
		}
	}

	@FXML
	private void handleSaveButtonClick(ActionEvent event) {
		logger.info("Save profile button clicked");

		// Get values from form
		String username = usernameField.getText().trim();
		String email = emailField.getText().trim();
		String language = languageComboBox.getValue();
		String password = passwordField.getText();
		String confirmPassword = confirmPasswordField.getText();

		// Check if we have a valid user
		if (currentUser == null) {
			logger.error("Cannot save profile: User data is missing");
			showErrorMessage("User data is missing. Please log in again.");
			return;
		}

		// For password validation, if fields are empty, use dummy valid values
		String passwordToValidate = password.isEmpty() ? "ValidDummy1" : password;
		String confirmPasswordToValidate = confirmPassword.isEmpty() ? "ValidDummy1" : confirmPassword;

		// Use ValidationUtility for all fields
		String validationError = ValidationUtility.validateRegistration(username, email, passwordToValidate,
				confirmPasswordToValidate, language);

		if (validationError != null) {
			logger.warn("Validation failed: {}", validationError);
			showErrorMessage(validationError);
			return;
		}

		// Convert UI language value to language code if needed
		String languageCode = "English".equals(language) ? "he" : "Hebrew".equals(language) ? "en" : language;

		// Update user model with new values
		currentUser.setUsername(username);
		currentUser.setEmail(email);
		currentUser.setLanguage(languageCode);

		try {
			boolean profileUpdated = false;
			boolean passwordUpdated = true; // Default to true if no password update needed

			// Ensure user ID is present for profile update
			if (currentUser.getId() == null) {
				logger.error("Cannot update profile: User ID is missing");
				showErrorMessage("Cannot update profile: User ID is missing. Please log out and log in again.");
				return;
			}

			// Update profile
			logger.info("Updating user profile for ID: {}", currentUser.getId());
			profileUpdated = userService.updateUser(currentUser);

			// Handle password update separately - works with just the JWT token
			if (!password.isEmpty()) {
				logger.info("Updating password using JWT token");
				passwordUpdated = userService.updateUserPassword(password);
			}

			// Show appropriate message based on results
			if (profileUpdated && passwordUpdated) {
				logger.info("Profile and password updated successfully");
				showSuccessMessage("Profile updated successfully! Log out and log in again to see changes");
				passwordField.clear();
				confirmPasswordField.clear();
				// Refresh the dashboard view to reflect the new language
				refresh();
			} else if (!profileUpdated && !passwordUpdated) {
				logger.error("Failed to update profile and password");
				showErrorMessage("Failed to update profile and password");
			} else if (!profileUpdated) {
				logger.error("Failed to update profile information");
				showErrorMessage("Failed to update profile information"
						+ (password.isEmpty() ? "" : ", but password was updated successfully"));
			} else {
				logger.error("Profile information updated successfully, but password update failed");
				showErrorMessage("Profile information updated successfully, but password update failed");
			}
		} catch (Exception ex) {
			logger.error("Error updating profile: {}", ex.getMessage(), ex);
			showErrorMessage("Error updating profile: " + ex.getMessage());
		}
	}

	@FXML
	private void handleFilterButtonClick(ActionEvent event) {
		String selectedCategory = categoryFilter.getValue();
		logger.info("Filtering words by category: {}", selectedCategory);

		try {
			List<Word> filteredWords;

			if ("All Categories".equals(selectedCategory)) {
				// Get all learned words
				logger.debug("Getting all learned words");
				filteredWords = userService.getLearnedWords();
			} else {
				// Get learned words for the selected category
				List<Category> categories = categoryService.getAllCategories();

				// Find the category ID for the selected category name
				Long categoryId = null;
				for (Category category : categories) {
					if (category.getName().equals(selectedCategory)) {
						categoryId = category.getId();
						break;
					}
				}

				if (categoryId != null) {
					logger.debug("Getting learned words for category ID: {}", categoryId);
					// Use client-side filtering by category
					filteredWords = userService.getLearnedWordsByCategory(categoryId);
				} else {
					logger.warn("Category not found, showing all words");
					// Category not found, show all words
					filteredWords = userService.getLearnedWords();
				}
			}

			// Update the words list
			ObservableList<String> wordItems = FXCollections.observableArrayList();
			if (filteredWords != null && !filteredWords.isEmpty()) {
				logger.debug("Found {} words to display", filteredWords.size());
				for (Word word : filteredWords) {
					wordItems.add(word.getWord() + " - " + word.getTranslatedText());
				}
				wordsList.setItems(wordItems);
			} else {
				logger.info("No words found for the selected filter");
				wordItems.add("No words found for the selected category");
				wordsList.setItems(wordItems);
			}
		} catch (Exception e) {
			logger.error("Error filtering words: {}", e.getMessage(), e);

			// Show error message in the list
			ObservableList<String> errorItems = FXCollections
					.observableArrayList("Error loading words: " + e.getMessage());
			wordsList.setItems(errorItems);
		}
	}

	private void showErrorMessage(String message) {
		logger.debug("Showing error message: {}", message);
		statusLabel.setText(message);
		statusLabel.setStyle("-fx-text-fill: #e74c3c;");
		statusLabel.setVisible(true);
	}

	private void showSuccessMessage(String message) {
		logger.debug("Showing success message: {}", message);
		statusLabel.setText(message);
		statusLabel.setStyle("-fx-text-fill: #2ecc71;");
		statusLabel.setVisible(true);
	}

	public BorderPane getView() {
		return view;
	}

	public void refresh() {
		logger.debug("Refreshing user profile view");
		if (currentUser != null) {
			try {
				// Refresh user data from server
				User refreshedUser = userService.getUserById(currentUser.getId());
				if (refreshedUser != null) {
					this.currentUser = refreshedUser;
					loadUserData();
					logger.debug("User data refreshed successfully");
				} else {
					logger.warn("Failed to refresh user data: user not found");
				}
			} catch (Exception e) {
				logger.error("Error refreshing user data: {}", e.getMessage(), e);
			}
		} else {
			logger.warn("Cannot refresh: User is null");
		}
	}

	/**
	 * Sets up responsive behavior for the profile view
	 */
	private void setupResponsiveLayout() {
		// Listen for scene changes
		view.sceneProperty().addListener((observable, oldScene, newScene) -> {
			if (newScene != null) {
				// Listen for window changes
				newScene.windowProperty().addListener((prop, oldWindow, newWindow) -> {
					if (newWindow != null) {
						// Add listeners for width/height changes
						newWindow.widthProperty().addListener((obs, oldWidth, newWidth) -> {
							adjustLayoutForWidth(newWidth.doubleValue());
						});

						// Initial adjustment
						adjustLayoutForWidth(newWindow.getWidth());
					}
				});
			}
		});
		logger.debug("Responsive layout setup complete");
	}

	/**
	 * Adjusts the layout based on available width
	 * 
	 * @param width The current window width
	 */
	private void adjustLayoutForWidth(double width) {
		// For very narrow screens
		if (width < 500) {
			logger.trace("Adjusting layout for narrow width: {}", width);
			// Make profile content more compact
			if (profileContent != null) {
				profileContent.setSpacing(10); // Reduce spacing
			}

			// Adjust progress chart size
			if (progressChart != null) {
				progressChart.setPrefSize(width * 0.8, width * 0.6);
			}

			// Make word list take full width
			if (wordsList != null) {
				wordsList.setPrefWidth(width * 0.9);
			}
		} else {
			// For larger screens, reset to default
			if (profileContent != null) {
				profileContent.setSpacing(20); // Normal spacing
			}

			if (progressChart != null) {
				progressChart.setPrefSize(300, 250); // Default size
			}
		}
	}
}