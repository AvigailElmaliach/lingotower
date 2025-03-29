package com.lingotower.ui.controllers;

import com.lingotower.model.User;
import com.lingotower.service.UserService;

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
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class UserProfileController {

	@FXML
	private BorderPane view;

	@FXML
	private TabPane tabPane;

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
	private Pane chartContainer;

	@FXML
	private Label wordsLearnedLabel;

	@FXML
	private Label totalWordsLabel;

	@FXML
	private Label quizzesCompletedLabel;

	@FXML
	private Label quizSuccessRateLabel;

	@FXML
	private GridPane statsGrid;

	@FXML
	private VBox recommendationsBox;

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
	private User currentUser;

	// Mock data for testing
	private final Long MOCK_USER_ID = 1L;
	private PieChart progressChart;

	@FXML
	private void initialize() {
		this.userService = new UserService();

		// For demo purposes, we'll create a mock user
		createMockUser();

		// Setup UI components
		setupProfileTab();
		setupProgressTab();
		setupLearnedWordsTab();

		// Update welcome message
		welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");
	}

	private void createMockUser() {
		this.currentUser = new User();
		this.currentUser.setId(MOCK_USER_ID);
		this.currentUser.setUsername("learner1");
		this.currentUser.setEmail("learner1@example.com");
		this.currentUser.setLanguage("English");
	}

	private void setupProfileTab() {
		// Initialize form fields with user data
		usernameField.setText(currentUser.getUsername());
		emailField.setText(currentUser.getEmail());

		// Set up language combo box
		languageComboBox.setItems(FXCollections.observableArrayList("English", "Hebrew"));
		languageComboBox.setValue(currentUser.getLanguage());

		// Hide status message initially
		statusLabel.setVisible(false);
	}

	private void setupProgressTab() {
		// Create pie chart for progress visualization
		progressChart = new PieChart();

		// This would come from the service
		double learningProgress = 0.0;
		try {
			Double progress = userService.getUserLearningProgress(currentUser.getId());
			if (progress != null) {
				learningProgress = progress;
			}
		} catch (Exception e) {
			// Fall back to mock data
			learningProgress = 35.0; // 35% learned
		}

		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
				new PieChart.Data("Learned", learningProgress), new PieChart.Data("To Learn", 100 - learningProgress));
		progressChart.setData(pieChartData);
		progressChart.setTitle("Word Learning Progress");

		// Add chart to the container
		chartContainer.getChildren().add(progressChart);

		// Update stats
		wordsLearnedLabel.setText("42");
		totalWordsLabel.setText("120");
		quizzesCompletedLabel.setText("7");
		quizSuccessRateLabel.setText("85%");
	}

	private void setupLearnedWordsTab() {
		// Setup category filter
		categoryFilter
				.setItems(FXCollections.observableArrayList("All Categories", "Basics", "Food", "Travel", "Business"));
		categoryFilter.setValue("All Categories");

		// Populate words list
		ObservableList<String> words = FXCollections.observableArrayList("Hello - שלום", "Goodbye - להתראות",
				"Thank you - תודה", "Please - בבקשה", "Yes - כן", "No - לא", "Food - אוכל", "Water - מים");
		wordsList.setItems(words);
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
			// userService.updateUser(currentUser);
			showSuccessMessage("Profile updated successfully");
		} catch (Exception ex) {
			showErrorMessage("Failed to update profile: " + ex.getMessage());
		}
	}

	@FXML
	private void handleFilterButtonClick(ActionEvent event) {
		// Filter words by selected category
		// This would call the service to get filtered words
		System.out.println("Filtering by category: " + categoryFilter.getValue());
	}

	@FXML
	private void handlePracticeButtonClick(ActionEvent event) {
		// Start practice with selected words
		System.out.println("Practice selected words");
	}

	@FXML
	private void handleExportButtonClick(ActionEvent event) {
		// Export selected words to flashcards
		System.out.println("Export to flashcards");
	}

	private void showErrorMessage(String message) {
		statusLabel.setText(message);
		statusLabel.setStyle("-fx-text-fill: red;");
		statusLabel.setVisible(true);
	}

	private void showSuccessMessage(String message) {
		statusLabel.setText(message);
		statusLabel.setStyle("-fx-text-fill: green;");
		statusLabel.setVisible(true);
	}

	public void setUser(User user) {
		this.currentUser = user;
		if (user != null) {
			welcomeLabel.setText("Welcome, " + user.getUsername() + "!");
			usernameField.setText(user.getUsername());
			emailField.setText(user.getEmail());
			languageComboBox.setValue(user.getLanguage());
		}
	}

	public BorderPane getView() {
		return view;
	}

	public void refresh() {
		// Refresh user data from server
		try {
			User refreshedUser = userService.getUserById(currentUser.getId());
			if (refreshedUser != null) {
				this.currentUser = refreshedUser;
				// Update UI elements with new data
				setUser(refreshedUser);
			}
		} catch (Exception e) {
			System.err.println("Error refreshing user data: " + e.getMessage());
		}
	}
}