package com.lingotower.ui.controllers;

import java.util.ArrayList;
import java.util.List;

import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;
import com.lingotower.model.Question;
import com.lingotower.model.Quiz;
import com.lingotower.service.CategoryService;
import com.lingotower.service.QuizService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class QuizController {

	@FXML
	private BorderPane view;
	@FXML
	private ListView<Quiz> quizListView;
	@FXML
	private ComboBox<String> difficultyComboBox;
	@FXML
	private ComboBox<String> categoryComboBox;
	@FXML
	private javafx.scene.control.ProgressBar progressBar;

	// Different content containers
	@FXML
	private VBox welcomeContent;
	@FXML
	private VBox previewContent;
	@FXML
	private VBox questionContent;
	@FXML
	private VBox summaryContent; // Summaries/score display

	// Quiz preview elements
	@FXML
	private Label quizNameLabel;
	@FXML
	private Label categoryLabel;
	@FXML
	private Label difficultyLabel;
	@FXML
	private Label questionsLabel;
	@FXML
	private Label sampleQuestionText;
	@FXML
	private Button startButton;

	// Quiz question elements
	@FXML
	private Label activeQuizNameLabel;
	@FXML
	private Label progressLabel;
	@FXML
	private Label questionText;
	@FXML
	private VBox answersBox;
	@FXML
	private RadioButton answer1;
	@FXML
	private RadioButton answer2;
	@FXML
	private RadioButton answer3;
	@FXML
	private RadioButton answer4;
	@FXML
	private VBox feedbackBox;
	@FXML
	private Label feedbackLabel;
	@FXML
	private Label correctAnswerLabel;
	@FXML
	private Button submitBtn;

	@FXML
	private Button nextBtn;
	@FXML
	private Button prevBtn;

	// Summary elements
	@FXML
	private Label summaryLabel;

	private QuizService quizService;
	private CategoryService categoryService;
	private ToggleGroup answerGroup;

	private Quiz currentQuiz;
	private int currentQuestionIndex = 0;
	private int correctAnswersCount = 0;

	@FXML
	private void initialize() {
		this.quizService = new QuizService();
		this.categoryService = new CategoryService();

		// Initialize ComboBoxes for difficulties
		ObservableList<String> difficulties = FXCollections.observableArrayList("EASY", "MEDIUM", "HARD");
		difficultyComboBox.setItems(difficulties);
		difficultyComboBox.setValue("EASY");

		// Update categories to match the API
		loadCategories();

		// Set up ToggleGroup for radio buttons
		answerGroup = new ToggleGroup();
		answer1.setToggleGroup(answerGroup);
		answer2.setToggleGroup(answerGroup);
		answer3.setToggleGroup(answerGroup);
		answer4.setToggleGroup(answerGroup);

		// Set up the ListView cell factory to display only the quiz name
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

		// Generate and add some sample quizzes to select from
		generateSampleQuizzes();

		// Ensure welcome content is visible initially
		welcomeContent.setVisible(true);
		previewContent.setVisible(false);
		questionContent.setVisible(false);
		summaryContent.setVisible(false);
	}

	/**
	 * Load categories from the service
	 */
	private void loadCategories() {
		try {
			List<Category> categories = categoryService.getAllCategories();

			if (categories != null && !categories.isEmpty()) {
				ObservableList<String> categoryNames = FXCollections.observableArrayList();

				for (Category category : categories) {
					categoryNames.add(category.getName());
				}

				categoryComboBox.setItems(categoryNames);
				categoryComboBox.setValue(categoryNames.get(0));
			} else {
				// Fallback categories if service returns none
				ObservableList<String> fallbackCategories = FXCollections.observableArrayList(
						"Everyday Life and Essential Vocabulary", "People and Relationships", "Work and Education",
						"Health and Well-being", "Travel and Leisure", "Environment and Nature");
				categoryComboBox.setItems(fallbackCategories);
				categoryComboBox.setValue(fallbackCategories.get(0));
			}
		} catch (Exception e) {
			System.err.println("Error loading categories: " + e.getMessage());

			// Fallback categories
			ObservableList<String> fallbackCategories = FXCollections.observableArrayList(
					"Everyday Life and Essential Vocabulary", "People and Relationships", "Work and Education",
					"Health and Well-being", "Travel and Leisure", "Environment and Nature");
			categoryComboBox.setItems(fallbackCategories);
			categoryComboBox.setValue(fallbackCategories.get(0));
		}
	}

	/**
	 * Generate sample quizzes for selection
	 */
	private void generateSampleQuizzes() {
		// Clear existing quizzes first to prevent duplicates
		ObservableList<Quiz> sampleQuizzes = FXCollections.observableArrayList();

		// Get categories and difficulties
		List<String> categories = new ArrayList<>(categoryComboBox.getItems());
		List<String> difficulties = new ArrayList<>(difficultyComboBox.getItems());

		// For each category, create a quiz for each difficulty
		long quizId = 1;
		for (String categoryName : categories) {
			for (String difficultyName : difficulties) {
				Quiz quiz = new Quiz();
				quiz.setId(quizId++);
				quiz.setName("10 Words Quiz - " + categoryName + " (" + difficultyName + ")");

				// Set category
				Category category = new Category();
				category.setId(getCategoryIdByName(categoryName));
				category.setName(categoryName);
				quiz.setCategory(category);

				// Set difficulty
				quiz.setDifficulty(Difficulty.valueOf(difficultyName));

				sampleQuizzes.add(quiz);
			}
		}

		// Set the items to the new list (replacing any existing items)
		quizListView.setItems(sampleQuizzes);

		// Add selection listener to preview the selected quiz
		quizListView.getSelectionModel().selectedItemProperty().addListener((obs, oldQuiz, newQuiz) -> {
			if (newQuiz != null) {
				currentQuiz = newQuiz;
				showQuizPreview(newQuiz);
			}
		});
	}

	/**
	 * Maps category names to their IDs based on the provided API information.
	 */
	private Long getCategoryIdByName(String categoryName) {
		switch (categoryName) {
		case "Everyday Life and Essential Vocabulary":
			return 1L;
		case "People and Relationships":
			return 2L;
		case "Work and Education":
			return 3L;
		case "Health and Well-being":
			return 4L;
		case "Travel and Leisure":
			return 5L;
		case "Environment and Nature":
			return 6L;
		default:
			// If not found, try to match partially
			if (categoryName.contains("Everyday") || categoryName.contains("Essential"))
				return 1L;
			if (categoryName.contains("People") || categoryName.contains("Relationship"))
				return 2L;
			if (categoryName.contains("Work") || categoryName.contains("Education"))
				return 3L;
			if (categoryName.contains("Health") || categoryName.contains("Well"))
				return 4L;
			if (categoryName.contains("Travel") || categoryName.contains("Leisure"))
				return 5L;
			if (categoryName.contains("Environment") || categoryName.contains("Nature"))
				return 6L;
			return 1L; // Default to first category
		}
	}

	/**
	 * Applies filter and updates the ListView.
	 */
	@FXML
	private void handleFilterButtonClick(ActionEvent event) {
		String selectedDifficulty = difficultyComboBox.getValue();
		String selectedCategory = categoryComboBox.getValue();

		System.out.println("Filter button clicked!");
		System.out.println("Selected difficulty = " + selectedDifficulty);
		System.out.println("Selected category = " + selectedCategory);

		// Re-generate sample quizzes with filter
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

		if (filteredQuizzes.isEmpty()) {
			// If no matches, create a new quiz with the selected criteria
			Quiz filteredQuiz = new Quiz();
			filteredQuiz.setId(System.currentTimeMillis());
			filteredQuiz.setName("10 Words Quiz - " + selectedCategory + " (" + selectedDifficulty + ")");

			Category category = new Category();
			category.setId(getCategoryIdByName(selectedCategory));
			category.setName(selectedCategory);
			filteredQuiz.setCategory(category);

			filteredQuiz.setDifficulty(Difficulty.valueOf(selectedDifficulty));

			filteredQuizzes.add(filteredQuiz);
		}

		// Update the ListView
		quizListView.setItems(filteredQuizzes);
	}

	/**
	 * Displays preview info about the selected quiz.
	 */
	private void showQuizPreview(Quiz quiz) {
		// Hide others
		welcomeContent.setVisible(false);
		questionContent.setVisible(false);
		summaryContent.setVisible(false);
		previewContent.setVisible(true);

		// Populate preview
		quizNameLabel.setText(quiz.getName());
		categoryLabel.setText("Category: " + (quiz.getCategory() != null ? quiz.getCategory().getName() : "N/A"));
		difficultyLabel
				.setText("Difficulty: " + (quiz.getDifficulty() != null ? quiz.getDifficulty().toString() : "N/A"));
		questionsLabel.setText("Questions: 10"); // Always 10 questions from the API

		sampleQuestionText
				.setText("This quiz will generate 10 random questions based on the selected category and difficulty.");
	}

	/**
	 * Start quiz button event.
	 */
	@FXML
	private void handleStartQuizClick(ActionEvent event) {
		if (currentQuiz != null) {
			System.out.println("Starting quiz: " + currentQuiz.getName());
			startQuiz(currentQuiz);
		} else {
			showError("Please select a quiz first");
		}
	}

	private void startQuiz(Quiz quiz) {
		// Clear previous state
		currentQuestionIndex = 0;
		correctAnswersCount = 0;

		// Get category and difficulty from the quiz
		Long categoryId = quiz.getCategory().getId();
		String difficulty = quiz.getDifficulty().toString();

		// Set a specific name for the quiz
		String quizName = "10 Words Quiz - " + quiz.getCategory().getName() + " (" + difficulty + ")";
		quiz.setName(quizName);

		// Show loading indication
		activeQuizNameLabel.setText("Loading quiz questions...");
		questionContent.setVisible(true);
		previewContent.setVisible(false);
		welcomeContent.setVisible(false);
		summaryContent.setVisible(false);

		// Disable controls while loading
		answersBox.setDisable(true);
		submitBtn.setDisable(true);
		nextBtn.setDisable(true);
		prevBtn.setDisable(true);

		// Generate quiz questions from the API
		try {
			List<Question> generatedQuestions = quizService.generateQuiz(categoryId, difficulty);

			if (generatedQuestions != null && !generatedQuestions.isEmpty()) {
				// Replace the quiz's questions with the generated ones
				quiz.getQuestions().clear();
				for (Question question : generatedQuestions) {
					quiz.addQuestion(question);
				}

				currentQuiz = quiz;

				// Update quiz name display
				activeQuizNameLabel.setText(quizName);

				// Enable controls
				answersBox.setDisable(false);

				// Show first question
				showCurrentQuestion();
			} else {
				// Handle error - no questions generated
				showError("Could not generate quiz questions. Please try again.");

				// Go back to welcome screen
				questionContent.setVisible(false);
				welcomeContent.setVisible(true);
			}
		} catch (Exception e) {
			showError("Error starting quiz: " + e.getMessage());
			// Go back to welcome screen
			questionContent.setVisible(false);
			welcomeContent.setVisible(true);
		}
	}

	private void showCurrentQuestion() {
		if (currentQuiz == null || currentQuiz.getQuestions() == null || currentQuiz.getQuestions().isEmpty()) {
			return;
		}

		// Ensure index is valid
		if (currentQuestionIndex < 0) {
			currentQuestionIndex = 0;
		} else if (currentQuestionIndex >= currentQuiz.getQuestions().size()) {
			currentQuestionIndex = currentQuiz.getQuestions().size() - 1;
		}

		// Get current question
		Question question = currentQuiz.getQuestions().get(currentQuestionIndex);

		// Update question text
		questionText.setText(question.getQuestionText());

		// Get all options for this question
		List<String> options = question.getOptions();

		if (options == null || options.isEmpty()) {
			showError("No answer options available for this question");
			return;
		}

		// Update the radio buttons
		answer1.setText(options.get(0));
		answer2.setText(options.get(1));
		answer3.setText(options.get(2));
		answer4.setText(options.get(3));

		// Clear previous selection
		answerGroup.selectToggle(null);

		// Hide feedback
		feedbackBox.setVisible(false);

		// Update progress label
		// Update progress bar
		int total = currentQuiz.getQuestions().size();
		double progress = (double) (currentQuestionIndex + 1) / total;
		progressBar.setProgress(progress);
		progressLabel.setText(String.format("Question %d of %d", currentQuestionIndex + 1, total));

		// Enable submit button, disable next button
		submitBtn.setDisable(false);
		nextBtn.setDisable(true);
	}

	/**
	 * Check whether selected answer is correct, show feedback.
	 */
	@FXML
	private void handleSubmitAnswerClick(ActionEvent event) {
		if (answerGroup.getSelectedToggle() == null) {
			return; // No answer selected
		}

		RadioButton selectedButton = (RadioButton) answerGroup.getSelectedToggle();
		String selectedAnswer = selectedButton.getText();

		Question question = currentQuiz.getQuestions().get(currentQuestionIndex);
		boolean isCorrect = selectedAnswer.equals(question.getCorrectAnswer());

		if (isCorrect) {
			correctAnswersCount++;
		}

		// Show feedback
		showAnswerFeedback(isCorrect, question.getCorrectAnswer());

		// Disable submit button, enable next button
		submitBtn.setDisable(true);
		nextBtn.setDisable(false);
	}

	private void showAnswerFeedback(boolean correct, String correctAnswer) {
		feedbackBox.setVisible(true);
		feedbackBox.getStyleClass().removeAll("correct", "incorrect");

		if (correct) {
			feedbackBox.getStyleClass().add("correct");
			feedbackLabel.setText("Correct!");
			feedbackLabel.setStyle("-fx-text-fill: #2e7d32;");
			correctAnswerLabel.setVisible(false);
		} else {
			feedbackBox.getStyleClass().add("incorrect");
			feedbackLabel.setText("Incorrect!");
			feedbackLabel.setStyle("-fx-text-fill: #c62828;");
			correctAnswerLabel.setText("The correct answer is: " + correctAnswer);
			correctAnswerLabel.setVisible(true);
		}
	}

	/**
	 * Move to previous question if possible.
	 */
	@FXML
	private void handlePrevButtonClick(ActionEvent event) {
		if (currentQuiz == null) {
			return;
		}
		currentQuestionIndex--;
		if (currentQuestionIndex < 0) {
			currentQuestionIndex = 0;
		}
		showCurrentQuestion();
	}

	/**
	 * Move to next question or show summary if at the end.
	 */
	@FXML
	private void handleNextButtonClick(ActionEvent event) {
		if (currentQuiz == null) {
			return;
		}
		currentQuestionIndex++;
		if (currentQuestionIndex < currentQuiz.getQuestions().size()) {
			showCurrentQuestion();
		} else {
			// If we've gone past the last question, show summary
			showQuizSummary();
		}
	}

	/**
	 * Display final score and switch to summary screen.
	 */
	private void showQuizSummary() {
		questionContent.setVisible(false);
		summaryContent.setVisible(true);

		int totalQuestions = currentQuiz.getQuestions().size();
		summaryLabel.setText(String.format("You scored %d out of %d!", correctAnswersCount, totalQuestions));
	}

	/**
	 * Return to quiz list from summary screen.
	 */
	@FXML
	private void handleBackToQuizzesClick(ActionEvent event) {
		summaryContent.setVisible(false);
		welcomeContent.setVisible(true);
	}

	// Public method to manually refresh quizzes if needed
	public void refresh() {
		generateSampleQuizzes();
	}

	private void showError(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}