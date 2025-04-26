package com.lingotower.ui.controllers.wordlearning;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.slf4j.Logger;

import com.lingotower.model.Category;
import com.lingotower.model.User;
import com.lingotower.model.Word;
import com.lingotower.service.UserService;
import com.lingotower.service.WordService;
import com.lingotower.utils.HebrewUtils;
import com.lingotower.utils.LoggingUtility;

import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Manages the display of words, translations, and related functionality
 */
public class WordDisplayManager {
	private static final Logger logger = LoggingUtility.getLogger(WordDisplayManager.class);

	// UI Components
	private final Label wordLabel;
	private final Label translationLabel;
	private final VBox wordCard;
	private final Button showTranslationButton;
	private final Button markLearnedButton;
	private final Button nextWordButton;
	private final Label messageLabel;

	// Services
	private final WordService wordService;
	private final UserService userService;

	// State
	private Category currentCategory;
	private List<Word> words = new ArrayList<>();
	private int currentWordIndex = 0;
	private User currentUser;

	public WordDisplayManager(Label wordLabel, Label translationLabel, VBox wordCard, Button showTranslationButton,
			Button markLearnedButton, Button nextWordButton, Label messageLabel) {

		this.wordLabel = wordLabel;
		this.translationLabel = translationLabel;
		this.wordCard = wordCard;
		this.showTranslationButton = showTranslationButton;
		this.markLearnedButton = markLearnedButton;
		this.nextWordButton = nextWordButton;
		this.messageLabel = messageLabel;

		// Initialize services
		this.wordService = new WordService();
		this.userService = new UserService();

		// Initialize state
		resetState();
	}

	/**
	 * Sets the current user
	 */
	public void setUser(User user) {
		this.currentUser = user;
	}

	/**
	 * Sets the current category
	 */
	public void setCategory(Category category) {
		this.currentCategory = category;
		resetState();
	}

	/**
	 * Resets the state for a new category/session
	 */
	private void resetState() {
		this.words.clear();
		this.currentWordIndex = 0;

		// Reset UI
		translationLabel.setVisible(false);
		nextWordButton.setDisable(true);
		markLearnedButton.setDisable(true);
		showTranslationButton.setDisable(false);
	}

	/**
	 * Loads words for the current category
	 * 
	 * @param onProgressUpdate Callback for updating progress
	 * @param onError          Callback for handling errors
	 */
	public void loadWords(BiConsumer<Integer, Integer> onProgressUpdate, Consumer<String> onError) {
		if (currentCategory == null || currentCategory.getId() == null) {
			onError.accept("Invalid category or category ID is null");
			return;
		}

		try {
			logger.info("Loading words for category ID: {}", currentCategory.getId());
			List<Word> fetchedWords = wordService.getWordsByCategory(currentCategory.getId());

			if (fetchedWords != null && !fetchedWords.isEmpty()) {
				this.words = fetchedWords;
				this.currentWordIndex = 0;

				// Update progress via callback
				onProgressUpdate.accept(currentWordIndex, words.size());

				// Show the first word
				showCurrentWord();
			} else {
				onError.accept("No words found for this category");
			}
		} catch (Exception e) {
			logger.error("Error loading words: {}", e.getMessage(), e);
			onError.accept("Error loading words: " + e.getMessage());
		}
	}

	/**
	 * Displays the current word
	 */
	public void showCurrentWord() {
		if (words.isEmpty() || currentWordIndex >= words.size()) {
			logger.warn("Cannot show word: no words available or index out of bounds");
			messageLabel.setText("No words available to display");
			return;
		}

		Word currentWord = words.get(currentWordIndex);
		if (currentWord == null) {
			logger.error("Error: Current word at index {} is null", currentWordIndex);
			messageLabel.setText("Error displaying word.");
			return;
		}

		logger.debug("Showing word: {} (Translation: {})", currentWord.getWord(), currentWord.getTranslatedText());

		// Set word text
		wordLabel.setText(currentWord.getWord());

		// Set translation (hidden initially)
		translationLabel.setText(currentWord.getTranslatedText());
		translationLabel.setVisible(false);

		// Reset buttons
		showTranslationButton.setDisable(false);
		nextWordButton.setDisable(true);
		markLearnedButton.setDisable(true);

		// Set text direction based on content
		setTextDirection(currentWord);

		// Update message
		messageLabel
				.setText("Click 'Show Translation' to reveal the meaning or 'Show Examples' to see usage in context.");
	}

	/**
	 * Sets the appropriate text direction based on the word content
	 */
	private void setTextDirection(Word word) {
		// Set RTL if needed for word
		if (HebrewUtils.containsHebrew(word.getWord())) {
			wordLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		} else {
			wordLabel.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
		}

		// Set RTL if needed for translation
		if (HebrewUtils.containsHebrew(word.getTranslatedText())) {
			translationLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		} else {
			translationLabel.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
		}
	}

	/**
	 * Shows the translation for the current word
	 */
	public void showTranslation() {
		// Show translation
		translationLabel.setVisible(true);

		// Disable show translation button as it's already shown
		showTranslationButton.setDisable(true);

		// Update message
		messageLabel.setText("Click 'Next Word' to continue or 'Mark as Learned' to add to your vocabulary.");
	}

	/**
	 * Shows the next word
	 */
	public void showNextWord() {
		// Move to next word
		currentWordIndex++;

		// Check if we've reached the end
		if (currentWordIndex >= words.size()) {
			// Reset to beginning
			currentWordIndex = 0;
			messageLabel.setText("You've gone through all words. Starting again from the beginning.");
		}

		// Show the current word
		showCurrentWord();
	}

	/**
	 * Marks the current word as learned
	 */
	public void markCurrentWordAsLearned() {
		if (words.isEmpty() || currentWordIndex >= words.size()) {
			logger.warn("Cannot mark word as learned: no words available or index out of bounds");
			messageLabel.setText("Error: No word to mark as learned");
			return;
		}

		Word currentWord = words.get(currentWordIndex);
		if (currentWord == null || currentWord.getId() == null) {
			logger.warn("Error: Word or Word ID is null. Current word: {}", currentWord);
			messageLabel.setText("Error: Invalid word data");
			return;
		}

		if (currentUser == null) {
			logger.error("Cannot mark word as learned: User is null");
			messageLabel.setText("Error: User information is missing");
			return;
		}

		logger.info("Attempting to mark word as learned: {} (ID: {})", currentWord.getWord(), currentWord.getId());

		try {
			boolean success = userService.addWordToLearned(currentWord.getId());
			if (success) {
				messageLabel.setText("Word marked as learned!");
			} else {
				messageLabel.setText("Error marking word as learned.");
			}
		} catch (Exception e) {
			logger.error("Error marking word as learned: {}", e.getMessage(), e);
			messageLabel.setText("Error marking word as learned: " + e.getMessage());
		}
	}

	/**
	 * Gets the current word
	 */
	public Word getCurrentWord() {
		if (words.isEmpty() || currentWordIndex >= words.size()) {
			return null;
		}
		return words.get(currentWordIndex);
	}

	/**
	 * Gets the current word index
	 */
	public int getCurrentWordIndex() {
		return currentWordIndex;
	}

	/**
	 * Gets the total number of words
	 */
	public int getTotalWordsCount() {
		return words.size();
	}
}