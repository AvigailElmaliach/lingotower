package com.lingotower.ui.controllers.profile;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;

import com.lingotower.model.Category;
import com.lingotower.model.User;
import com.lingotower.model.Word;
import com.lingotower.service.CategoryService;
import com.lingotower.service.UserService;
import com.lingotower.utils.LoggingUtility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

/**
 * Handles the Learned Words tab functionality
 */
public class LearnedWordsTabHandler {
	private static final Logger logger = LoggingUtility.getLogger(LearnedWordsTabHandler.class);

	// UI Components
	private final ComboBox<String> categoryFilter;
	private final Button filterButton;
	private final ListView<String> wordsList;

	// UI state manager
	private final UIStateManager uiStateManager;

	// Services
	private final UserService userService;
	private final CategoryService categoryService;

	// Current user
	private User currentUser;

	/**
	 * Constructor with UI components
	 */
	public LearnedWordsTabHandler(ComboBox<String> categoryFilter, Button filterButton, ListView<String> wordsList,
			UIStateManager uiStateManager) {

		this.categoryFilter = categoryFilter;
		this.filterButton = filterButton;
		this.wordsList = wordsList;
		this.uiStateManager = uiStateManager;

		// Initialize services
		this.userService = new UserService();
		this.categoryService = new CategoryService();
	}

	/**
	 * Sets the user for this handler
	 */
	public void setUser(User user) {
		this.currentUser = user;
	}

	/**
	 * Loads learned words data
	 */
	public void loadData(User user) {
		this.currentUser = user;

		// Load categories for filter
		loadCategories();

		// Load user's learned words
		loadUserLearnedWords();
	}

	/**
	 * Refreshes the learned words data
	 */
	public void refreshData(User user) {
		this.currentUser = user;
		loadUserLearnedWords();
	}

	/**
	 * Loads available categories for the filter dropdown
	 */
	private void loadCategories() {
		try {
			logger.debug("Loading categories for filter");
			// Get categories for filter
			List<Category> categories = categoryService.getAllCategories();
			ObservableList<String> categoryOptions = FXCollections.observableArrayList();
			categoryOptions.add("All Categories");

			if (categories != null) {
				for (Category category : categories) {
					categoryOptions.add(category.getName());
				}
				logger.debug("Loaded {} categories for filter", categories.size());
			}

			categoryFilter.setItems(categoryOptions);
			categoryFilter.setValue("All Categories");
		} catch (Exception e) {
			logger.error("Error loading category filter: {}", e.getMessage(), e);
			categoryFilter.setItems(FXCollections.observableArrayList("All Categories"));
			categoryFilter.setValue("All Categories");
		}
	}

	/**
	 * Loads the user's learned words
	 */
	private void loadUserLearnedWords() {
		try {
			logger.info("Loading learned words for user");
			List<Word> learnedWords = userService.getLearnedWords();
			displayWordsList(learnedWords);
		} catch (Exception e) {
			logger.error("Error loading learned words: {}", e.getMessage(), e);
			wordsList.setItems(FXCollections.observableArrayList());
		}
	}

	/**
	 * Displays the words list in the ListView
	 */
	private void displayWordsList(List<Word> words) {
		ObservableList<String> wordItems = FXCollections.observableArrayList();

		if (words != null && !words.isEmpty()) {
			logger.debug("Received {} learned words", words.size());
			for (Word word : words) {
				// Format: English - Hebrew
				wordItems.add(word.getWord() + " - " + word.getTranslatedText());
			}
		} else {
			logger.warn("No learned words returned from server");
			wordItems.add("No learned words found");
		}

		wordsList.setItems(wordItems);
	}

	/**
	 * Handles the filter button click
	 */
	public void handleFilter() {
		String selectedCategory = categoryFilter.getValue();
		logger.info("Filtering words by category: {}", selectedCategory);

		try {
			List<Word> filteredWords;

			if ("All Categories".equals(selectedCategory)) {
				// Get all learned words
				logger.debug("Getting all learned words");
				filteredWords = userService.getLearnedWords();
			} else {
				// Get learned words for the selected category
				filteredWords = filterByCategory(selectedCategory);
			}

			// Update the words list
			displayWordsList(filteredWords);
			showFilterResults(filteredWords, selectedCategory);
		} catch (Exception e) {
			logger.error("Error filtering words: {}", e.getMessage(), e);

			// Show error message in the list
			ObservableList<String> errorItems = FXCollections
					.observableArrayList("Error loading words: " + e.getMessage());
			wordsList.setItems(errorItems);

			uiStateManager.showErrorMessage("Error filtering words: " + e.getMessage());
		}
	}

	/**
	 * Filters words by category name
	 */
	private List<Word> filterByCategory(String categoryName) {
		// Find the category by name
		List<Category> categories = categoryService.getAllCategories();
		Optional<Category> categoryOpt = categories.stream().filter(c -> c.getName().equals(categoryName)).findFirst();

		if (categoryOpt.isPresent()) {
			Category category = categoryOpt.get();
			logger.info("Getting words for category ID: {}", category.getId());
			return userService.getLearnedWordsByCategory(category.getId());
		} else {
			logger.warn("Selected category not found: {}", categoryName);
			return userService.getLearnedWords(); // Fallback to all words
		}
	}

	/**
	 * Shows filter results message
	 */
	private void showFilterResults(List<Word> filteredWords, String selectedCategory) {
		int count = filteredWords != null ? filteredWords.size() : 0;
		if (count > 0) {
			uiStateManager
					.showSuccessMessage(String.format("Found %d words for category: %s", count, selectedCategory));
		} else {
			uiStateManager.showErrorMessage(String.format("No words found for category: %s", selectedCategory));
		}
	}
}