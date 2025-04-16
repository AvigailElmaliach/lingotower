package com.lingotower.ui.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.lingotower.model.User;
import com.lingotower.model.Word;
import com.lingotower.service.UserService;
import com.lingotower.service.WordService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class DailyWordController {

	@FXML
	private BorderPane view;

	@FXML
	private Label dateLabel;

	@FXML
	private Label wordLabel;

	@FXML
	private Label translationLabel;

	@FXML
	private Label exampleLabel;

	@FXML
	private Label categoryLabel;

	@FXML
	private Button audioButton;

	@FXML
	private Button addToLearnedButton;

	private WordService wordService;
	private UserService userService;

	// The daily word
	private Word dailyWord;

	// Current user ID - should be set when the user logs in
	private Long currentUserId;

	@FXML
	private void initialize() {
		// Initialize services
		wordService = new WordService();
		userService = new UserService();

		// Set current date
		LocalDate today = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
		dateLabel.setText(today.format(formatter));

		// Load daily words - wait until user is set if needed
		if (currentUserId != null) {
			loadDailyWords();
			showCurrentDailyWord();
		}
	}

	/**
	 * Sets the current user ID for this controller
	 * 
	 * @param userId The user ID of the logged-in user
	 */
	public void setUser(User user) {
		if (user != null) {
			this.currentUserId = user.getId();

			// Load words if not already loaded
			if (dailyWord == null) {
				loadDailyWords();
				showCurrentDailyWord();
			}
		}
	}

	private void loadDailyWords() {
		try {
			// Fetch the daily word using the WordService
			dailyWord = wordService.getDailyWord();

			if (dailyWord == null) {
				System.err.println("No daily word available from the service");
				displayNoWordAvailable();
			}
		} catch (Exception e) {
			System.err.println("Error loading daily word: " + e.getMessage());
			e.printStackTrace();
			displayNoWordAvailable();
		}
	}

	private void showCurrentDailyWord() {
		if (dailyWord == null) {
			displayNoWordAvailable();
			return;
		}

		// Update UI
		wordLabel.setText(dailyWord.getWord());
		translationLabel.setText(dailyWord.getTranslatedText());

		// Set example text (mock data for now)
		String exampleText = getExampleForWord(dailyWord.getWord());
		exampleLabel.setText(exampleText);

		// Set category
		if (dailyWord.getCategory() != null) {
			categoryLabel.setText(dailyWord.getCategory().getName());
		} else {
			categoryLabel.setText("General");
		}
	}

	private void displayNoWordAvailable() {
		wordLabel.setText("No daily word available");
		translationLabel.setText("N/A");
		exampleLabel.setText("Check back tomorrow for a new word!");
		categoryLabel.setText("N/A");

		// Disable buttons
		audioButton.setDisable(true);
		addToLearnedButton.setDisable(true);
	}

	private String getExampleForWord(String word) {
		// This would typically come from the server
		// For now, we'll return a generic example
		return "This is an example sentence using the word \"" + word + "\".";
	}

	@FXML
	private void handleRefreshButtonClick(ActionEvent event) {
		// Reload the daily word
		loadDailyWords();
		showCurrentDailyWord();
	}

	@FXML
	private void handleAudioButtonClick(ActionEvent event) {
		// In a real application, this would play audio pronunciation
		System.out.println("Playing audio for: " + wordLabel.getText());
		// Audio player logic would go here
	}

	@FXML
	private void handleAddToLearnedClick(ActionEvent event) {
		if (dailyWord == null) {
			return;
		}

		try {
			// Add word to user's learned words
			boolean success = userService.addWordToLearned(dailyWord.getId());

			if (success) {
				// Disable the button to indicate it's been added
				addToLearnedButton.setDisable(true);
				addToLearnedButton.setText("Added to Learned");
			} else {
				// Show error if failed
				System.err.println("Failed to add word to learned words");
			}
		} catch (Exception e) {
			System.err.println("Error adding word to learned: " + e.getMessage());
			e.printStackTrace();
		}
	}

}