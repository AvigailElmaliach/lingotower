package com.lingotower.ui.controllers;

import java.util.List;

import com.lingotower.model.Category;
import com.lingotower.model.User;
import com.lingotower.model.Word;
import com.lingotower.service.CategoryService;
import com.lingotower.service.UserService;
import com.lingotower.service.WordService;

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
	private Label quizzesCompletedLabel;
	@FXML
	private Label quizSuccessRateLabel;
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
	@FXML
	private Button practiceButton;
	@FXML
	private Button exportButton;

	private UserService userService;
	private CategoryService categoryService;
	private WordService wordService;
	private User currentUser;

	@FXML
	private void initialize() {
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
		// Set up table for responsive behavior
//		optimizeTableForSmallScreens();

	}

	@FXML
	private void switchToProfileTab(ActionEvent event) {
		showProfileTab();
	}

	@FXML
	private void switchToProgressTab(ActionEvent event) {
		showProgressTab();
	}

	@FXML
	private void switchToWordsTab(ActionEvent event) {
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

			// Load user data
			loadUserData();
		}
	}

	private void loadUserData() {
		// Profile tab
		usernameField.setText(currentUser.getUsername());
		emailField.setText(currentUser.getEmail());
		languageComboBox.setValue(currentUser.getLanguage());

		// Clear password fields
		passwordField.clear();
		confirmPasswordField.clear();

		// Load progress data
		loadProgressData();

		// Load learned words
		loadLearnedWords();
	}

	private void loadProgressData() {
		try {
			// Get user learning progress
			Double progress = userService.getUserLearningProgress(currentUser.getId());
			double learningProgress = progress != null ? progress : 0.0;

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

			// In a real app, these would come from a quiz service
			quizzesCompletedLabel.setText("0");
			quizSuccessRateLabel.setText("0%");

			// Update recommendations based on user progress
			updateRecommendations(learnedWordsCount, totalWordsCount);

		} catch (Exception e) {
			System.err.println("Error loading progress data: " + e.getMessage());
		}
	}

	private void updateRecommendations(int learnedCount, int totalCount) {
		try {
			List<Category> categories = categoryService.getAllCategories();

			if (categories != null && !categories.isEmpty()) {
				// Basic recommendations
				recommendationLabel1.setText("• Complete the \"" + categories.get(0).getName() + "\" category");

				if (categories.size() > 1) {
					recommendationLabel2
							.setText("• Review words in the \"" + categories.get(1).getName() + "\" category");
				}

				recommendationLabel3.setText("• Try available quizzes to test your knowledge");
			}
		} catch (Exception e) {
			System.err.println("Error updating recommendations: " + e.getMessage());
		}
	}

	private void loadLearnedWords() {
		try {
			// Get categories for filter
			List<Category> categories = categoryService.getAllCategories();
			ObservableList<String> categoryOptions = FXCollections.observableArrayList();
			categoryOptions.add("All Categories");

			if (categories != null) {
				for (Category category : categories) {
					categoryOptions.add(category.getName());
				}
			}

			categoryFilter.setItems(categoryOptions);
			categoryFilter.setValue("All Categories");

			// Load user's learned words
			loadUserLearnedWords();
		} catch (Exception e) {
			System.err.println("Error loading category filter: " + e.getMessage());
			categoryFilter.setItems(FXCollections.observableArrayList("All Categories"));
			categoryFilter.setValue("All Categories");
		}
	}

	private void loadUserLearnedWords() {
		try {
			System.out.println("Loading learned words for user...");
			List<Word> learnedWords = userService.getLearnedWords();
			ObservableList<String> wordItems = FXCollections.observableArrayList();

			if (learnedWords != null) {
				System.out.println("Received " + learnedWords.size() + " learned words");
				for (Word word : learnedWords) {
					// Format: English - Hebrew
					wordItems.add(word.getWord() + " - " + word.getTranslatedText());
				}
			} else {
				System.out.println("No learned words returned from server");
			}

			wordsList.setItems(wordItems);
		} catch (Exception e) {
			System.err.println("Error loading learned words: " + e.getMessage());
			e.printStackTrace();
			wordsList.setItems(FXCollections.observableArrayList());
		}
	}

	@FXML
	private void handleSaveButtonClick(ActionEvent event) {
		// Validate and save user profile changes
		if (!passwordField.getText().isEmpty() && !passwordField.getText().equals(confirmPasswordField.getText())) {
			showErrorMessage("Passwords do not match");
			return;
		}

		currentUser.setUsername(usernameField.getText());
		currentUser.setEmail(emailField.getText());
		currentUser.setLanguage(languageComboBox.getValue());

		// If password was provided, update it
		if (!passwordField.getText().isEmpty()) {
			currentUser.setPassword(passwordField.getText());
		}

		// Call service to update user
		try {
			boolean updated = userService.updateUser(currentUser);
			if (updated) {
				showSuccessMessage("Profile updated successfully");
			} else {
				showErrorMessage("Failed to update profile");
			}
		} catch (Exception ex) {
			showErrorMessage("Error updating profile: " + ex.getMessage());
		}
	}

	@FXML
	private void handleFilterButtonClick(ActionEvent event) {
		String selectedCategory = categoryFilter.getValue();
		System.out.println("Filtering words by category: " + selectedCategory);

		try {
			List<Word> filteredWords;

			if ("All Categories".equals(selectedCategory)) {
				// Get all learned words
				System.out.println("Getting all learned words");
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
					System.out.println("Getting learned words for category ID: " + categoryId);
					// Use client-side filtering by category
					filteredWords = userService.getLearnedWordsByCategory(categoryId);
				} else {
					System.out.println("Category not found, showing all words");
					// Category not found, show all words
					filteredWords = userService.getLearnedWords();
				}
			}

			// Update the words list
			ObservableList<String> wordItems = FXCollections.observableArrayList();
			if (filteredWords != null && !filteredWords.isEmpty()) {
				System.out.println("Found " + filteredWords.size() + " words to display");
				for (Word word : filteredWords) {
					wordItems.add(word.getWord() + " - " + word.getTranslatedText());
				}
				wordsList.setItems(wordItems);
			} else {
				System.out.println("No words found for the selected filter");
				wordItems.add("No words found for the selected category");
				wordsList.setItems(wordItems);
			}
		} catch (Exception e) {
			System.err.println("Error filtering words: " + e.getMessage());
			e.printStackTrace();

			// Show error message in the list
			ObservableList<String> errorItems = FXCollections
					.observableArrayList("Error loading words: " + e.getMessage());
			wordsList.setItems(errorItems);
		}
	}

	@FXML
	private void handlePracticeButtonClick(ActionEvent event) {
		// This would navigate to a practice view for the selected words
		System.out.println("Practice selected words");
	}

	@FXML
	private void handleExportButtonClick(ActionEvent event) {
		// This would export the selected words as flashcards
		System.out.println("Export to flashcards");
	}

	private void showErrorMessage(String message) {
		statusLabel.setText(message);
		statusLabel.setStyle("-fx-text-fill: #e74c3c;");
		statusLabel.setVisible(true);
	}

	private void showSuccessMessage(String message) {
		statusLabel.setText(message);
		statusLabel.setStyle("-fx-text-fill: #2ecc71;");
		statusLabel.setVisible(true);
	}

	public BorderPane getView() {
		return view;
	}

	public void refresh() {
		if (currentUser != null) {
			try {
				// Refresh user data from server
				User refreshedUser = userService.getUserById(currentUser.getId());
				if (refreshedUser != null) {
					this.currentUser = refreshedUser;
					loadUserData();
				}
			} catch (Exception e) {
				System.err.println("Error refreshing user data: " + e.getMessage());
			}
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
	}

	/**
	 * Adjusts the layout based on available width
	 * 
	 * @param width The current window width
	 */
	private void adjustLayoutForWidth(double width) {
		// For very narrow screens
		if (width < 500) {
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

	/**
	 * Handles table display for very small windows
	 */
//	private void optimizeTableForSmallScreens() {
//		// Get the current scene's width property
//		view.widthProperty().addListener((obs, oldVal, newVal) -> {
//			double width = newVal.doubleValue();
//
//			// For very small screens
//			if (width < 500) {
//				// Set column priorities - keep ID and Username visible
//				idColumn.setMinWidth(40);
//				usernameColumn.setMinWidth(100);
//				actionsColumn.setMinWidth(120);
//
//				// Hide less important columns
//				emailColumn.setVisible(width > 400);
//				languageColumn.setVisible(width > 450);
//			} else {
//				// Show all columns for larger screens
//				emailColumn.setVisible(true);
//				languageColumn.setVisible(true);
//			}
//
//			// Force the table to refresh its layout
//			userTableView.refresh();
//		});
//	}

}
