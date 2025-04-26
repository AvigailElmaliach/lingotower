package com.lingotower.ui.controllers.wordlearning;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.lingotower.model.Word;
import com.lingotower.service.ExampleSentencesService;
import com.lingotower.utils.HebrewUtils;
import com.lingotower.utils.LoggingUtility;

import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Manages fetching and displaying example sentences for words
 */
public class ExampleManager {
	private static final Logger logger = LoggingUtility.getLogger(ExampleManager.class);

	// UI Components
	private final Label examplesUsageLabel;
	private final Button showExamplesButton;

	// Services
	private final ExampleSentencesService exampleSentencesService;

	public ExampleManager(Label examplesUsageLabel, Button showExamplesButton) {
		this.examplesUsageLabel = examplesUsageLabel;
		this.showExamplesButton = showExamplesButton;

		// Initialize services
		this.exampleSentencesService = new ExampleSentencesService();

		// Initialize state
		examplesUsageLabel.setVisible(false);
	}

	/**
	 * Fetches and displays examples for a word
	 * 
	 * @param word The word to fetch examples for
	 */
	public void fetchAndDisplayExamples(Word word) {
		// Clear the text area before displaying new examples
		examplesUsageLabel.setText("");

		if (word == null) {
			logger.warn("Cannot fetch examples for null word");
			examplesUsageLabel.setText("No word selected.");
			examplesUsageLabel.setVisible(true);
			return;
		}

		// Disable button immediately to prevent multiple clicks
		showExamplesButton.setDisable(true);

		// Determine which word to use for lookup (English word)
		String wordToLookup = determineWordToLookup(word);
		if (wordToLookup == null) {
			examplesUsageLabel.setText("Cannot fetch examples: Missing English translation.");
			examplesUsageLabel.setVisible(true);
			return;
		}

		// Show loading indicator
		examplesUsageLabel.setText("Loading examples...");
		examplesUsageLabel.setVisible(true);

		// Fetch and display examples
		try {
			logger.debug("Fetching examples for word: {}", wordToLookup);
			List<String> examples = exampleSentencesService.getExampleSentences(wordToLookup);

			if (examples == null || examples.isEmpty()) {
				examplesUsageLabel.setText("No examples returned for this word.");
				logger.warn("Service returned null or empty list for word: {}", wordToLookup);
				return;
			}

			// Check if the first example is an error/info message
			String firstLine = examples.get(0);
			if (isInfoOrErrorMessage(firstLine) && examples.size() == 1) {
				examplesUsageLabel.setText(firstLine);
				logger.debug("Service returned info/error message: {}", firstLine);
				return;
			}

			// Format and display actual examples
			displayFormattedExamples(examples);

		} catch (Exception e) {
			handleExamplesFetchingError(e, wordToLookup);
		}
	}

	/**
	 * Determines which word text to use for example lookup (English word)
	 */
	private String determineWordToLookup(Word word) {
		// If the current word is in Hebrew, we need to use its translation (English)
		if (HebrewUtils.containsHebrew(word.getWord())) {
			// Use the translated text (English) for lookup
			if (word.getTranslatedText() != null && !word.getTranslatedText().isBlank()) {
				String wordToLookup = word.getTranslatedText();
				logger.debug("Using English translation '{}' to lookup examples for Hebrew word '{}'", wordToLookup,
						word.getWord());
				return wordToLookup;
			} else {
				logger.warn("Hebrew word has no English translation: {}", word.getWord());
				return null;
			}
		} else {
			// If it's already English, use the word directly
			return word.getWord();
		}
	}

	/**
	 * Checks if the provided message is an info or error message from the service
	 */
	private boolean isInfoOrErrorMessage(String message) {
		return message.startsWith("No example sentences") || message.startsWith("Error")
				|| message.startsWith("Authentication error") || message.startsWith("HTTP Error")
				|| message.startsWith("No word provided");
	}

	/**
	 * Formats and displays the examples
	 */
	private void displayFormattedExamples(List<String> examples) {
		String examplesText = examples.stream().filter(s -> s != null && !s.isBlank()).map(s -> "- " + s)
				.collect(Collectors.joining("\n"));

		if (examplesText.isEmpty()) {
			examplesUsageLabel.setText("No valid examples found for this word.");
		} else {
			examplesUsageLabel.setText(examplesText);
			logger.debug("Displayed {} example sentences.", examples.size());

			// Set text direction based on content
			if (HebrewUtils.containsHebrew(examplesUsageLabel.getText())) {
				examplesUsageLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
			} else {
				examplesUsageLabel.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
			}
		}
	}

	/**
	 * Handles errors during example fetching
	 */
	private void handleExamplesFetchingError(Exception e, String wordToLookup) {
		logger.error("Unexpected error calling ExampleSentencesService for word '{}': {}", wordToLookup, e.getMessage(),
				e);
		examplesUsageLabel.setText("System error loading examples.");
	}

	/**
	 * Resets the examples state
	 */
	public void reset() {
		examplesUsageLabel.setText("");
		examplesUsageLabel.setVisible(false);
		showExamplesButton.setDisable(false);
	}
}