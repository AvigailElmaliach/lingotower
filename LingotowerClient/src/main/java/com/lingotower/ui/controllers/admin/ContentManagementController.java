package com.lingotower.ui.controllers.admin;

import java.util.List;

import com.lingotower.model.Admin;
import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;
import com.lingotower.model.Word;
import com.lingotower.service.CategoryService;
import com.lingotower.service.WordService;
import com.lingotower.ui.components.ActionButtonCell;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class ContentManagementController {

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

	private Admin currentAdmin;
	private Runnable returnToDashboard;

	private CategoryService categoryService;
	private WordService wordService;

	private ObservableList<Category> categoryList = FXCollections.observableArrayList();
	private ObservableList<Word> wordsList = FXCollections.observableArrayList();

	private Category selectedCategory;
	private Word selectedWord;
	private boolean isEditMode = false;

	public ContentManagementController() {
		// Initialize services
		categoryService = new CategoryService();
		wordService = new WordService();
	}

	@FXML
	public void initialize() {
		// Initialize category table columns
		categoryIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		categoryTranslationColumn.setCellValueFactory(new PropertyValueFactory<>("translation"));

		// Setup category actions column using ActionButtonCell
		// ****************************************************
		// החלפת הקוד הקודם בשימוש ב-ActionButtonCell
		// ****************************************************
		categoryActionsColumn.setCellFactory(col -> new ActionButtonCell<>(event -> { // Edit handler
			Category category = (Category) event.getSource(); // קבלת האובייקט מהאירוע
			showCategoryEditForm(category);
		}, event -> { // Delete handler
			Category category = (Category) event.getSource(); // קבלת האובייקט מהאירוע
			deleteCategory(category);
		}));

		// Initialize word table columns
		wordIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		wordTextColumn.setCellValueFactory(new PropertyValueFactory<>("word"));
		wordTranslationColumn.setCellValueFactory(cellData -> {
			Word word = cellData.getValue();
			if (word == null) {
				System.out.println("Table Cell - Word object is null");
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
		// ****************************************************
		// החלפת הקוד הקודם בשימוש ב-ActionButtonCell
		// ****************************************************
		wordActionsColumn.setCellFactory(col -> new ActionButtonCell<>(event -> { // Edit handler
			Word word = (Word) event.getSource(); // קבלת האובייקט מהאירוע
			showWordEditForm(word);
		}, event -> { // Delete handler
			Word word = (Word) event.getSource(); // קבלת האובייקט מהאירוע
			deleteWord(word);
		}));

		// Initialize difficulty combo box
		wordDifficultyComboBox.getItems().addAll(Difficulty.values());

		// Set tables to use our observable lists
		categoryTableView.setItems(categoryList);
		wordTableView.setItems(wordsList);

		// Load initial data
		loadCategories();
		setupCategoryFilter();
		loadWords();
	}

	public void setAdmin(Admin admin) {
		this.currentAdmin = admin;
	}

	public void setReturnToDashboard(Runnable callback) {
		this.returnToDashboard = callback;
	}

	@FXML
	private void handleBackButton() {
		if (returnToDashboard != null) {
			returnToDashboard.run();
		}
	}

	private void loadCategories() {
		try {
			// Show loading indicator or status message
			showStatusMessage("Loading categories...", false);

			// Clear existing list
			categoryList.clear();

			// Get categories from service
			List<Category> categories = categoryService.getAllCategories();

			// Check if we got valid data
			if (categories == null || categories.isEmpty()) {
				showStatusMessage("No categories found or unable to connect to server", true);
				return;
			}

			// Add categories to the list
			categoryList.addAll(categories);

			// Update dropdowns and other UI elements
			updateCategoryComboBoxes();

			// Show success message
			showStatusMessage("Loaded " + categories.size() + " categories successfully", false);

		} catch (Exception e) {
			System.err.println("Error loading categories: " + e.getMessage());
			e.printStackTrace();
			showStatusMessage("Error loading categories: " + e.getMessage(), true);
		}
	}

	private void loadWords() {
		try {
			// Show loading indicator or status message
			showStatusMessage("Loading words...", false);

			// Clear existing list
			wordsList.clear();

			// Get words from service
			List<Word> words = wordService.getAllWords();

			// Check if we got valid data
			if (words == null || words.isEmpty()) {
				showStatusMessage("No words found or unable to connect to server", true);
				return;
			}

			// Add words to the list
			wordsList.addAll(words);

			// Show success message
			showStatusMessage("Loaded " + words.size() + " words successfully", false);

		} catch (Exception e) {
			System.err.println("Error loading words: " + e.getMessage());
			e.printStackTrace();
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
	}

	@FXML
	private void handleAddCategory() {
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
		categoryEditForm.setVisible(false);
	}

	@FXML
	private void handleSaveCategory() {
		String name = categoryNameField.getText().trim();
		String translation = categoryTranslationField.getText().trim();

		if (name.isEmpty()) {
			showStatusMessage("Category name cannot be empty", true);
			return;
		}

		try {
			if (isEditMode && selectedCategory != null) {
				// Update existing category
				selectedCategory.setName(name);
				selectedCategory.setTranslation(translation);
				Category updatedCategory = categoryService.updateCategory(selectedCategory.getId(), selectedCategory);

				if (updatedCategory != null) {
					showStatusMessage("Category updated successfully", false);
				} else {
					showStatusMessage("Failed to update category", true);
				}
			} else {
				// Create new category
				Category newCategory = new Category();
				newCategory.setName(name);
				newCategory.setTranslation(translation);

				Category createdCategory = categoryService.addCategory(newCategory);

				if (createdCategory != null) {
					showStatusMessage("Category created successfully", false);
				} else {
					showStatusMessage("Failed to create category", true);
				}
			}

			// Refresh the list and hide the form
			loadCategories();
			categoryEditForm.setVisible(false);

		} catch (Exception e) {
			System.err.println("Error saving category: " + e.getMessage());
			e.printStackTrace();
			showStatusMessage("Error saving category: " + e.getMessage(), true);
		}
	}

	private void deleteCategory(Category category) {
		try {
			boolean success = categoryService.deleteCategory(category.getId());

			if (success) {
				showStatusMessage("Category deleted successfully", false);
				loadCategories();
			} else {
				showStatusMessage("Failed to delete category", true);
			}
		} catch (Exception e) {
			System.err.println("Error deleting category: " + e.getMessage());
			e.printStackTrace();
			showStatusMessage("Error deleting category: " + e.getMessage(), true);
		}
	}

	@FXML
	private void handleRefreshCategories() {
		loadCategories();
	}

	private void setupCategoryFilter() {
		// Set up the category filter dropdown
		ObservableList<String> categories = FXCollections.observableArrayList("All Categories");
		categories.addAll(categoryList.stream().map(Category::getName).toList());
		wordCategoryFilter.setItems(categories);
		wordCategoryFilter.setValue("All Categories");
	}

	@FXML
	private void handleFilterWords() {
		String selectedCategory = wordCategoryFilter.getValue();

		try {
			if ("All Categories".equals(selectedCategory)) {
				loadWords();
			} else {
				// Find the category by name
				Category category = categoryList.stream().filter(c -> c.getName().equals(selectedCategory)).findFirst()
						.orElse(null);

				if (category != null) {
					// Get words for this category
					List<Word> words = wordService.getWordsByCategory(category.getId());

					// Update the table
					wordsList.clear();
					wordsList.addAll(words);
				}
			}
		} catch (Exception e) {
			System.err.println("Error filtering words: " + e.getMessage());
			e.printStackTrace();
			showStatusMessage("Error filtering words: " + e.getMessage(), true);
		}
	}

	@FXML
	private void handleAddWord() {
		// Reset form for new word
		wordFormTitle.setText("Add New Word");
		wordTextField.setText("");
		wordTranslationField.setText("");

		if (!wordCategoryComboBox.getItems().isEmpty()) {
			wordCategoryComboBox.setValue(wordCategoryComboBox.getItems().get(0));
		}

		wordDifficultyComboBox.setValue(Difficulty.EASY);
		selectedWord = null;
		isEditMode = false;

		// Show the form
		wordEditForm.setVisible(true);
	}

	private void showWordEditForm(Word word) {
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
		wordEditForm.setVisible(false);
	}

	@FXML
	private void handleSaveWord() {
		String wordText = wordTextField.getText().trim();
		String translation = wordTranslationField.getText().trim();
		Category category = wordCategoryComboBox.getValue();
		Difficulty difficulty = wordDifficultyComboBox.getValue();

		if (wordText.isEmpty()) {
			showStatusMessage("Word text cannot be empty", true);
			return;
		}

		if (category == null) {
			showStatusMessage("Please select a category", true);
			return;
		}

		if (difficulty == null) {
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

				// Save using service
				Word updatedWord = wordService.updateWord(selectedWord.getId(), selectedWord);

				if (updatedWord != null) {
					showStatusMessage("Word updated successfully", false);
				} else {
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

				// Save using service
				Word createdWord = wordService.createWord(newWord);

				if (createdWord != null) {
					showStatusMessage("Word created successfully", false);
				} else {
					showStatusMessage("Failed to create word", true);
				}
			}

			// Refresh the list and hide the form
			loadWords();
			wordEditForm.setVisible(false);

		} catch (Exception e) {
			System.err.println("Error saving word: " + e.getMessage());
			e.printStackTrace();
			showStatusMessage("Error saving word: " + e.getMessage(), true);
		}
	}

	private void deleteWord(Word word) {
		try {
			boolean success = wordService.deleteWord(word.getId());

			if (success) {
				showStatusMessage("Word deleted successfully", false);
				loadWords();
			} else {
				showStatusMessage("Failed to delete word", true);
			}
		} catch (Exception e) {
			System.err.println("Error deleting word: " + e.getMessage());
			e.printStackTrace();
			showStatusMessage("Error deleting word: " + e.getMessage(), true);
		}
	}

	@FXML
	private void handleRefreshWords() {
		loadWords();
	}

	private void showStatusMessage(String message, boolean isError) {
		statusLabel.setText(message);
		statusLabel.getStyleClass().removeAll("error-message", "success-message");
		statusLabel.getStyleClass().add(isError ? "error-message" : "success-message");
		statusLabel.setVisible(true);
	}
}