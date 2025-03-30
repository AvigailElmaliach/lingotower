package com.lingotower.ui.controllers;

import java.util.ArrayList;
import java.util.List;

import com.lingotower.model.Category;
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

	public void setCategory(Category category) {
		this.currentCategory = category;

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
			// Try to fetch words from server
			List<Word> fetchedWords = wordService.getAllWords();

			if (fetchedWords == null || fetchedWords.isEmpty()) {
				System.out.println("No words found from server, using mock data");
				// Create mock data for testing
				fetchedWords = createMockWords();
			}

			this.words = fetchedWords;

			if (words != null && !words.isEmpty()) {
				// Update progress
				updateProgress();

				// Show first word
				showCurrentWord();
			} else {
				// No words found
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

	private List<Word> createMockWords() {
		List<Word> mockWords = new ArrayList<>();

		// For a Food category
		if (currentCategory.getName().equals("אוכל")) {
			Word word1 = new Word("apple", "תפוח", "en");
			word1.setId(1L);

			Word word2 = new Word("bread", "לחם", "en");
			word2.setId(2L);

			Word word3 = new Word("water", "מים", "en");
			word3.setId(3L);

			mockWords.add(word1);
			mockWords.add(word2);
			mockWords.add(word3);
		}
		// For Basic Words category
		else if (currentCategory.getName().equals("מילים בסיסיות")) {
			Word word1 = new Word("hello", "שלום", "en");
			word1.setId(4L);

			Word word2 = new Word("thank you", "תודה", "en");
			word2.setId(5L);

			Word word3 = new Word("please", "בבקשה", "en");
			word3.setId(6L);

			mockWords.add(word1);
			mockWords.add(word2);
			mockWords.add(word3);
		}
		// For Trips and Flights category
		else if (currentCategory.getName().equals("טיולים וטיסות")) {
			Word word1 = new Word("airport", "שדה תעופה", "en");
			word1.setId(7L);

			Word word2 = new Word("passport", "דרכון", "en");
			word2.setId(8L);

			Word word3 = new Word("hotel", "מלון", "en");
			word3.setId(9L);

			mockWords.add(word1);
			mockWords.add(word2);
			mockWords.add(word3);
		}

		return mockWords;
	}

	private void showCurrentWord() {
		if (words == null || words.isEmpty() || currentWordIndex >= words.size()) {
			return;
		}

		Word currentWord = words.get(currentWordIndex);

		// Set word text
		wordLabel.setText(currentWord.getWord());

		// Set translation (hidden initially)
		translationLabel.setText(currentWord.getTranslation());
		translationLabel.setVisible(false);

		// Reset buttons
		showTranslationButton.setDisable(false);
		nextWordButton.setDisable(true);
		markLearnedButton.setDisable(true);

		// Set RTL if needed for word
		if (currentWord.getLanguage() != null
				&& (currentWord.getLanguage().equals("he") || containsHebrew(currentWord.getWord()))) {
			wordLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		} else {
			wordLabel.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
		}

		// Set RTL if needed for translation
		if (currentWord.getLanguage() != null
				&& (currentWord.getLanguage().equals("en") || !containsHebrew(currentWord.getTranslation()))) {
			translationLabel.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
		} else {
			translationLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		}

		// Update message
		messageLabel.setText("Click 'Show Translation' to reveal the meaning");
	}

	@FXML
	private void handleShowTranslation(ActionEvent event) {
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
			return;
		}

		Word currentWord = words.get(currentWordIndex);

		try {
			// Mark word as learned (user ID would come from the logged-in user)
			// For now, we'll use a mock user ID
			Long userId = 1L; // This would come from the logged-in user in a real app
			userService.addLearnedWord(userId, currentWord.getId());

			// Show success message
			messageLabel.setText("Word marked as learned! Click 'Next Word' to continue.");
		} catch (Exception e) {
			System.err.println("Error marking word as learned: " + e.getMessage());
			e.printStackTrace();

			// Show error message
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