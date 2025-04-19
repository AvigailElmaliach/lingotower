package com.lingotower.ui.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.lingotower.model.User;
import com.lingotower.model.Word;
import com.lingotower.service.ExampleSentencesService;
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
	private Button addToLearnedButton;

	private WordService wordService;
	private UserService userService;
	private ExampleSentencesService exampleSentencesService;

	// The daily word
	private Word dailyWord;

	private Long currentUserId; // Keep track of user ID if needed for specific calls

	@FXML
	private void initialize() {
		// --- Service Initialization ---
		try {
			wordService = new WordService();
			userService = new UserService();
			exampleSentencesService = new ExampleSentencesService();
		} catch (Exception e) {
			// Handle cases where services might not be initialized correctly
			System.err.println("FATAL: Could not initialize services in DailyWordController: " + e.getMessage());
			e.printStackTrace();
			// Optionally disable UI elements or show an error message
			displayServiceError();
			return; // Stop initialization if services failed
		}
		// --- End Service Initialization ---

		// Set current date
		LocalDate today = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy"); // Added yyyy for clarity
		dateLabel.setText(today.format(formatter));

	}

	/**
	 * Sets the current user for this controller and triggers data loading. This
	 * should be called by the parent controller/view that manages login state.
	 *
	 * @param user The logged-in user object.
	 */
	public void setUser(User user) {
		if (user != null) {
			this.currentUserId = user.getId();
			System.out.println("DailyWordController: User set with ID: " + this.currentUserId);

			// Now that we have the user (and thus the token is likely set in TokenStorage),
			// load the daily word.
			loadDailyWordsAndDisplay();
		} else {
			System.err.println("DailyWordController: Attempted to set a null user.");
			// Handle scenario where user becomes null (e.g., logout) if necessary
			displayNoWordAvailable(); // Or show a generic "please log in" message
		}
	}

	/**
	 * Loads the daily word from the service and updates the UI.
	 */
	private void loadDailyWordsAndDisplay() {
		// Ensure services are available before proceeding
		if (wordService == null || userService == null || exampleSentencesService == null) {
			System.err.println("Cannot load daily word: Services not initialized.");
			displayServiceError();
			return;
		}

		try {
			// Fetch the daily word using the WordService
			System.out.println("Fetching daily word...");
			dailyWord = wordService.getDailyWord();

			if (dailyWord != null) {
				System.out.println("Daily word received: " + dailyWord.getWord());
				showCurrentDailyWord(); // Update UI if word found
			} else {
				System.err.println("No daily word available from the service.");
				displayNoWordAvailable(); // Update UI if no word found
			}
		} catch (Exception e) {
			System.err.println("Error loading daily word: " + e.getMessage());
			e.printStackTrace(); // Log the full error
			displayError("Could not load the daily word. Please try again later."); // Show user-friendly error
		}
	}

	/**
	 * Updates the UI elements with the details of the current daily word. Assumes
	 * dailyWord is not null when called.
	 */
	private void showCurrentDailyWord() {
		if (dailyWord == null) { // Safety check
			displayNoWordAvailable();
			return;
		}

		// Update UI labels
		wordLabel.setText(dailyWord.getWord());
		translationLabel.setText(dailyWord.getTranslatedText());

		// Fetch and set example sentence using the ExampleSentencesService
		String exampleText = getExampleForWord(dailyWord.getWord());
		exampleLabel.setText(exampleText);

		// Set category label
		if (dailyWord.getCategory() != null && dailyWord.getCategory().getName() != null) {
			categoryLabel.setText(dailyWord.getCategory().getName());
		} else {
			categoryLabel.setText("General"); // Default or "N/A"
		}

		// Enable buttons
		addToLearnedButton.setDisable(false); // Enable adding initially
		addToLearnedButton.setText("Add to Learned"); // Reset button text

		// Optional: Check if the word is *already* learned by the user
		// checkIfWordIsLearned(); // Needs implementation using UserService
	}

	/**
	 * Updates the UI to show that no daily word is available.
	 */
	private void displayNoWordAvailable() {
		wordLabel.setText("No Daily Word");
		translationLabel.setText("N/A");
		exampleLabel.setText("Please check back tomorrow!");
		categoryLabel.setText("N/A");

		// Disable buttons
		addToLearnedButton.setDisable(true);
		addToLearnedButton.setText("Add to Learned"); // Reset text
		dailyWord = null; // Ensure dailyWord is null
	}

	/**
	 * Updates the UI to indicate a service initialization error.
	 */
	private void displayServiceError() {
		wordLabel.setText("Error");
		translationLabel.setText("Service unavailable");
		exampleLabel.setText("Could not load services. Please restart the application.");
		categoryLabel.setText("Error");
		addToLearnedButton.setDisable(true);
	}

	/**
	 * Displays a general error message in the UI.
	 * 
	 * @param message The message to display.
	 */
	private void displayError(String message) {
		wordLabel.setText("Error");
		translationLabel.setText("");
		exampleLabel.setText(message);
		categoryLabel.setText("");
		addToLearnedButton.setDisable(true);
	}

	/**
	 * Fetches up to two example sentences for the given word. Joins the sentences
	 * with a newline if two are available.
	 *
	 * @param word The word to get examples for.
	 * @return Up to two example sentences joined by a newline, or a default/error
	 *         message.
	 */
	public String getExampleForWord(String word) {
		// Ensure service is available
		if (exampleSentencesService == null) {
			System.err.println("getExampleForWord: ExampleSentencesService is null.");
			return "Example service not available.";
		}

		try {
			System.out.println("Fetching example sentences for: " + word);
			List<String> examples = exampleSentencesService.getExampleSentences(word);

			// Check if the list itself is null or empty
			if (examples == null || examples.isEmpty()) {
				System.out.println("No examples list returned or list is empty for: " + word);
				return "No examples available."; // Return default message
			}

			// Check if the first element indicates an error or default message from the
			// service
			String firstExample = examples.get(0);
			if (firstExample.startsWith("No example sentences available") || firstExample.startsWith("Error fetching")
					|| firstExample.startsWith("HTTP Error") || // Add other potential error prefixes from the service
					firstExample.startsWith("Authentication error")
					|| firstExample.startsWith("An unexpected error occurred")) {

				System.out.println("Service returned specific message: " + firstExample);
				return firstExample; // Display the message from the service directly
			}

			// We have at least one valid sentence
			System.out.println("Found first example: " + firstExample);

			// Check if there is a second sentence
			if (examples.size() >= 2) {
				String secondExample = examples.get(1);
				System.out.println("Found second example: " + secondExample);
				// Return the first two sentences joined by a newline
				return firstExample + "\n" + secondExample;
			} else {
				// Only one sentence available, return just the first one
				return firstExample;
			}

		} catch (Exception e) {
			// Catch any unexpected exceptions during the process
			System.err.println(
					"Error calling getExampleSentences or processing results for '" + word + "': " + e.getMessage());
			e.printStackTrace(); // Log for detailed debugging
			// Return a user-friendly error message
			return "Could not load examples due to an error.";
		}
	}

	@FXML
	private void handleRefreshButtonClick(ActionEvent event) {
		System.out.println("Refresh button clicked.");
		// Reload the daily word, assuming user context is still valid
		loadDailyWordsAndDisplay();
	}

	@FXML
	private void handleAddToLearnedClick(ActionEvent event) {
		if (dailyWord == null || userService == null) {
			System.err.println("Cannot add word: Daily word or User service is null.");
			return;
		}
		if (currentUserId == null) {
			System.err.println("Cannot add word: Current User ID is null.");
			// Maybe show a message?
			return;
		}

		try {
			// Use UserService to add the word to the current user's learned list
			// NOTE: The current UserService.addWordToLearned takes only wordId.
			// It implicitly uses the token to identify the user.
			System.out.println("Adding word ID " + dailyWord.getId()
					+ " to learned words for user associated with current token.");
			boolean success = userService.addWordToLearned(dailyWord.getId());

			if (success) {
				System.out.println("Word successfully added to learned words.");
				// Disable the button and update text to indicate success
				addToLearnedButton.setDisable(true);
				addToLearnedButton.setText("Added!");
			} else {
				// Show error if failed (e.g., word already added, server error)
				System.err.println("Failed to add word to learned words (server returned false or error).");
				displayError("Could not add word. It might already be in your learned list.");
			}
		} catch (Exception e) {
			System.err.println("Exception occurred while adding word to learned: " + e.getMessage());
			e.printStackTrace();
			displayError("An error occurred. Please try again.");
		}
	}

}