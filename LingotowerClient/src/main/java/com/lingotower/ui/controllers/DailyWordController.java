package com.lingotower.ui.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
	private Label pronunciationLabel;

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

	@FXML
	private Button shareButton;

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

//	private void loadDailyWords() {
//		try {
//			// Get today's date in format YYYY-MM-DD
//			LocalDate today = LocalDate.now();
//			String dateStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE);
//
//			// In a real implementation, you would call a specific API endpoint for the
//			// daily word
//			// The endpoint should return the same word for all users on the same day
//			// For example: http://localhost:8080/dailywords/today
//
//			// If such an endpoint doesn't exist yet, we can deterministically select a word
//			// based on the date to ensure all users see the same word on the same day
//
//			// Use the day of year to determine which category to use (1-6)
//			int dayOfYear = today.getDayOfYear();
//			long categoryId = (dayOfYear % 6) + 1;
//
//			// Try to get the daily word - if a dedicated endpoint exists:
//			// dailyWord = wordService.getDailyWord();
//
//			// As a fallback, we'll get a deterministic word based on the date
//			// The server should use the seed parameter to ensure the same "random" word
//			// is returned for all users on the same day
//			List<Word> words = wordService.getWordsByCategory(categoryId);
//
//			if (words != null && !words.isEmpty()) {
//				// Use a deterministic selection based on the date
//				// This ensures all users see the same word on the same day
//				int index = dayOfYear % words.size();
//				dailyWord = words.get(index);
//			} else {
//				System.err.println("No words found for daily word");
//				displayNoWordAvailable();
//			}
//		} catch (Exception e) {
//			System.err.println("Error loading daily word: " + e.getMessage());
//			e.printStackTrace();
//			displayNoWordAvailable();
//		}
//	}
	private void loadDailyWords() {
		try {
			// Get today's date
			LocalDate today = LocalDate.now();
			// need to call to a specific API endpoint for the
			// daily word
			// For now, we'll get a random word from a category
			// Get a random category ID between 1 and 6
			int categoryId = (today.getDayOfMonth() % 6) + 1;
			// Get a random word from the category with difficulty EASY
			// Using the API endpoint:
			// http://localhost:8080/words/category/{categoryId}/difficulty/EASY/translate/random
			String url = "/category/" + categoryId + "/difficulty/EASY/translate/random?sourceLang=en&targetLang=he";
			List<Word> randomWords = wordService.getRandomWordsByCategory(categoryId, "EASY", "en", "he");
			if (randomWords != null && !randomWords.isEmpty()) {
				// Take the first word from the random selection
				dailyWord = randomWords.get(0);
			} else {
				System.err.println("No words found for daily word");
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
		translationLabel.setText(dailyWord.getTranslation());

		// Set example text (mock data for now)
		String exampleText = getExampleForWord(dailyWord.getWord());
		exampleLabel.setText(exampleText);

		// Set pronunciation (mock data for now)
		pronunciationLabel.setText(getPronunciationForWord(dailyWord.getWord()));

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
		pronunciationLabel.setText("");
		categoryLabel.setText("N/A");

		// Disable buttons
		audioButton.setDisable(true);
		addToLearnedButton.setDisable(true);
		shareButton.setDisable(true);
	}

	private String getExampleForWord(String word) {
		// This would typically come from the server
		// For now, we'll return a generic example
		return "This is an example sentence using the word \"" + word + "\".";
	}

	private String getPronunciationForWord(String word) {
		// This would typically come from a dictionary API or server
		// For now, we'll return a placeholder
		return "[pronunciation]";
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
		if (dailyWord == null || currentUserId == null) {
			return;
		}

		try {
			// Add word to user's learned words
			boolean success = userService.removeLearnedWord(currentUserId, dailyWord.getId());

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

	@FXML
	private void handleShareButtonClick(ActionEvent event) {
		if (dailyWord == null) {
			return;
		}

		// In a real application, this would open sharing options
		String shareText = "Today I learned the word \"" + dailyWord.getWord() + "\" (" + dailyWord.getTranslation()
				+ ") on LingoTower!";

		System.out.println("Sharing: " + shareText);
		// Sharing logic would go here
	}
}