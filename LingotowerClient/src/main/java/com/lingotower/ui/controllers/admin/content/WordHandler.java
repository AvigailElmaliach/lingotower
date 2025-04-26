package com.lingotower.ui.controllers.admin.content;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;

import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;
import com.lingotower.model.Word;
import com.lingotower.service.WordService;
import com.lingotower.ui.controllers.admin.ContentManagementController;
import com.lingotower.utils.LoggingUtility;
import com.lingotower.utils.ui.BackgroundTask;
import com.lingotower.utils.ui.DialogUtils;

import javafx.application.Platform;
import javafx.collections.ObservableList;

/**
 * Handler class for Word-related operations in the ContentManagementController
 */
public class WordHandler {
	private static final Logger logger = LoggingUtility.getLogger(WordHandler.class);

	private final ContentManagementController controller;
	private final WordService wordService;

	private Word selectedWord;
	private boolean isEditMode = false;

	public WordHandler(ContentManagementController controller, WordService wordService) {
		this.controller = controller;
		this.wordService = wordService;
	}

	/**
	 * Load all words from the server
	 */
	public void loadWords() {
		BackgroundTask.run("Loading words", startTime -> {
			controller.showStatusMessage("Loading words...", false);

			List<Word> words = wordService.getAllWords();

			Platform.runLater(() -> {
				ObservableList<Word> wordsList = controller.getWordsList();
				wordsList.clear();

				if (words != null && !words.isEmpty()) {
					wordsList.addAll(words);
					controller.showStatusMessage("Loaded " + words.size() + " words successfully", false);
					LoggingUtility.logPerformance(logger, "load_words", System.currentTimeMillis() - startTime,
							"success");
				} else {
					controller.showStatusMessage("No words found", true);
					LoggingUtility.logPerformance(logger, "load_words", System.currentTimeMillis() - startTime,
							"failed");
				}
			});
		});
	}

	/**
	 * Filter words by category
	 */
	public void filterWordsByCategory(String selectedCategory) {
		logger.info("Filtering words by category: {}", selectedCategory);

		try {
			if ("All Categories".equals(selectedCategory)) {
				loadWords();
			} else {
				// Find the category by name
				Optional<Category> categoryOpt = controller.getCategoryList().stream()
						.filter(c -> c.getName().equals(selectedCategory)).findFirst();

				if (categoryOpt.isPresent()) {
					Category category = categoryOpt.get();
					logger.info("Getting words for category ID: {}", category.getId());
					List<Word> words = wordService.getWordsByCategory(category.getId());

					controller.getWordsList().clear();
					controller.getWordsList().addAll(words);

					logger.info("Found {} words for category: {}", words.size(), selectedCategory);
					controller.showStatusMessage("Found " + words.size() + " words for category: " + selectedCategory,
							false);
				} else {
					logger.warn("Selected category not found in category list: {}", selectedCategory);
					controller.showStatusMessage("Category not found: " + selectedCategory, true);
				}
			}
		} catch (Exception e) {
			logger.error("Error filtering words: {}", e.getMessage(), e);
			controller.showStatusMessage("Error filtering words: " + e.getMessage(), true);
		}
	}

	/**
	 * Show the edit form for a word
	 */
	public void showEditForm(Word word) {
		if (word == null) {
			logger.warn("Cannot edit null word");
			return;
		}

		logger.info("Showing edit form for word: {} (ID: {})", word.getWord(), word.getId());

		controller.getWordEditForm().setVisible(true);
		controller.getWordEditForm().setManaged(true);
		controller.getJsonUploadForm().setVisible(false);
		controller.getJsonUploadForm().setManaged(false);

		// Prepare form for editing
		controller.getWordFormTitle().setText("Edit Word");

		// Set category
		for (Category category : controller.getWordCategoryComboBox().getItems()) {
			if (category.getId().equals(word.getCategory().getId())) {
				controller.getWordCategoryComboBox().setValue(category);
				break;
			}
		}

		this.selectedWord = word;
		this.isEditMode = true;
	}

	/**
	 * Save a new or existing word
	 */
	public void saveWord(String wordText, String translation, Category category, Difficulty difficulty) {
		logger.info("Saving word: {} (mode: {})", wordText, isEditMode ? "edit" : "add");

		if (!validateWordForm(wordText, category, difficulty)) {
			return;
		}

		BackgroundTask.run("Save Word", startTime -> {
			try {
				if (isEditMode && selectedWord != null) {
					// Update existing word
					selectedWord.setWord(wordText);
					selectedWord.setTranslatedText(translation);
					selectedWord.setCategory(category);
					selectedWord.setDifficulty(difficulty);

					logger.info("Updating word with ID: {}", selectedWord.getId());
					Word updatedWord = wordService.updateWord(selectedWord.getId(), selectedWord);

					Platform.runLater(() -> processWordSaveResult(updatedWord, wordText, "update", startTime));
				} else {
					// Create new word
					Word newWord = new Word();
					newWord.setWord(wordText);
					newWord.setTranslatedText(translation);
					newWord.setCategory(category);
					newWord.setDifficulty(difficulty);
					newWord.setSourceLanguage("en");
					newWord.setTargetLanguage("he");

					logger.info("Creating new word: {}", wordText);
					Word createdWord = wordService.createWord(newWord);

					Platform.runLater(() -> processWordSaveResult(createdWord, wordText, "create", startTime));
				}
			} catch (Exception e) {
				Platform.runLater(() -> {
					logger.error("Error saving word: {}", e.getMessage(), e);
					controller.showStatusMessage("Error saving word: " + e.getMessage(), true);
					LoggingUtility.logAction(logger, isEditMode ? "update" : "create",
							controller.getCurrentAdmin() != null ? controller.getCurrentAdmin().getUsername()
									: "system",
							"word:" + wordText, "error: " + e.getMessage());
				});
			}
		});
	}

	/**
	 * Validate the word form inputs
	 */
	private boolean validateWordForm(String wordText, Category category, Difficulty difficulty) {
		if (wordText.isEmpty()) {
			logger.warn("Word validation failed: text is empty");
			controller.showStatusMessage("Word text cannot be empty", true);
			return false;
		}

		if (category == null) {
			logger.warn("Word validation failed: no category selected");
			controller.showStatusMessage("Please select a category", true);
			return false;
		}

		if (difficulty == null) {
			logger.warn("Word validation failed: no difficulty selected");
			controller.showStatusMessage("Please select a difficulty level", true);
			return false;
		}

		return true;
	}

	/**
	 * Process the result of saving a word
	 */
	private void processWordSaveResult(Word result, String wordText, String action, long startTime) {
		if (result != null) {
			logger.info("Word {} successfully", action.equals("update") ? "updated" : "created");
			controller.showStatusMessage("Word " + action + "d successfully", false);

			LoggingUtility.logAction(logger, action,
					controller.getCurrentAdmin() != null ? controller.getCurrentAdmin().getUsername() : "system",
					"word:" + wordText, "success");

			long duration = System.currentTimeMillis() - startTime;
			LoggingUtility.logPerformance(logger, action + "_word", duration, "success");
		} else {
			logger.warn("Failed to {} word: service returned null", action);
			controller.showStatusMessage("Failed to " + action + " word", true);

			long duration = System.currentTimeMillis() - startTime;
			LoggingUtility.logPerformance(logger, action + "_word", duration, "failed");
		}

		// Refresh and hide form
		loadWords();
		controller.getWordEditForm().setVisible(false);
		if (controller.getParentView() != null) {
			controller.getParentView().refresh();
		}
	}

	/**
	 * Delete a word
	 */
	public void deleteWord(Word word) {
		if (word == null) {
			logger.warn("Cannot delete null word");
			return;
		}

		logger.info("Delete requested for word: {} (ID: {})", word.getWord(), word.getId());

		if (DialogUtils.showDeleteConfirmation("word", word.getWord())) {
			BackgroundTask.run("Delete Word", startTime -> {
				try {
					logger.info("Deleting word with ID: {}", word.getId());
					boolean success = wordService.deleteWord(word.getId());

					Platform.runLater(() -> {
						if (success) {
							logger.info("Word deleted successfully");
							controller.showStatusMessage("Word deleted successfully", false);
							loadWords();
							if (controller.getParentView() != null) {
								controller.getParentView().refresh();
							}

							LoggingUtility.logAction(logger, "delete",
									controller.getCurrentAdmin() != null ? controller.getCurrentAdmin().getUsername()
											: "system",
									"word:" + word.getWord(), "success");
						} else {
							logger.warn("Failed to delete word: service returned false");
							controller.showStatusMessage("Failed to delete word", true);
						}
					});
				} catch (Exception e) {
					Platform.runLater(() -> {
						logger.error("Error deleting word: {}", e.getMessage(), e);
						controller.showStatusMessage("Error deleting word: " + e.getMessage(), true);
						LoggingUtility.logAction(logger, "delete",
								controller.getCurrentAdmin() != null ? controller.getCurrentAdmin().getUsername()
										: "system",
								"word:" + word.getWord(), "error: " + e.getMessage());
					});
				}
			});
		} else {
			logger.info("Word deletion cancelled by user");
		}
	}
}