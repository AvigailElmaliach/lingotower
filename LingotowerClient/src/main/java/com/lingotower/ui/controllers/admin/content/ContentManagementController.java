package com.lingotower.ui.controllers.admin.content;

import org.slf4j.Logger;

import com.lingotower.model.Admin;
import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;
import com.lingotower.model.Word;
import com.lingotower.service.AdminService;
import com.lingotower.service.CategoryService;
import com.lingotower.service.WordService;
import com.lingotower.ui.components.ActionButtonCell;
import com.lingotower.ui.controllers.admin.content.CategoryHandler;
import com.lingotower.ui.controllers.admin.content.UploadHandler;
import com.lingotower.ui.controllers.admin.content.WordHandler;
import com.lingotower.ui.views.admin.ContentManagementView;
import com.lingotower.utils.LoggingUtility;
import com.lingotower.utils.ui.UIUtils;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class ContentManagementController {
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

	// Upload form controls
	@FXML
	private VBox jsonUploadForm;
	@FXML
	private TextField selectedFileTextField;
	@FXML
	private ComboBox<Category> uploadCategoryComboBox;
	@FXML
	private Button uploadJsonButton;

	// Services and data
	private CategoryService categoryService;
	private WordService wordService;
	private AdminService adminService;
	private ObservableList<Category> categoryList = FXCollections.observableArrayList();
	private ObservableList<Word> wordsList = FXCollections.observableArrayList();

	// Controller state
	private Admin currentAdmin;
	private Runnable returnToDashboard;
	private ContentManagementView parentView;

	// Handlers
	private CategoryHandler categoryHandler;
	private WordHandler wordHandler;
	private UploadHandler uploadHandler;

	public ContentManagementController() {
		// Initialize services
		categoryService = new CategoryService();
		wordService = new WordService();
		adminService = new AdminService();
		logger.debug("ContentManagementController services initialized");
	}

	/**
	 * Sets the parent view reference
	 */
	public void setParentView(ContentManagementView view) {
		this.parentView = view;
		logger.debug("Parent view set in ContentManagementController");
	}

	@FXML
	public void initialize() {
		// Initialize handlers
		categoryHandler = new CategoryHandler(this, categoryService);
		wordHandler = new WordHandler(this, wordService);
		uploadHandler = new UploadHandler(this, wordService);

		setupCategoryTable();
		setupWordTable();
		setupComboBoxes();

		// Set tables to use observable lists
		categoryTableView.setItems(categoryList);
		wordTableView.setItems(wordsList);

		// Load initial data
		categoryHandler.loadCategories();
		wordHandler.loadWords();

		logger.info("ContentManagementController UI initialized");
	}

	private void setupCategoryTable() {
		// Initialize category table columns
		categoryIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		categoryTranslationColumn.setCellValueFactory(new PropertyValueFactory<>("translation"));
		categoryActionsColumn.setCellFactory(
				col -> new ActionButtonCell<>(event -> categoryHandler.showEditForm((Category) event.getSource()),
						event -> categoryHandler.deleteCategory((Category) event.getSource())));
	}

	private void setupWordTable() {
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
		wordActionsColumn.setCellFactory(
				col -> new ActionButtonCell<>(event -> wordHandler.showEditForm((Word) event.getSource()),
						event -> wordHandler.deleteWord((Word) event.getSource())));
	}

	private void setupComboBoxes() {
		// Initialize difficulty combo box for word form
		wordDifficultyComboBox.getItems().addAll(Difficulty.values());

		// Initialize category filter
		wordCategoryFilter.setItems(FXCollections.observableArrayList("All Categories"));
		wordCategoryFilter.setValue("All Categories");

		// Hide forms initially
		if (jsonUploadForm != null) {
			jsonUploadForm.setVisible(false);
		}
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
			LoggingUtility.logAction(logger, "navigation", currentAdmin != null ? currentAdmin.getUsername() : "system",
					"dashboard", "success");
		} else {
			logger.warn("Return to dashboard callback is null");
		}
	}

	// Category methods
	@FXML
	private void handleAddCategory() {
		categoryHandler.resetCategoryForm();
		categoryEditForm.setVisible(true);
	}

	@FXML
	private void handleCancelCategoryEdit() {
		categoryEditForm.setVisible(false);
	}

	@FXML
	private void handleSaveCategory() {
		categoryHandler.saveCategory(categoryNameField.getText().trim(), categoryTranslationField.getText().trim());
	}

	@FXML
	private void handleRefreshCategories() {
		categoryHandler.loadCategories();
	}

	// Word methods
	@FXML
	private void handleFilterWords() {
		String selectedCategory = wordCategoryFilter.getValue();
		wordHandler.filterWordsByCategory(selectedCategory);
	}

	@FXML
	private void handleCancelWordEdit() {
		wordEditForm.setVisible(false);
	}

	@FXML
	private void handleSaveWord() {
		wordHandler.saveWord(wordTextField.getText().trim(), wordTranslationField.getText().trim(),
				wordCategoryComboBox.getValue(), wordDifficultyComboBox.getValue());
	}

	@FXML
	private void handleRefreshWords() {
		wordHandler.loadWords();
	}

	// Upload methods
	@FXML
	private void handleUploadWordsJson() {
		uploadHandler.showUploadForm();
	}

	@FXML
	private void handleSelectJsonFile() {
		uploadHandler.selectJsonFile(view.getScene().getWindow());
	}

	@FXML
	private void handleCancelJsonUpload() {
		jsonUploadForm.setVisible(false);
	}

	@FXML
	public void handleUploadJson() {
		uploadHandler.uploadJson();
	}

	// Getters for UI components and data
	public Admin getCurrentAdmin() {
		return currentAdmin;
	}

	public ObservableList<Category> getCategoryList() {
		return categoryList;
	}

	public ObservableList<Word> getWordsList() {
		return wordsList;
	}

	public Label getStatusLabel() {
		return statusLabel;
	}

	public ContentManagementView getParentView() {
		return parentView;
	}

	public VBox getCategoryEditForm() {
		return categoryEditForm;
	}

	public Label getCategoryFormTitle() {
		return categoryFormTitle;
	}

	public ComboBox<String> getWordCategoryFilter() {
		return wordCategoryFilter;
	}

	public ComboBox<Category> getWordCategoryComboBox() {
		return wordCategoryComboBox;
	}

	public VBox getWordEditForm() {
		return wordEditForm;
	}

	public Label getWordFormTitle() {
		return wordFormTitle;
	}

	public VBox getJsonUploadForm() {
		return jsonUploadForm;
	}

	public TextField getSelectedFileTextField() {
		return selectedFileTextField;
	}

	public ComboBox<Category> getUploadCategoryComboBox() {
		return uploadCategoryComboBox;
	}

	public Button getUploadJsonButton() {
		return uploadJsonButton;
	}

	public void updateCategoryComboBoxes() {
		ObservableList<String> filterItems = FXCollections.observableArrayList("All Categories");
		filterItems.addAll(categoryList.stream().map(Category::getName).toList());
		wordCategoryFilter.setItems(filterItems);
		wordCategoryFilter.setValue("All Categories");

		wordCategoryComboBox.setItems(FXCollections.observableArrayList(categoryList));
		uploadCategoryComboBox.setItems(FXCollections.observableArrayList(categoryList));

		logger.debug("Category combo boxes updated with {} categories", categoryList.size());
	}

	public void showStatusMessage(String message, boolean isError) {
		UIUtils.showStatusMessage(statusLabel, message, isError);
	}
}