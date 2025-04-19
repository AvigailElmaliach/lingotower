package com.lingotower.ui.controllers.admin;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	// Add a logger for this class
	private static final Logger logger = Logger.getLogger(ContentManagementController.class.getName());

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
		logger.log(Level.CONFIG, "ContentManagementController services initialized");
	}

	/**
	 * Sets the parent view reference
	 * 
	 * @param view The parent view
	 */
	public void setParentView(ContentManagementView view) {
		this.parentView = view;
		logger.log(Level.FINE, "Parent view set in ContentManagementController");
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
				logger.log(Level.WARNING, "Table Cell - Word object is null");
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

		logger.log(Level.INFO, "ContentManagementController UI initialized");
	}

	public void setAdmin(Admin admin) {
		this.currentAdmin = admin;
		logger.log(Level.INFO, "Admin set: {0}", (admin != null ? admin.getUsername() : "null"));
	}

	public void setReturnToDashboard(Runnable callback) {
		this.returnToDashboard = callback;
		logger.log(Level.FINE, "Return to dashboard callback set");
	}

	@FXML
	private void handleBackButton() {
		logger.log(Level.INFO, "Back button clicked");
		if (returnToDashboard != null) {
			returnToDashboard.run();
		} else {
			logger.log(Level.WARNING, "Return to dashboard callback is null");
		}
	}

	public void loadCategories() {
		try {
			// Show loading indicator or status message
			showStatusMessage("Loading categories...", false);
			logger.log(Level.INFO, "Loading categories...");

			// Clear existing list
			categoryList.clear();

			// Get categories from service
			List<Category> categories = categoryService.getAllCategories();

			// Check if we got valid data
			if (categories == null || categories.isEmpty()) {
				logger.log(Level.WARNING, "No categories found or unable to connect to server");
				showStatusMessage("No categories found or unable to connect to server", true);
				return;
			}

			// Add categories to the list
			categoryList.addAll(categories);

			// Update dropdowns and other UI elements
			updateCategoryComboBoxes();

			// Show success message
			logger.log(Level.INFO, "Loaded {0} categories successfully", categories.size());
			showStatusMessage("Loaded " + categories.size() + " categories successfully", false);

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error loading categories: {0}", e.getMessage());
			logger.log(Level.SEVERE, "Exception details:", e);
			showStatusMessage("Error loading categories: " + e.getMessage(), true);
		}
	}

	public void loadWords() {
		try {
			logger.log(Level.INFO, "Loading words...");
			showStatusMessage("Loading words...", false); // Don't log this in showStatusMessage

			wordsList.clear();
			List<Word> words = wordService.getAllWords();

			if (words == null || words.isEmpty()) {
				logger.log(Level.WARNING, "No words found");
				showStatusMessage("No words found", true);
				return;
			}

			wordsList.addAll(words);
			logger.log(Level.INFO, "Loaded {0} words successfully", words.size());
			showStatusMessage("Loaded " + words.size() + " words successfully", false);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error loading words: {0}", e.getMessage());
			logger.log(Level.SEVERE, "Exception details:", e);
			showStatusMessage("Error loading words: " + e.getMessage(), true);
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

		logger.log(Level.FINE, "Category combo boxes updated with {0} categories", categoryList.size());
	}

	@FXML
	private void handleAddCategory() {
		logger.log(Level.INFO, "Add category button clicked");

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
			logger.log(Level.WARNING, "Cannot edit null category");
			return;
		}

		logger.log(Level.INFO, "Showing edit form for category: {0} (ID: {1})",
				new Object[] { category.getName(), category.getId() });

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
		logger.log(Level.INFO, "Category edit cancelled");
		categoryEditForm.setVisible(false);
	}

	@FXML
	private void handleSaveCategory() {
		String name = categoryNameField.getText().trim();
		String translation = categoryTranslationField.getText().trim();

		logger.log(Level.INFO, "Saving category: {0} (mode: {1})", new Object[] { name, isEditMode ? "edit" : "add" });

		if (name.isEmpty()) {
			logger.log(Level.WARNING, "Category validation failed: name is empty");
			showStatusMessage("Category name cannot be empty", true);
			return;
		}

		try {
			if (isEditMode && selectedCategory != null) {
				// Update existing category
				selectedCategory.setName(name);
				selectedCategory.setTranslation(translation);

				logger.log(Level.INFO, "Updating category with ID: {0}", selectedCategory.getId());
				Category updatedCategory = categoryService.updateCategory(selectedCategory.getId(), selectedCategory);

				if (updatedCategory != null) {
					logger.log(Level.INFO, "Category updated successfully");
					showStatusMessage("Category updated successfully", false);
				} else {
					logger.log(Level.WARNING, "Failed to update category: service returned null");
					showStatusMessage("Failed to update category", true);
				}
			} else {
				// Create new category
				Category newCategory = new Category();
				newCategory.setName(name);
				newCategory.setTranslation(translation);

				logger.log(Level.INFO, "Creating new category: {0}", name);
				Category createdCategory = categoryService.addCategory(newCategory);

				if (createdCategory != null) {
					logger.log(Level.INFO, "Category created successfully with ID: {0}", createdCategory.getId());
					showStatusMessage("Category created successfully", false);
				} else {
					logger.log(Level.WARNING, "Failed to create category: service returned null");
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
			logger.log(Level.SEVERE, "Error saving category: {0}", e.getMessage());
			logger.log(Level.SEVERE, "Exception details:", e);
			showStatusMessage("Error saving category: " + e.getMessage(), true);
		}
	}

	private void deleteCategory(Category category) {
		if (category == null) {
			logger.log(Level.WARNING, "Cannot delete null category");
			return;
		}

		logger.log(Level.INFO, "Delete requested for category: {0} (ID: {1})",
				new Object[] { category.getName(), category.getId() });

		if (showDeleteConfirmation("category", category.getName())) {
			try {
				// Delete all words associated with the category
				List<Word> words = wordService.getWordsByCategory(category.getId());
				logger.log(Level.INFO, "Deleting {0} words associated with category", words.size());

				for (Word word : words) {
					wordService.deleteWord(word.getId());
				}

				// Delete the category
				logger.log(Level.INFO, "Deleting category with ID: {0}", category.getId());
				boolean success = categoryService.deleteCategory(category.getId());

				if (success) {
					logger.log(Level.INFO, "Category deleted successfully");
					showStatusMessage("Category deleted successfully", false);
					loadCategories();
					if (parentView != null) {
						parentView.refresh();
					}
				} else {
					logger.log(Level.WARNING, "Failed to delete category: service returned false");
					showStatusMessage("Failed to delete category", true);
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error deleting category: {0}", e.getMessage());
				logger.log(Level.SEVERE, "Exception details:", e);
				showStatusMessage("Error deleting category: " + e.getMessage(), true);
			}
		} else {
			logger.log(Level.INFO, "Category deletion cancelled by user");
		}
	}

	@FXML
	private void handleRefreshCategories() {
		logger.log(Level.INFO, "Refresh categories button clicked");
		loadCategories();
	}

	private void setupCategoryFilter() {
		// Set up the category filter dropdown
		ObservableList<String> categories = FXCollections.observableArrayList("All Categories");
		categories.addAll(categoryList.stream().map(Category::getName).toList());
		wordCategoryFilter.setItems(categories);
		wordCategoryFilter.setValue("All Categories");

		logger.log(Level.FINE, "Category filter setup with {0} categories", categoryList.size());
	}

	@FXML
	private void handleFilterWords() {
		String selectedCategory = wordCategoryFilter.getValue();
		logger.log(Level.INFO, "Filtering words by category: {0}", selectedCategory);

		try {
			if ("All Categories".equals(selectedCategory)) {
				loadWords();
			} else {
				// Find the category by name
				Category category = categoryList.stream().filter(c -> c.getName().equals(selectedCategory)).findFirst()
						.orElse(null);

				if (category != null) {
					logger.log(Level.INFO, "Getting words for category ID: {0}", category.getId());
					// Get words for this category
					List<Word> words = wordService.getWordsByCategory(category.getId());

					// Update the table
					wordsList.clear();
					wordsList.addAll(words);

					logger.log(Level.INFO, "Found {0} words for category: {1}",
							new Object[] { words.size(), selectedCategory });
				} else {
					logger.log(Level.WARNING, "Selected category not found in category list: {0}", selectedCategory);
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error filtering words: {0}", e.getMessage());
			logger.log(Level.SEVERE, "Exception details:", e);
			showStatusMessage("Error filtering words: " + e.getMessage(), true);
		}
	}

	private void showWordEditForm(Word word) {
		if (word == null) {
			logger.log(Level.WARNING, "Cannot edit null word");
			return;
		}

		logger.log(Level.INFO, "Showing edit form for word: {0} (ID: {1})",
				new Object[] { word.getWord(), word.getId() });

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
		logger.log(Level.INFO, "Word edit cancelled");
		wordEditForm.setVisible(false);
	}

	@FXML
	private void handleSaveWord() {
		String wordText = wordTextField.getText().trim();
		String translation = wordTranslationField.getText().trim();
		Category category = wordCategoryComboBox.getValue();
		Difficulty difficulty = wordDifficultyComboBox.getValue();

		logger.log(Level.INFO, "Saving word: {0} (mode: {1})", new Object[] { wordText, isEditMode ? "edit" : "add" });

		if (wordText.isEmpty()) {
			logger.log(Level.WARNING, "Word validation failed: text is empty");
			showStatusMessage("Word text cannot be empty", true);
			return;
		}

		if (category == null) {
			logger.log(Level.WARNING, "Word validation failed: no category selected");
			showStatusMessage("Please select a category", true);
			return;
		}

		if (difficulty == null) {
			logger.log(Level.WARNING, "Word validation failed: no difficulty selected");
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

				logger.log(Level.INFO, "Updating word with ID: {0}", selectedWord.getId());
				// Save using service
				Word updatedWord = wordService.updateWord(selectedWord.getId(), selectedWord);

				if (updatedWord != null) {
					logger.log(Level.INFO, "Word updated successfully");
					showStatusMessage("Word updated successfully", false);
				} else {
					logger.log(Level.WARNING, "Failed to update word: service returned null");
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

				logger.log(Level.INFO, "Creating new word: {0}", wordText);
				// Save using service
				Word createdWord = wordService.createWord(newWord);

				if (createdWord != null) {
					logger.log(Level.INFO, "Word created successfully with ID: {0}", createdWord.getId());
					showStatusMessage("Word created successfully", false);
				} else {
					logger.log(Level.WARNING, "Failed to create word: service returned null");
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
			logger.log(Level.SEVERE, "Error saving word: {0}", e.getMessage());
			logger.log(Level.SEVERE, "Exception details:", e);
			showStatusMessage("Error saving word: " + e.getMessage(), true);
		}
	}

	private void deleteWord(Word word) {
		if (word == null) {
			logger.log(Level.WARNING, "Cannot delete null word");
			return;
		}

		logger.log(Level.INFO, "Delete requested for word: {0} (ID: {1})",
				new Object[] { word.getWord(), word.getId() });

		if (showDeleteConfirmation("word", word.getWord())) {
			try {
				logger.log(Level.INFO, "Deleting word with ID: {0}", word.getId());
				boolean success = wordService.deleteWord(word.getId());

				if (success) {
					logger.log(Level.INFO, "Word deleted successfully");
					showStatusMessage("Word deleted successfully", false);
					loadWords();
					if (parentView != null) {
						parentView.refresh();
					}
				} else {
					logger.log(Level.WARNING, "Failed to delete word: service returned false");
					showStatusMessage("Failed to delete word", true);
				}
			} catch (DataIntegrityViolationException e) {
				logger.log(Level.WARNING, "Cannot delete word due to foreign key constraint: {0}", e.getMessage());
				logger.log(Level.FINE, "Constraint violation details:", e);
				showStatusMessage("Cannot delete word: it is referenced by other data.", true);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error deleting word: {0}", e.getMessage());
				logger.log(Level.SEVERE, "Exception details:", e);
				showStatusMessage("Error deleting word: " + e.getMessage(), true);
			}
		} else {
			logger.log(Level.INFO, "Word deletion cancelled by user");
		}
	}

	@FXML
	private void handleRefreshWords() {
		logger.log(Level.INFO, "Refresh words button clicked");
		loadWords();
	}

	private void showStatusMessage(String message, boolean isError) {
//		logger.log(isError ? Level.WARNING : Level.INFO, "Status message: {0}", message);

		statusLabel.setText(message);
		statusLabel.getStyleClass().removeAll("error-message", "success-message");
		statusLabel.getStyleClass().add(isError ? "error-message" : "success-message");
		statusLabel.setVisible(true);
	}

	private boolean showDeleteConfirmation(String itemType, String itemName) {
		logger.log(Level.INFO, "Showing delete confirmation for {0}: {1}", new Object[] { itemType, itemName });

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Delete Confirmation");
		alert.setHeaderText("Are you sure you want to delete this " + itemType + "?");
		alert.setContentText("Item: " + itemName);

		ButtonType confirmButton = new ButtonType("Delete");
		ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(confirmButton, cancelButton);

		Optional<ButtonType> result = alert.showAndWait();
		boolean confirmed = result.isPresent() && result.get() == confirmButton;

		logger.log(Level.INFO, "Delete confirmation result: {0}", confirmed ? "confirmed" : "cancelled");
		return confirmed;
	}

	/**
	 * Shows the JSON upload form
	 */
	@FXML
	private void handleUploadWordsJson() {
		logger.log(Level.INFO, "Upload JSON button clicked");

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
		logger.log(Level.INFO, "Select JSON file button clicked");

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Words JSON File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("JSON Files", "*.json"));

		// Show the file chooser dialog
		File file = fileChooser.showOpenDialog(view.getScene().getWindow());

		if (file != null) {
			selectedJsonFile = file;
			selectedFileTextField.setText(file.getName());
			uploadJsonButton.setDisable(false);
			logger.log(Level.INFO, "JSON file selected: {0}", file.getAbsolutePath());
		} else {
			logger.log(Level.INFO, "No file selected (file chooser cancelled)");
		}
	}

	/**
	 * Cancels the JSON upload and hides the form
	 */
	@FXML
	private void handleCancelJsonUpload() {
		logger.log(Level.INFO, "JSON upload cancelled");
		jsonUploadForm.setVisible(false);
	}

	/**
	 * Uploads the selected JSON file to the server using WordService
	 */
	@FXML
	public void handleUploadJson() {
		if (selectedJsonFile == null || !selectedJsonFile.exists()) {
			logger.log(Level.WARNING, "Upload failed: No file selected or file does not exist");
			showStatusMessage("No file selected or file does not exist", true);
			return;
		}

		Category selectedCategory = uploadCategoryComboBox.getValue();
		if (selectedCategory == null) {
			logger.log(Level.WARNING, "Upload failed: No category selected");
			showStatusMessage("Please select a category", true);
			return;
		}

		logger.log(Level.INFO, "Uploading JSON file: {0} to category ID: {1}",
				new Object[] { selectedJsonFile.getName(), selectedCategory.getId() });

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
						logger.log(Level.INFO, "Words uploaded successfully: {0}", result);
						showStatusMessage("Words uploaded successfully: " + result, false);
						jsonUploadForm.setVisible(false);

						// Refresh the words list
						loadWords();
					} else {
						logger.log(Level.WARNING, "Upload failed: Service returned null result");
						showStatusMessage("Upload failed. Please check the server logs for details.", true);
					}
				});
			} catch (Exception e) {
				Platform.runLater(() -> {
					logger.log(Level.SEVERE, "Error uploading words: {0}", e.getMessage());
					logger.log(Level.SEVERE, "Exception details:", e);
					showStatusMessage("Error uploading words: " + e.getMessage(), true);
				});
			}
		});

		// Start the background thread
		uploadThread.setDaemon(true);
		uploadThread.setName("WordsJsonUploader");
		uploadThread.start();
	}
}