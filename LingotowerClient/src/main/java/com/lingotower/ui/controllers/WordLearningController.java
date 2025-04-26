package com.lingotower.ui.controllers;

import org.slf4j.Logger;

import com.lingotower.model.Category;
import com.lingotower.model.User;
import com.lingotower.model.Word;
import com.lingotower.ui.controllers.wordlearning.ExampleManager;
import com.lingotower.ui.controllers.wordlearning.NavigationManager;
import com.lingotower.ui.controllers.wordlearning.ProgressManager;
import com.lingotower.ui.controllers.wordlearning.WordDisplayManager;
import com.lingotower.utils.LoggingUtility;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class WordLearningController {

	private static final Logger logger = LoggingUtility.getLogger(WordLearningController.class);

	// FXML UI components
	@FXML
	private BorderPane view;
	@FXML
	private Label categoryNameLabel;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label progressLabel;
	@FXML
	private VBox wordCard;
	@FXML
	private Label wordLabel;
	@FXML
	private Label translationLabel;
	@FXML
	private Label examplesUsageLabel;
	@FXML
	private Button showTranslationButton;
	@FXML
	private Button showExamplesButton;
	@FXML
	private Button nextWordButton;
	@FXML
	private Label messageLabel;
	@FXML
	private Button backButton;
	@FXML
	private Button markLearnedButton;

	// Component managers
	private WordDisplayManager wordDisplayManager;
	private ExampleManager exampleManager;
	private ProgressManager progressManager;
	private NavigationManager navigationManager;

	// State
	private User currentUser;

	@FXML
	private void initialize() {
		logger.debug("Initializing WordLearningController");

		// Initialize managers
		wordDisplayManager = new WordDisplayManager(wordLabel, translationLabel, wordCard, showTranslationButton,
				markLearnedButton, nextWordButton, messageLabel);

		exampleManager = new ExampleManager(examplesUsageLabel, showExamplesButton);

		progressManager = new ProgressManager(progressBar, progressLabel);

		navigationManager = new NavigationManager(backButton, categoryNameLabel);
	}

	public void setUser(User user) {
		this.currentUser = user;
		logger.debug("User set in WordLearningController: {}", user != null ? user.getUsername() : "null");

		// Pass user to managers that need it
		wordDisplayManager.setUser(user);
	}

	public void setCategory(Category category) {
		if (category == null) {
			logger.warn("Attempt to set null category");
			return;
		}

		logger.debug("Setting category: {} (ID: {})", category.getName(), category.getId());

		// Update all component managers with the new category
		navigationManager.setCategory(category);
		wordDisplayManager.setCategory(category);
		progressManager.reset(); // Reset progress for the new category

		// Load words for this category
		loadWords();
	}

	private void loadWords() {
		// Delegate word loading to the word display manager
		wordDisplayManager.loadWords(
				// Callback to update progress when words are loaded
				(currentIndex, totalSize) -> progressManager.updateProgress(currentIndex, totalSize),
				// Callback when an error occurs
				(errorMessage) -> {
					logger.error("Error loading words: {}", errorMessage);
					messageLabel.setText(errorMessage);
					disableInteractiveButtons();
				});
	}

	private void disableInteractiveButtons() {
		showTranslationButton.setDisable(true);
		showExamplesButton.setDisable(true);
		nextWordButton.setDisable(true);
		markLearnedButton.setDisable(true);
	}

	@FXML
	public void handleShowTranslation(ActionEvent event) {
		wordDisplayManager.showTranslation();
		updateButtonState();
	}

	@FXML
	public void handleShowExamples(ActionEvent event) {
		Word currentWord = wordDisplayManager.getCurrentWord();
		if (currentWord != null) {
			exampleManager.fetchAndDisplayExamples(currentWord);
			updateButtonState();
		}
	}

	@FXML
	public void handleNextWord(ActionEvent event) {
		wordDisplayManager.showNextWord();
		progressManager.updateProgress(wordDisplayManager.getCurrentWordIndex(),
				wordDisplayManager.getTotalWordsCount());

		// Reset examples label state
		examplesUsageLabel.setText("");
		examplesUsageLabel.setVisible(false);

		// Re-enable the "Show Examples" button when moving to the next word
		showExamplesButton.setDisable(false);

		// Update other button states (Next, Mark Learned) based on *new* word's initial
		// state (nothing shown yet)
		updateButtonState();
	}

	@FXML
	public void handleMarkLearned(ActionEvent event) {
		wordDisplayManager.markCurrentWordAsLearned();
	}

	@FXML
	public void handleBackToDashboard(ActionEvent event) {
		navigationManager.navigateBackToDashboard();
	}

	private void updateButtonState() {
		boolean contentShown = translationLabel.isVisible() || examplesUsageLabel.isVisible();
		nextWordButton.setDisable(!contentShown);
		markLearnedButton.setDisable(!contentShown);
	}

	public void setOnBackToDashboard(Runnable callback) {
		navigationManager.setOnBackToDashboard(callback);
	}
}