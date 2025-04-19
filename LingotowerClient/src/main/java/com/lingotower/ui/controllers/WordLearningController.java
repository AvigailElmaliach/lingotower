package com.lingotower.ui.controllers;

import java.util.List;
import java.util.stream.Collectors; // Import for stream operations

import com.lingotower.model.Category;
import com.lingotower.model.User;
import com.lingotower.model.Word;
import com.lingotower.service.ExampleSentencesService; // Import the service
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
	private Label ExamplesUsageLabel; // Label to show example sentences

	@FXML
	private Button showTranslationButton;
	@FXML
	private Button showExamplesButton; // Button to trigger showing examples

	@FXML
	private Button nextWordButton;

	@FXML
	private Label messageLabel;

	@FXML
	private Label messageLabel1; // Consider removing if unused

	@FXML
	private Button backButton;

	@FXML
	private Button markLearnedButton;

	// Services
	private WordService wordService;
	private UserService userService;
	private ExampleSentencesService exampleSentencesService; // Added service instance

	// State variables
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
		exampleSentencesService = new ExampleSentencesService(); // Initialize the new service

		// Initial button states
		nextWordButton.setDisable(true);
		markLearnedButton.setDisable(true);
		showTranslationButton.setDisable(false); // Should be enabled initially
		showExamplesButton.setDisable(false); // Should be enabled initially

		// Hide translation and examples initially
		translationLabel.setVisible(false);
		ExamplesUsageLabel.setVisible(false); // Ensure examples are hidden too
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
		if (category != null) {
			categoryNameLabel.setText(category.getName());
			// Set RTL if needed
			if (containsHebrew(category.getName())) {
				categoryNameLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
			} else {
				categoryNameLabel.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
			}
			// Load words for this category
			loadWords();
		} else {
			categoryNameLabel.setText("No Category Selected");
			// Handle case where category is null
		}
	}

	private void loadWords() {
		if (currentCategory == null || currentCategory.getId() == null) {
			System.err.println("Cannot load words: Current category or its ID is null.");
			wordLabel.setText("Error loading words: Invalid category.");
			disableAllActionButtons();
			return;
		}
		try {
			System.out.println("Fetching words for category ID: " + currentCategory.getId());
			List<Word> fetchedWords = wordService.getWordsByCategory(currentCategory.getId());
			this.words = fetchedWords;

			if (words != null && !words.isEmpty()) {
				System.out.println("Loaded " + words.size() + " words successfully");
				currentWordIndex = 0; // Reset index when loading new words
				updateProgress();
				showCurrentWord();
			} else {
				System.out.println("No words found for this category");
				wordLabel.setText("No words found for this category");
				disableAllActionButtons();
			}
		} catch (Exception e) {
			System.err.println("Error loading words: " + e.getMessage());
			e.printStackTrace();
			wordLabel.setText("Error loading words");
			messageLabel.setText("Could not load words: " + e.getMessage());
			disableAllActionButtons();
		}
	}

	private void disableAllActionButtons() {
		showTranslationButton.setDisable(true);
		showExamplesButton.setDisable(true);
		nextWordButton.setDisable(true);
		markLearnedButton.setDisable(true);
	}

	private void showCurrentWord() {
		if (words == null || words.isEmpty() || currentWordIndex >= words.size()) {
			System.out.println("Cannot show current word: words list is empty or index out of bounds");
			// Optionally display a message like "Category complete" or handle appropriately
			wordLabel.setText("End of category reached.");
			disableAllActionButtons();
			return;
		}

		Word currentWord = words.get(currentWordIndex);
		if (currentWord == null) {
			System.err.println("Error: Current word at index " + currentWordIndex + " is null.");
			wordLabel.setText("Error displaying word.");
			disableAllActionButtons();
			return;
		}

		System.out.println(
				"Showing word: " + currentWord.getWord() + " (Translation: " + currentWord.getTranslatedText() + ")");

		// Set word text
		wordLabel.setText(currentWord.getWord());

		// Set translation (hidden initially)
		translationLabel.setText(currentWord.getTranslatedText());
		translationLabel.setVisible(false);

		// --- Reset Examples Usage Label ---
		ExamplesUsageLabel.setText(""); // Clear previous examples
		ExamplesUsageLabel.setVisible(false);
		// --- End Reset ---

		// Reset buttons for the new word
		showTranslationButton.setDisable(false);
		showExamplesButton.setDisable(false); // Re-enable show examples button
		nextWordButton.setDisable(true); // Next should be disabled until translation or example is shown
		markLearnedButton.setDisable(true); // Mark learned disabled until translation or example is shown

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
		messageLabel.setText("Click 'Show Translation' or 'Show Examples'");
	}

	@FXML
	public void handleShowTranslation(ActionEvent event) {
		// Show translation
		translationLabel.setVisible(true);

		// Enable next word and mark learned buttons
		nextWordButton.setDisable(false);
		markLearnedButton.setDisable(false);

		// Disable show translation button as it's already shown
		showTranslationButton.setDisable(true);

		// Update message based on whether examples button is still available
		updateMessageAfterReveal();
	}

	@FXML
	public void handleShowExamples(ActionEvent event) {
		if (words == null || words.isEmpty() || currentWordIndex >= words.size()) {
			ExamplesUsageLabel.setText("No current word selected.");
			ExamplesUsageLabel.setVisible(true);
			return;
		}
		Word currentWord = words.get(currentWordIndex);
		// --- Check if word object or word text is null/blank ---
		if (currentWord == null || currentWord.getWord() == null || currentWord.getWord().isBlank()) {
			System.err.println("Cannot fetch examples: Current word text is missing.");
			ExamplesUsageLabel.setText("Cannot fetch examples: Word data missing.");
			ExamplesUsageLabel.setVisible(true);
			return;
		}
		String wordText = currentWord.getWord(); // --- Use word text ---

		ExamplesUsageLabel.setText("Loading examples..."); // Provide feedback while loading
		ExamplesUsageLabel.setVisible(true);
		showExamplesButton.setDisable(true); // Disable button immediately

		try {
			// --- Fetch Examples using the Service with WORD TEXT ---
			System.out.println("Fetching examples for word: " + wordText);
			// --- Call the actual service method with String ---
			List<String> examples = exampleSentencesService.getExampleSentences(wordText);

			if (examples != null && !examples.isEmpty()) {
				// --- Check if the returned list contains a specific info/error message from
				// the service ---
				String firstLine = examples.get(0);
				boolean isInfoMessage = examples.size() == 1
						&& (firstLine.startsWith("No example sentences") || firstLine.startsWith("Error") || // Catches
																												// "Error
																												// connecting..."
																												// and
																												// "An
																												// unexpected
																												// error..."
								firstLine.startsWith("Authentication error") || firstLine.startsWith("HTTP Error")
								|| firstLine.startsWith("No word provided")); // Check for messages defined in the
																				// service

				if (isInfoMessage) {
					ExamplesUsageLabel.setText(firstLine); // Display the message directly from the service
					System.out.println("Service returned info/error message: " + firstLine);
				} else {
					// Format the actual examples (assuming the list contains real sentences)
					String examplesText = examples.stream().filter(s -> s != null && !s.isBlank()) // Filter out null or
																									// blank lines just
																									// in case
							.map(s -> "- " + s) // Add a bullet point
							.collect(Collectors.joining("\n")); // Join with newlines

					if (examplesText.isEmpty()) {
						// This case might happen if the list contained only blank strings after
						// filtering
						ExamplesUsageLabel.setText("No valid examples found for this word.");
					} else {
						ExamplesUsageLabel.setText(examplesText);
						System.out.println("Displayed " + examples.size() + " example sentences.");
						// --- Set RTL if examples contain Hebrew ---
						if (containsHebrew(ExamplesUsageLabel.getText())) {
							ExamplesUsageLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
						} else {
							ExamplesUsageLabel.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
						}
					}
				}
			} else {
				// This case means the service returned null or an empty list, which shouldn't
				// happen based on its code, but good to handle defensively.
				ExamplesUsageLabel.setText("No examples returned for this word.");
				System.out.println("Service returned null or empty list for word: " + wordText);
			}
			// --- End Fetch Examples ---

		} catch (Exception e) {
			// Catch any truly unexpected exceptions from the service call itself
			System.err.println(
					"Unexpected error calling ExampleSentencesService for word '" + wordText + "': " + e.getMessage());
			e.printStackTrace(); // Log the full stack trace for debugging
			ExamplesUsageLabel.setText("System error loading examples."); // Show a generic error
		}

		// Make sure the label is visible after attempting to load/display
		ExamplesUsageLabel.setVisible(true);

		// Enable next word and mark learned buttons now that the action is complete
		nextWordButton.setDisable(false);
		markLearnedButton.setDisable(false);

		// Update the user instruction message
		updateMessageAfterReveal();
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

	/**
	 * Updates the instruction message label based on which reveal buttons are
	 * disabled.
	 */
	private void updateMessageAfterReveal() {
		boolean translationShown = showTranslationButton.isDisabled();
		boolean examplesShown = showExamplesButton.isDisabled();

		if (translationShown && examplesShown) {
			messageLabel.setText("Click 'Next Word' or 'Mark as Learned'");
		} else if (translationShown) {
			messageLabel.setText("Click 'Next Word', 'Mark as Learned', or 'Show Examples'");
		} else if (examplesShown) {
			messageLabel.setText("Click 'Next Word', 'Mark as Learned', or 'Show Translation'");
		} else {
			// Should not happen if called correctly, but as a fallback:
			messageLabel.setText("Click 'Next Word', 'Mark as Learned', 'Show Translation', or 'Show Examples'");
		}
	}

	@FXML
	private void handleBackToDashboard(ActionEvent event) {
		if (onBackToDashboard != null) {
			onBackToDashboard.run();
		} else {
			System.err.println("Error: Back to dashboard action not set.");
			messageLabel.setText("Error: Cannot go back.");
		}
	}

	private void updateProgress() {
		if (words == null || words.isEmpty()) {
			progressBar.setProgress(0);
			progressLabel.setText("0/0");
			return;
		}

		// Calculate progress (make sure index is within bounds for display)
		int displayIndex = Math.min(currentWordIndex, words.size() - 1);
		double progress = (double) (displayIndex + 1) / words.size();
		progressBar.setProgress(progress);
		progressLabel.setText((displayIndex + 1) + "/" + words.size());
	}

	private boolean containsHebrew(String text) {
		if (text == null)
			return false;
		// Check if any character in the string belongs to the Hebrew Unicode block
		return text.codePoints().anyMatch(c -> Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HEBREW);
	}

	public void setOnBackToDashboard(Runnable callback) {
		this.onBackToDashboard = callback;
	}
}