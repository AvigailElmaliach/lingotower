package com.lingotower.ui.controllers;

import java.util.List;

import com.lingotower.model.Category;
import com.lingotower.model.User;
import com.lingotower.model.Word;
import com.lingotower.service.UserService;
import com.lingotower.service.WordService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class WordLearningController {

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
	private Button showTranslationButton;

	@FXML
	private Button nextWordButton;

	@FXML
	private Label messageLabel;

	@FXML
	private Button backButton;

	@FXML
	private Button markLearnedButton;

	private WordService wordService;
	private UserService userService;
	private Category currentCategory;
	private List<Word> words;
	private int currentWordIndex = 0;
	private Runnable onBackToDashboard;
	private User currentUser;

	@FXML
	private void initialize() {
		// Initialize services
		wordService = new WordService();
		userService = new UserService();

		// Disable next word button initially
		nextWordButton.setDisable(true);

		// Hide translation initially
		translationLabel.setVisible(false);

		// Disable mark learned button until translation is shown
		markLearnedButton.setDisable(true);
	}

	public void setUser(User user) {
		this.currentUser = user;
		System.out.println("User set in WordLearningController: " + (user != null ? user.getUsername() : "null"));
	}

	public void setCategory(Category category) {
		this.currentCategory = category;
		System.out.println("Category set in WordLearningController: "
				+ (category != null ? category.getName() + " (ID: " + category.getId() + ")" : "null"));

		// Update category name label
		categoryNameLabel.setText(category.getName());

		// Set RTL if needed
		if (containsHebrew(category.getName())) {
			categoryNameLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		}

		// Load words for this category
		loadWords();
	}

	private void loadWords() {
		try {
			// Try to fetch words with translations from server
			System.out.println("Fetching words for category ID: " + currentCategory.getId());

			// Use getWordsByCategory method instead of getWordsByCategoryWithTranslation
			// since the server endpoint returns the full Word objects with translation
			List<Word> fetchedWords = wordService.getWordsByCategory(currentCategory.getId());

			this.words = fetchedWords;

			if (words != null && !words.isEmpty()) {
				System.out.println("Loaded " + words.size() + " words successfully");

				// Debug the first word
				Word firstWord = words.get(0);
				System.out.println("First word: " + firstWord.getWord());
				System.out.println("Translation: " + firstWord.getTranslatedText());

				// Update progress
				updateProgress();

				// Show first word
				showCurrentWord();
			} else {
				// No words found
				System.out.println("No words found for this category");
				wordLabel.setText("No words found for this category");
				showTranslationButton.setDisable(true);
				nextWordButton.setDisable(true);
				markLearnedButton.setDisable(true);
			}
		} catch (Exception e) {
			System.err.println("Error loading words: " + e.getMessage());
			e.printStackTrace();

			// Show error message
			wordLabel.setText("Error loading words");
			messageLabel.setText("Could not load words: " + e.getMessage());
		}
	}

	private void showCurrentWord() {
		if (words == null || words.isEmpty() || currentWordIndex >= words.size()) {
			System.out.println("Cannot show current word: words list is empty or index out of bounds");
			return;
		}

		Word currentWord = words.get(currentWordIndex);
		System.out.println(
				"Showing word: " + currentWord.getWord() + " (Translation: " + currentWord.getTranslatedText() + ")");

		// Set word text
		wordLabel.setText(currentWord.getWord());

		// Set translation (hidden initially)
		translationLabel.setText(currentWord.getTranslatedText());
		translationLabel.setVisible(false);

		// Reset buttons
		showTranslationButton.setDisable(false);
		nextWordButton.setDisable(true);
		markLearnedButton.setDisable(true);

		// Set RTL if needed for word
		if (containsHebrew(currentWord.getWord())) {
			wordLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		} else {
			wordLabel.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
		}

		// Set RTL if needed for translation
		if (containsHebrew(currentWord.getTranslatedText())) {
			translationLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		} else {
			translationLabel.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
		}

		// Update message
		messageLabel.setText("Click 'Show Translation' to reveal the meaning");
	}

	@FXML
	public void handleShowTranslation(ActionEvent event) {
		// Show translation
		translationLabel.setVisible(true);

		// Enable next word button
		nextWordButton.setDisable(false);

		// Enable mark learned button
		markLearnedButton.setDisable(false);

		// Disable show translation button
		showTranslationButton.setDisable(true);

		// Update message
		messageLabel.setText("Click 'Next Word' to continue or 'Mark as Learned' to track your progress");
	}

	@FXML
	private void handleNextWord(ActionEvent event) {
		// Move to next word
		currentWordIndex++;

		// Check if we've reached the end
		if (currentWordIndex >= words.size()) {
			// Reset to beginning
			currentWordIndex = 0;
			messageLabel.setText("You've gone through all words. Starting again from the beginning.");
		}

		// Update progress
		updateProgress();

		// Show next word
		showCurrentWord();
	}

	@FXML
	private void handleMarkLearned(ActionEvent event) {
		if (words == null || words.isEmpty() || currentWordIndex >= words.size()) {
			System.err.println("Cannot mark word as learned: words list is empty or index out of bounds");
			messageLabel.setText("Error: No word to mark as learned");
			return;
		}

		Word currentWord = words.get(currentWordIndex);
		if (currentWord == null || currentWord.getId() == null) {
			System.err.println("Error: Word or Word ID is null. Current word: " + currentWord);
			messageLabel.setText("Error: Invalid word data");
			return;
		}

		System.out.println(
				"Attempting to mark word as learned: " + currentWord.getWord() + " (ID: " + currentWord.getId() + ")");

		try {
			boolean success = userService.addWordToLearned(currentWord.getId());
			if (success) {
				messageLabel.setText("Word marked as learned!");
			} else {
				messageLabel.setText("Error marking word as learned.");
			}
		} catch (Exception e) {
			System.err.println("Error marking word as learned: " + e.getMessage());
			e.printStackTrace();
			messageLabel.setText("Error marking word as learned: " + e.getMessage());
		}
	}

	@FXML
	private void handleBackToDashboard(ActionEvent event) {
		if (onBackToDashboard != null) {
			onBackToDashboard.run();
		}
	}

	private void updateProgress() {
		if (words == null || words.isEmpty()) {
			progressBar.setProgress(0);
			progressLabel.setText("0/0");
			return;
		}

		// Calculate progress
		double progress = (double) (currentWordIndex + 1) / words.size();
		progressBar.setProgress(progress);
		progressLabel.setText((currentWordIndex + 1) + "/" + words.size());
	}

	private boolean containsHebrew(String text) {
		if (text == null)
			return false;
		return text.codePoints().anyMatch(c -> Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HEBREW);
	}

	public void setOnBackToDashboard(Runnable callback) {
		this.onBackToDashboard = callback;
	}
}