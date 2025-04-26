package com.lingotower.ui.quiz;

import static com.lingotower.constants.QuizConstants.DIFFICULTIES;
import static com.lingotower.constants.QuizConstants.FALLBACK_CATEGORIES;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.lingotower.model.Category;
import com.lingotower.model.Quiz;
import com.lingotower.service.CategoryService;
import com.lingotower.utils.LoggingUtility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

/**
 * Manages the list of quizzes, including loading categories, generating sample
 * quizzes, and handling filtering.
 */
public class QuizListManager {
	private static final Logger logger = LoggingUtility.getLogger(QuizListManager.class);

	// UI Components
	private final ListView<Quiz> quizListView;
	private final ComboBox<String> difficultyComboBox;
	private final ComboBox<String> categoryComboBox;

	// Services
	private final CategoryService categoryService;
	private final QuizGenerator quizGenerator;

	// State
	private Quiz selectedQuiz;

	/**
	 * Constructs a new QuizListManager.
	 * 
	 * @param quizListView       The list view for quizzes
	 * @param difficultyComboBox The difficulty combo box
	 * @param categoryComboBox   The category combo box
	 * @param categoryService    The category service
	 * @param quizGenerator      The quiz generator
	 */
	public QuizListManager(ListView<Quiz> quizListView, ComboBox<String> difficultyComboBox,
			ComboBox<String> categoryComboBox, CategoryService categoryService, QuizGenerator quizGenerator) {
		this.quizListView = quizListView;
		this.difficultyComboBox = difficultyComboBox;
		this.categoryComboBox = categoryComboBox;
		this.categoryService = categoryService;
		this.quizGenerator = quizGenerator;
	}

	/**
	 * Initializes the quiz list components.
	 */
	public void initialize() {
		// Set up difficulty dropdown
		difficultyComboBox.setItems(DIFFICULTIES);
		difficultyComboBox.setValue("EASY");

		// Load categories
		loadCategories();

		// Set up quiz list view
		setupQuizListView();

		// Generate sample quizzes
		generateSampleQuizzes();
	}

	/**
	 * Sets up the quiz list view cell factory.
	 */
	private void setupQuizListView() {
		quizListView.setCellFactory(lv -> new ListCell<Quiz>() {
			@Override
			protected void updateItem(Quiz quiz, boolean empty) {
				super.updateItem(quiz, empty);
				if (empty || quiz == null) {
					setText(null);
				} else {
					setText(quiz.getName());
				}
			}
		});
	}

	/**
	 * Loads available categories from the service.
	 */
	private void loadCategories() {
		try {
			List<Category> categories = categoryService.getAllCategories();

			if (categories != null && !categories.isEmpty()) {
				ObservableList<String> categoryNames = FXCollections.observableArrayList();

				for (Category category : categories) {
					// Only add category name if it's not null or empty
					if (category != null && category.getName() != null && !category.getName().trim().isEmpty()) {
						categoryNames.add(category.getName());
					} else {
						logger.warn("Skipping category with null or empty name from service.");
					}
				}

				if (!categoryNames.isEmpty()) {
					categoryComboBox.setItems(categoryNames);
					categoryComboBox.setValue(categoryNames.get(0));
				} else {
					// If no valid names were found, use fallback
					logger.warn("No valid category names retrieved from service, using fallback categories.");
					categoryComboBox.setItems(FALLBACK_CATEGORIES);
					categoryComboBox.setValue(FALLBACK_CATEGORIES.get(0));
				}
			} else {
				logger.warn("No categories retrieved from service, using fallback categories.");
				categoryComboBox.setItems(FALLBACK_CATEGORIES);
				categoryComboBox.setValue(FALLBACK_CATEGORIES.get(0));
			}
		} catch (Exception e) {
			logger.error("Error loading categories: {}", e.getMessage(), e);
			// Fallback categories
			categoryComboBox.setItems(FALLBACK_CATEGORIES);
			categoryComboBox.setValue(FALLBACK_CATEGORIES.get(0));
		}
	}

	/**
	 * Generates sample quizzes for selection.
	 */
	private void generateSampleQuizzes() {
		// Clear existing quizzes first to prevent duplicates
		ObservableList<Quiz> sampleQuizzes = FXCollections.observableArrayList();

		// Get categories and difficulties, filtering out null/empty category names
		List<String> categories = new ArrayList<>(categoryComboBox.getItems()).stream()
				.filter(name -> name != null && !name.trim().isEmpty()).collect(Collectors.toList());

		List<String> difficulties = new ArrayList<>(difficultyComboBox.getItems());

		// For each category, create both regular quizzes and sentence completion
		// quizzes
		for (String categoryName : categories) {
			for (String difficultyName : difficulties) {
				// Create regular vocabulary quiz
				sampleQuizzes.add(quizGenerator.createSampleQuiz(categoryName, difficultyName, false));

				// Create sentence completion quiz
				sampleQuizzes.add(quizGenerator.createSampleQuiz(categoryName, difficultyName, true));
			}
		}

		// Set the items to the new list
		quizListView.setItems(sampleQuizzes);
	}

	/**
	 * Applies filter and updates the quiz list.
	 */
	public void applyFilter() {
		String selectedDifficulty = difficultyComboBox.getValue();
		String selectedCategory = categoryComboBox.getValue();

		logger.debug("Filter applied - difficulty: {}, category: {}", selectedDifficulty, selectedCategory);

		// Filter existing quizzes
		ObservableList<Quiz> filteredQuizzes = FXCollections.observableArrayList();

		// Get all quizzes
		ObservableList<Quiz> allQuizzes = quizListView.getItems();

		// Filter by category and difficulty
		for (Quiz quiz : allQuizzes) {
			boolean matchesCategory = quiz.getCategory().getName().equals(selectedCategory);
			boolean matchesDifficulty = quiz.getDifficulty().toString().equals(selectedDifficulty);

			if (matchesCategory && matchesDifficulty) {
				filteredQuizzes.add(quiz);
			}
		}

		// If no matches, create new filtered quizzes
		if (filteredQuizzes.isEmpty()) {
			// Create regular quiz with selected criteria
			filteredQuizzes.add(quizGenerator.createSampleQuiz(selectedCategory, selectedDifficulty, false));

			// Create sentence completion quiz
			filteredQuizzes.add(quizGenerator.createSampleQuiz(selectedCategory, selectedDifficulty, true));
		}

		// Update the ListView
		quizListView.setItems(filteredQuizzes);
	}

	/**
	 * Gets the currently selected quiz.
	 * 
	 * @return The selected quiz or null if none selected
	 */
	public Quiz getSelectedQuiz() {
		return quizListView.getSelectionModel().getSelectedItem();
	}

	/**
	 * Sets up a selection listener for the quiz list.
	 * 
	 * @param onQuizSelected Callback when a quiz is selected
	 */
	public void setSelectionListener(QuizSelectionHandler onQuizSelected) {
		quizListView.getSelectionModel().selectedItemProperty().addListener((obs, oldQuiz, newQuiz) -> {
			if (newQuiz != null) {
				selectedQuiz = newQuiz;
				onQuizSelected.onQuizSelected(newQuiz);
			}
		});
	}

	/**
	 * Callback interface for quiz selection events.
	 */
	public interface QuizSelectionHandler {
		void onQuizSelected(Quiz quiz);
	}
}