package com.lingotower.ui.controllers.admin;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;

import com.lingotower.model.Admin;
import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;
import com.lingotower.model.Word;
import com.lingotower.service.AdminService;
import com.lingotower.service.CategoryService;
import com.lingotower.service.WordService;
import com.lingotower.ui.components.ActionButtonCell;
import com.lingotower.ui.views.admin.ContentManagementView;
import com.lingotower.utils.LoggingUtility;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ContentManagementController {
	// Add a logger using LoggingUtility
	private static final Logger logger = LoggingUtility.getLogger(ContentManagementController.class);

	@FXML
	private BorderPane view;

	// Category management controls
	@FXML
	private TableView<Category> categoryTableView;

	@FXML
	private TableColumn<Category, Long> categoryIdColumn;

	@FXML
	private TableColumn<Category, String> categoryNameColumn;

	@FXML
	private TableColumn<Category, String> categoryTranslationColumn;

	@FXML
	private TableColumn<Category, String> categoryActionsColumn;

	@FXML
	private VBox categoryEditForm;

	@FXML
	private Label categoryFormTitle;

	@FXML
	private TextField categoryNameField;

	@FXML
	private TextField categoryTranslationField;

	// Word management controls
	@FXML
	private TableView<Word> wordTableView;

	@FXML
	private TableColumn<Word, Long> wordIdColumn;

	@FXML
	private TableColumn<Word, String> wordTextColumn;

	@FXML
	private TableColumn<Word, String> wordTranslationColumn;

	@FXML
	private TableColumn<Word, String> wordCategoryColumn;

	@FXML
	private TableColumn<Word, String> wordDifficultyColumn;

	@FXML
	private TableColumn<Word, String> wordActionsColumn;

	@FXML
	private ComboBox<String> wordCategoryFilter;

	@FXML
	private VBox wordEditForm;

	@FXML
	private Label wordFormTitle;

	@FXML
	private TextField wordTextField;

	@FXML
	private TextField wordTranslationField;

	@FXML
	private ComboBox<Category> wordCategoryComboBox;

	@FXML
	private ComboBox<Difficulty> wordDifficultyComboBox;

	@FXML
	private Label statusLabel;

	@FXML
	private VBox jsonUploadForm;

	@FXML
	private TextField selectedFileTextField;

	@FXML
	private ComboBox<Category> uploadCategoryComboBox;

	@FXML
	private Button uploadJsonButton;

	private File selectedJsonFile;

	private Admin currentAdmin;
	private Runnable returnToDashboard;
	private ContentManagementView parentView; // Reference to parent view

	private CategoryService categoryService;
	private WordService wordService;
	private AdminService adminService;

	private ObservableList<Category> categoryList = FXCollections.observableArrayList();
	private ObservableList<Word> wordsList = FXCollections.observableArrayList();

	private Category selectedCategory;
	private Word selectedWord;
	private boolean isEditMode = false;

	public ContentManagementController() {
		// Initialize services
		categoryService = new CategoryService();
		wordService = new WordService();
		adminService = new AdminService();
		logger.debug("ContentManagementController services initialized");
	}

	/**
	 * Sets the parent view reference
	 * 
	 * @param view The parent view
	 */
	public void setParentView(ContentManagementView view) {
		this.parentView = view;
		logger.debug("Parent view set in ContentManagementController");
	}

	@FXML
	public void initialize() {
		// Initialize category table columns
		categoryIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		categoryTranslationColumn.setCellValueFactory(new PropertyValueFactory<>("translation"));

		// Setup category actions column using ActionButtonCell
		categoryActionsColumn.setCellFactory(col -> new ActionButtonCell<>(event -> { // Edit handler
			Category category = (Category) event.getSource(); // Get the object from the event
			showCategoryEditForm(category);
		}, event -> { // Delete handler
			Category category = (Category) event.getSource(); // Get the object from the event
			deleteCategory(category);
		}));

		// Initialize word table columns
		wordIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		wordTextColumn.setCellValueFactory(new PropertyValueFactory<>("word"));
		wordTranslationColumn.setCellValueFactory(cellData -> {
			Word word = cellData.getValue();
			if (word == null) {
				logger.warn("Table Cell - Word object is null");
				return new SimpleStringProperty("Error: Null Word");
			}
			String translation = word.getTranslatedText();
			return new SimpleStringProperty(translation != null ? translation : "<No Translation>");
		});
		wordCategoryColumn.setCellValueFactory(cellData -> {
			Category category = cellData.getValue().getCategory();
			return new SimpleStringProperty(category != null ? category.getName() : "N/A");
		});
		wordDifficultyColumn.setCellValueFactory(cellData -> {
			Difficulty difficulty = cellData.getValue().getDifficulty();
			return new SimpleStringProperty(difficulty != null ? difficulty.toString() : "N/A");
		});

		// Setup word actions column using ActionButtonCell
		wordActionsColumn.setCellFactory(col -> new ActionButtonCell<>(event -> { // Edit handler
			Word word = (Word) event.getSource(); // Get the object from the event
			showWordEditForm(word);
		}, event -> { // Delete handler
			Word word = (Word) event.getSource(); // Get the object from the event
			deleteWord(word);
		}));

		// Initialize difficulty combo box
		wordDifficultyComboBox.getItems().addAll(Difficulty.values());

		// Initialize JSON upload form (make sure it's hidden initially)
		if (jsonUploadForm != null) {
			jsonUploadForm.setVisible(false);
		}

		// Set tables to use our observable lists
		categoryTableView.setItems(categoryList);
		wordTableView.setItems(wordsList);

		// Load initial data
		loadCategories();
		setupCategoryFilter();
		loadWords();

		logger.info("ContentManagementController UI initialized");
	}

	public void setAdmin(Admin admin) {
		this.currentAdmin = admin;
		logger.info("Admin set: {}", admin != null ? admin.getUsername() : "null");
	}

	public void setReturnToDashboard(Runnable callback) {
		this.returnToDashboard = callback;
		logger.debug("Return to dashboard callback set");
	}

	@FXML
	private void handleBackButton() {
		logger.info("Back button clicked");
		if (returnToDashboard != null) {
			returnToDashboard.run();
			// Log only critical user actions
			LoggingUtility.logAction(logger, "navigation", currentAdmin != null ? currentAdmin.getUsername() : "system",
					"dashboard", "success");
		} else {
			logger.warn("Return to dashboard callback is null");
		}
	}

	public void loadCategories() {
		long startTime = System.currentTimeMillis();
		try {
			// Show loading indicator or status message
			showStatusMessage("Loading categories...", false);
			logger.info("Loading categories...");

			// Clear existing list
			categoryList.clear();

			// Get categories from service
			List<Category> categories = categoryService.getAllCategories();

			// Check if we got valid data
			if (categories == null || categories.isEmpty()) {
				logger.warn("No categories found or unable to connect to server");
				showStatusMessage("No categories found or unable to connect to server", true);
				return;
			}

			// Add categories to the list
			categoryList.addAll(categories);

			// Update dropdowns and other UI elements
			updateCategoryComboBoxes();

			// Show success message
			logger.info("Loaded {} categories successfully", categories.size());
			showStatusMessage("Loaded " + categories.size() + " categories successfully", false);

			// Only log performance for long operations
			long duration = System.currentTimeMillis() - startTime;
			if (duration > 1000) { // Only log if it took more than 1 second
				LoggingUtility.logPerformance(logger, "load_categories", duration, "success");
			}

		} catch (Exception e) {
			logger.error("Error loading categories: {}", e.getMessage(), e);
			showStatusMessage("Error loading categories: " + e.getMessage(), true);

			// Always log errors
			long duration = System.currentTimeMillis() - startTime;
			LoggingUtility.logPerformance(logger, "load_categories", duration, "error");
		}
	}

	public void loadWords() {
		long startTime = System.currentTimeMillis();
		try {
			logger.info("Loading words...");
			showStatusMessage("Loading words...", false);

			wordsList.clear();
			List<Word> words = wordService.getAllWords();

			if (words == null || words.isEmpty()) {
				logger.warn("No words found");
				showStatusMessage("No words found", true);
				return;
			}

			wordsList.addAll(words);
			logger.info("Loaded {} words successfully", words.size());
			showStatusMessage("Loaded " + words.size() + " words successfully", false);

			// Only log performance for long operations
			long duration = System.currentTimeMillis() - startTime;
			if (duration > 1000) { // Only log if it took more than 1 second
				LoggingUtility.logPerformance(logger, "load_words", duration, "success");
			}
		} catch (Exception e) {
			logger.error("Error loading words: {}", e.getMessage(), e);
			showStatusMessage("Error loading words: " + e.getMessage(), true);

			// Always log errors
			long duration = System.currentTimeMillis() - startTime;
			LoggingUtility.logPerformance(logger, "load_words", duration, "error");
		}
	}

	private void updateCategoryComboBoxes() {
		// Update word category filter
		ObservableList<String> filterItems = FXCollections.observableArrayList("All Categories");
		filterItems.addAll(categoryList.stream().map(Category::getName).toList());
		wordCategoryFilter.setItems(filterItems);
		wordCategoryFilter.setValue("All Categories");

		// Update word edit form category combo box
		wordCategoryComboBox.setItems(FXCollections.observableArrayList(categoryList));

		logger.debug("Category combo boxes updated with {} categories", categoryList.size());
	}

	@FXML
	private void handleAddCategory() {
		logger.info("Add category button clicked");

		// Reset form for new category
		categoryFormTitle.setText("Add New Category");
		categoryNameField.setText("");
		categoryTranslationField.setText("");
		selectedCategory = null;
		isEditMode = false;

		// Show the form
		categoryEditForm.setVisible(true);
	}

	private void showCategoryEditForm(Category category) {
		if (category == null) {
			logger.warn("Cannot edit null category");
			return;
		}

		logger.info("Showing edit form for category: {} (ID: {})", category.getName(), category.getId());

		// Prepare form for editing
		categoryFormTitle.setText("Edit Category");
		categoryNameField.setText(category.getName());
		categoryTranslationField.setText(category.getTranslation());
		selectedCategory = category;
		isEditMode = true;

		// Show the form
		categoryEditForm.setVisible(true);
	}

	@FXML
	private void handleCancelCategoryEdit() {
		logger.debug("Category edit cancelled");
		categoryEditForm.setVisible(false);
	}

	@FXML
	private void handleSaveCategory() {
		String name = categoryNameField.getText().trim();
		String translation = categoryTranslationField.getText().trim();
		long startTime = System.currentTimeMillis();

		logger.info("Saving category: {} (mode: {})", name, isEditMode ? "edit" : "add");

		if (name.isEmpty()) {
			logger.warn("Category validation failed: name is empty");
			showStatusMessage("Category name cannot be empty", true);
			return;
		}

		try {
			if (isEditMode && selectedCategory != null) {
				// Update existing category
				selectedCategory.setName(name);
				selectedCategory.setTranslation(translation);

				logger.info("Updating category with ID: {}", selectedCategory.getId());
				Category updatedCategory = categoryService.updateCategory(selectedCategory.getId(), selectedCategory);

				if (updatedCategory != null) {
					logger.info("Category updated successfully");
					showStatusMessage("Category updated successfully", false);

					// Log significant data modifications
					LoggingUtility.logAction(logger, "update",
							currentAdmin != null ? currentAdmin.getUsername() : "system", "category:" + name,
							"success");
				} else {
					logger.warn("Failed to update category: service returned null");
					showStatusMessage("Failed to update category", true);
				}
			} else {
				// Create new category
				Category newCategory = new Category();
				newCategory.setName(name);
				newCategory.setTranslation(translation);

				logger.info("Creating new category: {}", name);
				Category createdCategory = categoryService.addCategory(newCategory);

				if (createdCategory != null) {
					logger.info("Category created successfully with ID: {}", createdCategory.getId());
					showStatusMessage("Category created successfully", false);

					// Log significant data additions
					LoggingUtility.logAction(logger, "create",
							currentAdmin != null ? currentAdmin.getUsername() : "system", "category:" + name,
							"success");
				} else {
					logger.warn("Failed to create category: service returned null");
					showStatusMessage("Failed to create category", true);
				}
			}

			// Refresh the list and hide the form
			loadCategories();
			categoryEditForm.setVisible(false);

			// Notify parent view if available
			if (parentView != null) {
				parentView.refresh();
			}

		} catch (Exception e) {
			logger.error("Error saving category: {}", e.getMessage(), e);
			showStatusMessage("Error saving category: " + e.getMessage(), true);

			// Log errors
			LoggingUtility.logAction(logger, isEditMode ? "update" : "create",
					currentAdmin != null ? currentAdmin.getUsername() : "system", "category:" + name,
					"error: " + e.getMessage());
		}
	}

	private void deleteCategory(Category category) {
		if (category == null) {
			logger.warn("Cannot delete null category");
			return;
		}

		logger.info("Delete requested for category: {} (ID: {})", category.getName(), category.getId());

		if (showDeleteConfirmation("category", category.getName())) {
			long startTime = System.currentTimeMillis();
			try {
				// Delete all words associated with the category
				List<Word> words = wordService.getWordsByCategory(category.getId());
				logger.info("Deleting {} words associated with category", words.size());

				for (Word word : words) {
					wordService.deleteWord(word.getId());
				}

				// Delete the category
				logger.info("Deleting category with ID: {}", category.getId());
				boolean success = categoryService.deleteCategory(category.getId());

				if (success) {
					logger.info("Category deleted successfully");
					showStatusMessage("Category deleted successfully", false);
					loadCategories();
					if (parentView != null) {
						parentView.refresh();
					}

					// Log critical data deletions
					LoggingUtility.logAction(logger, "delete",
							currentAdmin != null ? currentAdmin.getUsername() : "system",
							"category:" + category.getName(), "success");
				} else {
					logger.warn("Failed to delete category: service returned false");
					showStatusMessage("Failed to delete category", true);
				}
			} catch (Exception e) {
				logger.error("Error deleting category: {}", e.getMessage(), e);
				showStatusMessage("Error deleting category: " + e.getMessage(), true);

				// Log errors
				LoggingUtility.logAction(logger, "delete", currentAdmin != null ? currentAdmin.getUsername() : "system",
						"category:" + category.getName(), "error: " + e.getMessage());
			}
		} else {
			logger.info("Category deletion cancelled by user");
		}
	}

	@FXML
	private void handleRefreshCategories() {
		logger.info("Refresh categories button clicked");
		loadCategories();
	}

	private void setupCategoryFilter() {
		// Set up the category filter dropdown
		ObservableList<String> categories = FXCollections.observableArrayList("All Categories");
		categories.addAll(categoryList.stream().map(Category::getName).toList());
		wordCategoryFilter.setItems(categories);
		wordCategoryFilter.setValue("All Categories");

		logger.debug("Category filter setup with {} categories", categoryList.size());
	}

	@FXML
	private void handleFilterWords() {
		String selectedCategory = wordCategoryFilter.getValue();
		logger.info("Filtering words by category: {}", selectedCategory);

		try {
			if ("All Categories".equals(selectedCategory)) {
				loadWords();
			} else {
				// Find the category by name
				Category category = categoryList.stream().filter(c -> c.getName().equals(selectedCategory)).findFirst()
						.orElse(null);

				if (category != null) {
					logger.info("Getting words for category ID: {}", category.getId());
					// Get words for this category
					List<Word> words = wordService.getWordsByCategory(category.getId());

					// Update the table
					wordsList.clear();
					wordsList.addAll(words);

					logger.info("Found {} words for category: {}", words.size(), selectedCategory);
					showStatusMessage("Found " + words.size() + " words for category: " + selectedCategory, false);
				} else {
					logger.warn("Selected category not found in category list: {}", selectedCategory);
					showStatusMessage("Category not found: " + selectedCategory, true);
				}
			}
		} catch (Exception e) {
			logger.error("Error filtering words: {}", e.getMessage(), e);
			showStatusMessage("Error filtering words: " + e.getMessage(), true);
		}
	}

	private void showWordEditForm(Word word) {
		if (word == null) {
			logger.warn("Cannot edit null word");
			return;
		}

		logger.info("Showing edit form for word: {} (ID: {})", word.getWord(), word.getId());

		wordEditForm.setVisible(true);
		wordEditForm.setManaged(true);
		jsonUploadForm.setVisible(false);
		jsonUploadForm.setManaged(false);

		// Prepare form for editing
		wordFormTitle.setText("Edit Word");
		wordTextField.setText(word.getWord());
		wordTranslationField.setText(word.getTranslatedText());

		// Set category
		for (Category category : wordCategoryComboBox.getItems()) {
			if (category.getId().equals(word.getCategory().getId())) {
				wordCategoryComboBox.setValue(category);
				break;
			}
		}

		// Set difficulty
		wordDifficultyComboBox.setValue(word.getDifficulty());

		selectedWord = word;
		isEditMode = true;

		// Show the form
		wordEditForm.setVisible(true);
	}

	@FXML
	private void handleCancelWordEdit() {
		logger.debug("Word edit cancelled");
		wordEditForm.setVisible(false);
	}

	@FXML
	private void handleSaveWord() {
		String wordText = wordTextField.getText().trim();
		String translation = wordTranslationField.getText().trim();
		Category category = wordCategoryComboBox.getValue();
		Difficulty difficulty = wordDifficultyComboBox.getValue();

		logger.info("Saving word: {} (mode: {})", wordText, isEditMode ? "edit" : "add");

		if (wordText.isEmpty()) {
			logger.warn("Word validation failed: text is empty");
			showStatusMessage("Word text cannot be empty", true);
			return;
		}

		if (category == null) {
			logger.warn("Word validation failed: no category selected");
			showStatusMessage("Please select a category", true);
			return;
		}

		if (difficulty == null) {
			logger.warn("Word validation failed: no difficulty selected");
			showStatusMessage("Please select a difficulty level", true);
			return;
		}

		try {
			if (isEditMode && selectedWord != null) {
				// Update existing word
				selectedWord.setWord(wordText);
				selectedWord.setTranslatedText(translation);
				selectedWord.setCategory(category);
				selectedWord.setDifficulty(difficulty);

				logger.info("Updating word with ID: {}", selectedWord.getId());
				// Save using service
				Word updatedWord = wordService.updateWord(selectedWord.getId(), selectedWord);

				if (updatedWord != null) {
					logger.info("Word updated successfully");
					showStatusMessage("Word updated successfully", false);

					// Log significant updates
					LoggingUtility.logAction(logger, "update",
							currentAdmin != null ? currentAdmin.getUsername() : "system", "word:" + wordText,
							"success");
				} else {
					logger.warn("Failed to update word: service returned null");
					showStatusMessage("Failed to update word", true);
				}
			} else {
				// Create new word
				Word newWord = new Word();
				newWord.setWord(wordText);
				newWord.setTranslatedText(translation);
				newWord.setCategory(category);
				newWord.setDifficulty(difficulty);

				// Set source and target language (default values)
				newWord.setSourceLanguage("en");
				newWord.setTargetLanguage("he");

				logger.info("Creating new word: {}", wordText);
				// Save using service
				Word createdWord = wordService.createWord(newWord);

				if (createdWord != null) {
					logger.info("Word created successfully with ID: {}", createdWord.getId());
					showStatusMessage("Word created successfully", false);

					// Log new content additions
					LoggingUtility.logAction(logger, "create",
							currentAdmin != null ? currentAdmin.getUsername() : "system", "word:" + wordText,
							"success");
				} else {
					logger.warn("Failed to create word: service returned null");
					showStatusMessage("Failed to create word", true);
				}
			}

			// Refresh the list and hide the form
			loadWords();
			wordEditForm.setVisible(false);

			// Notify parent view if available
			if (parentView != null) {
				parentView.refresh();
			}

		} catch (Exception e) {
			logger.error("Error saving word: {}", e.getMessage(), e);
			showStatusMessage("Error saving word: " + e.getMessage(), true);

			// Log errors
			LoggingUtility.logAction(logger, isEditMode ? "update" : "create",
					currentAdmin != null ? currentAdmin.getUsername() : "system", "word:" + wordText,
					"error: " + e.getMessage());
		}
	}

	private void deleteWord(Word word) {
		if (word == null) {
			logger.warn("Cannot delete null word");
			return;
		}

		logger.info("Delete requested for word: {} (ID: {})", word.getWord(), word.getId());

		if (showDeleteConfirmation("word", word.getWord())) {
			try {
				logger.info("Deleting word with ID: {}", word.getId());
				boolean success = wordService.deleteWord(word.getId());

				if (success) {
					logger.info("Word deleted successfully");
					showStatusMessage("Word deleted successfully", false);
					loadWords();
					if (parentView != null) {
						parentView.refresh();
					}

					// Log content deletion
					LoggingUtility.logAction(logger, "delete",
							currentAdmin != null ? currentAdmin.getUsername() : "system", "word:" + word.getWord(),
							"success");
				} else {
					logger.warn("Failed to delete word: service returned false");
					showStatusMessage("Failed to delete word", true);
				}
			} catch (DataIntegrityViolationException e) {
				logger.warn("Cannot delete word due to foreign key constraint: {}", e.getMessage(), e);
				showStatusMessage("Cannot delete word: it is referenced by other data.", true);

				// Log constraint violations
				LoggingUtility.logAction(logger, "delete", currentAdmin != null ? currentAdmin.getUsername() : "system",
						"word:" + word.getWord(), "error: constraint violation");
			} catch (Exception e) {
				logger.error("Error deleting word: {}", e.getMessage(), e);
				showStatusMessage("Error deleting word: " + e.getMessage(), true);

				// Log errors
				LoggingUtility.logAction(logger, "delete", currentAdmin != null ? currentAdmin.getUsername() : "system",
						"word:" + word.getWord(), "error: " + e.getMessage());
			}
		} else {
			logger.info("Word deletion cancelled by user");
		}
	}

	@FXML
	private void handleRefreshWords() {
		logger.info("Refresh words button clicked");
		loadWords();
	}

	private void showStatusMessage(String message, boolean isError) {
		statusLabel.setText(message);
		statusLabel.getStyleClass().removeAll("error-message", "success-message");
		statusLabel.getStyleClass().add(isError ? "error-message" : "success-message");
		statusLabel.setVisible(true);
	}

	private boolean showDeleteConfirmation(String itemType, String itemName) {
		logger.info("Showing delete confirmation for {}: {}", itemType, itemName);

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Delete Confirmation");
		alert.setHeaderText("Are you sure you want to delete this " + itemType + "?");
		alert.setContentText("Item: " + itemName);

		ButtonType confirmButton = new ButtonType("Delete");
		ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(confirmButton, cancelButton);

		Optional<ButtonType> result = alert.showAndWait();
		boolean confirmed = result.isPresent() && result.get() == confirmButton;

		logger.info("Delete confirmation result: {}", confirmed ? "confirmed" : "cancelled");
		return confirmed;
	}

	/**
	 * Shows the JSON upload form
	 */
	@FXML
	private void handleUploadWordsJson() {
		logger.info("Upload JSON button clicked");

		// Hide other forms
		jsonUploadForm.setVisible(true);
		jsonUploadForm.setManaged(true);
		wordEditForm.setVisible(false);
		wordEditForm.setManaged(false);
		categoryEditForm.setVisible(false);

		// Make sure the uploadCategoryComboBox has the same categories as the word
		// category combobox
		uploadCategoryComboBox.setItems(FXCollections.observableArrayList(categoryList));
		if (!categoryList.isEmpty()) {
			uploadCategoryComboBox.setValue(categoryList.get(0));
		}

		// Reset file selection
		selectedJsonFile = null;
		selectedFileTextField.setText("No file selected");
		uploadJsonButton.setDisable(true);

		// Show the upload form
		jsonUploadForm.setVisible(true);
	}

	/**
	 * Opens a file chooser to select a JSON file
	 */
	@FXML
	private void handleSelectJsonFile() {
		logger.info("Select JSON file button clicked");

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Words JSON File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("JSON Files", "*.json"));

		// Show the file chooser dialog
		File file = fileChooser.showOpenDialog(view.getScene().getWindow());

		if (file != null) {
			selectedJsonFile = file;
			selectedFileTextField.setText(file.getName());
			uploadJsonButton.setDisable(false);
			logger.info("JSON file selected: {}", file.getAbsolutePath());
		} else {
			logger.info("No file selected (file chooser cancelled)");
		}
	}

	/**
	 * Cancels the JSON upload and hides the form
	 */
	@FXML
	private void handleCancelJsonUpload() {
		logger.debug("JSON upload cancelled");
		jsonUploadForm.setVisible(false);
	}

	/**
	 * Uploads the selected JSON file to the server using WordService
	 */
	@FXML
	public void handleUploadJson() {
		if (selectedJsonFile == null || !selectedJsonFile.exists()) {
			logger.warn("Upload failed: No file selected or file does not exist");
			showStatusMessage("No file selected or file does not exist", true);
			return;
		}

		Category selectedCategory = uploadCategoryComboBox.getValue();
		if (selectedCategory == null) {
			logger.warn("Upload failed: No category selected");
			showStatusMessage("Please select a category", true);
			return;
		}

		logger.info("Uploading JSON file: {} to category ID: {}", selectedJsonFile.getName(), selectedCategory.getId());

		// Show loading status
		showStatusMessage("Uploading words...", false);

		// Upload in background thread to keep UI responsive
		Thread uploadThread = new Thread(() -> {
			try {
				// Use the updated WordService method to upload the JSON file
				String result = wordService.uploadWordsJson(selectedJsonFile, selectedCategory.getId());

				// Update UI on JavaFX thread
				Platform.runLater(() -> {
					if (result != null) {
						logger.info("Words uploaded successfully: {}", result);
						showStatusMessage("Words uploaded successfully: " + result, false);
						jsonUploadForm.setVisible(false);

						// Refresh the words list
						loadWords();

						// Log successful batch operation
						LoggingUtility.logAction(logger, "upload",
								currentAdmin != null ? currentAdmin.getUsername() : "system",
								"json_file:" + selectedJsonFile.getName(), "success: " + result);
					} else {
						logger.warn("Upload failed: Service returned null result");
						showStatusMessage("Upload failed. Please check the server logs for details.", true);
					}
				});
			} catch (Exception e) {
				Platform.runLater(() -> {
					logger.error("Error uploading words: {}", e.getMessage(), e);
					showStatusMessage("Error uploading words: " + e.getMessage(), true);

					// Log error for important batch operation
					LoggingUtility.logAction(logger, "upload",
							currentAdmin != null ? currentAdmin.getUsername() : "system",
							"json_file:" + selectedJsonFile.getName(), "error: " + e.getMessage());
				});
			}
		});

		// Start the background thread
		uploadThread.setDaemon(true);
		uploadThread.setName("WordsJsonUploader");
		uploadThread.start();
	}
}