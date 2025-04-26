package com.lingotower.ui.controllers;

import static com.lingotower.constants.QuizConstants.BLANK_PLACEHOLDER;
import static com.lingotower.constants.QuizConstants.COMPLETION_PREFIX;
import static com.lingotower.constants.QuizConstants.DIFFICULTIES;
import static com.lingotower.constants.QuizConstants.FALLBACK_CATEGORIES;
import static com.lingotower.constants.QuizConstants.STYLE_TEXT_FILL_GREEN;
import static com.lingotower.constants.QuizConstants.STYLE_TEXT_FILL_RED;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.lingotower.model.Category;
import com.lingotower.model.Question;
import com.lingotower.model.Quiz;
import com.lingotower.service.CategoryService;
import com.lingotower.service.QuizService;
import com.lingotower.ui.quiz.QuizGenerator;
import com.lingotower.ui.quiz.QuizUIHelper;
import com.lingotower.utils.LoggingUtility;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

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
	private VBox summaryContent;

	// Quiz preview elements
	@FXML
	private Label quizNameLabel;
	@FXML
	private Label categoryLabel;
	@FXML
	private Label difficultyLabel;
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
	private RadioButton answer5;

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

	// Summary elements
	@FXML
	private Label summaryLabel;

	// Services
	private QuizService quizService;
	private CategoryService categoryService;
	private QuizGenerator quizGenerator;

	// State
	private ToggleGroup answerGroup;
	private Quiz currentQuiz;
	private int currentQuestionIndex = 0;
	private int correctAnswersCount = 0;

	private static final Logger logger = LoggingUtility.getLogger(QuizController.class);

	@FXML
	private void initialize() {
		// Initialize services
		this.quizService = new QuizService();
		this.categoryService = new CategoryService();
		this.quizGenerator = new QuizGenerator(quizService, categoryService);

		// Initialize difficulty dropdown
		difficultyComboBox.setItems(DIFFICULTIES);
		difficultyComboBox.setValue("EASY");

		// Load available categories
		loadCategories();

		// Set up ToggleGroup for radio buttons
		setupRadioButtons();

		// Set up quiz ListView display
		setupQuizListView();

		// Generate sample quizzes
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
	 * Set up the radio button toggle group
	 */
	private void setupRadioButtons() {
		answerGroup = new ToggleGroup();
		answer1.setToggleGroup(answerGroup);
		answer2.setToggleGroup(answerGroup);
		answer3.setToggleGroup(answerGroup);
		answer4.setToggleGroup(answerGroup);
		answer5.setToggleGroup(answerGroup);
	}

	/**
	 * Set up the quiz list view cell factory
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
	 * Generate sample quizzes for selection
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

		// Add selection listener to preview the selected quiz
		quizListView.getSelectionModel().selectedItemProperty().addListener((obs, oldQuiz, newQuiz) -> {
			if (newQuiz != null) {
				currentQuiz = newQuiz;
				showQuizPreview(newQuiz);
			}
		});
	}

	/**
	 * Applies filter and updates the ListView.
	 */
	@FXML
	private void handleFilterButtonClick(ActionEvent event) {
		String selectedDifficulty = difficultyComboBox.getValue();
		String selectedCategory = categoryComboBox.getValue();

		logger.debug("Filter button clicked!");
		logger.debug("Selected difficulty = {}", selectedDifficulty);
		logger.debug("Selected category = {}", selectedCategory);

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
	 * Displays preview info about the selected quiz.
	 */
	private void showQuizPreview(Quiz quiz) {
		// Hide others, show preview
		welcomeContent.setVisible(false);
		questionContent.setVisible(false);
		summaryContent.setVisible(false);
		previewContent.setVisible(true);

		// Check if this is a sentence completion quiz
		boolean isSentenceCompletionQuiz = quiz.getName().startsWith(COMPLETION_PREFIX);

		// Populate preview
		quizNameLabel.setText(quiz.getName());
		categoryLabel.setText("Category: " + (quiz.getCategory() != null ? quiz.getCategory().getName() : "N/A"));
		difficultyLabel
				.setText("Difficulty: " + (quiz.getDifficulty() != null ? quiz.getDifficulty().toString() : "N/A"));

		// Set appropriate description based on quiz type
		if (isSentenceCompletionQuiz) {
			sampleQuestionText.setText("This quiz will test your language skills with sentence completion exercises. "
					+ "You'll be shown sentences with missing words and asked to select the correct word "
					+ "to fill in the blank.");
		} else {
			sampleQuestionText
					.setText("This quiz will generate random vocabulary questions based on the selected category "
							+ "and difficulty level. Test your language knowledge!");
		}
	}

	/**
	 * Start quiz button event.
	 */
	@FXML
	private void handleStartQuizClick(ActionEvent event) {
		if (currentQuiz != null) {
			logger.info("Starting quiz: {}", currentQuiz.getName());
			startQuiz(currentQuiz);
		} else {
			QuizUIHelper.showError("Please select a quiz first");
		}
	}

	/**
	 * Starts a quiz by generating questions and showing the first one
	 */
	private void startQuiz(Quiz quiz) {
		// Reset state
		currentQuestionIndex = 0;
		correctAnswersCount = 0;

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

		try {
			// Generate the quiz using the service
			Quiz generatedQuiz = quizGenerator.generateQuizWithQuestions(quiz);

			if (generatedQuiz != null && !generatedQuiz.getQuestions().isEmpty()) {
				currentQuiz = generatedQuiz;

				// Update quiz name display
				activeQuizNameLabel.setText(currentQuiz.getName());

				// Enable controls
				answersBox.setDisable(false);

				// Check if fallback questions are being used
				boolean usingFallback = false;
				if (!currentQuiz.getQuestions().isEmpty()) {
					Question firstQuestion = currentQuiz.getQuestions().get(0);
					if (firstQuestion.getCategory() != null && firstQuestion.getCategory().getName() != null
							&& firstQuestion.getCategory().getName().endsWith("_FALLBACK")) {
						usingFallback = true;

						// Clean up the category name for display purposes while keeping the flag
						for (Question q : currentQuiz.getQuestions()) {
							if (q.getCategory() != null && q.getCategory().getName().endsWith("_FALLBACK")) {
								String originalName = q.getCategory().getName().replace("_FALLBACK", "");
								q.getCategory().setName(originalName);
							}
						}

						// Show a warning to the user
						Platform.runLater(() -> {
							QuizUIHelper.showWarning("Using sample questions",
									"Could not retrieve real questions from the server. "
											+ "Using sample questions instead.");

						});
					}
				}

				// Show first question
				showCurrentQuestion();
			} else {
				// Handle error - no questions generated
				String errorMessage = "No questions available for the selected category and difficulty. Please try a different selection";
				QuizUIHelper.showError(errorMessage);

				// Go back to welcome screen
				questionContent.setVisible(false);
				welcomeContent.setVisible(true);
			}
		} catch (Exception e) {
			QuizUIHelper.showError("Error starting quiz: " + e.getMessage());
			e.printStackTrace();
			// Go back to welcome screen
			questionContent.setVisible(false);
			welcomeContent.setVisible(true);
		}
	}

	/**
	 * Display the current question
	 */
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

		// Check if this is a sentence completion question
		boolean isSentenceCompletion = question.getQuestionText().contains(BLANK_PLACEHOLDER);

		// Update question text and style it appropriately
		questionText.setText(question.getQuestionText());

		if (isSentenceCompletion) {
			// Add special styling for sentence completion questions
			questionText.getStyleClass().add("sentence-completion");
		} else {
			// Remove the special styling if not a sentence completion
			questionText.getStyleClass().removeAll("sentence-completion");
		}

		// Set up the answer options
		QuizUIHelper.setUpAnswers(question, answer1, answer2, answer3, answer4, answer5);

		// Clear previous selection
		answerGroup.selectToggle(null);

		// Hide feedback
		feedbackBox.setVisible(false);

		// Update progress label and bar
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
			selectedButton.setStyle(STYLE_TEXT_FILL_GREEN); // Green for selected answer
		} else {
			selectedButton.setStyle(STYLE_TEXT_FILL_RED); // Red for selected answer
		}

		// Show feedback
		QuizUIHelper.showAnswerFeedback(isCorrect, question.getCorrectAnswer(), feedbackBox, feedbackLabel,
				correctAnswerLabel, question);

		// Disable submit button, enable next
		submitBtn.setDisable(true);
		nextBtn.setDisable(false);

		// If this is a sentence completion question, highlight the completed sentence
		boolean isSentenceCompletion = question.getQuestionText().contains(BLANK_PLACEHOLDER);
		if (isSentenceCompletion) {
			// Replace the blank with the selected answer and show it in the question text
			String completedSentence = QuizUIHelper.getCompleteSentence(question.getQuestionText(), selectedAnswer);
			// Set a different color based on correctness
			if (isCorrect) {
				questionText.setStyle(STYLE_TEXT_FILL_GREEN); // Green for correct
			} else {
				questionText.setStyle(STYLE_TEXT_FILL_RED); // Red for incorrect
			}
			questionText.setText(completedSentence);
		}

		// Create a Timeline to reset the color after a delay
		Timeline resetColorTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
			questionText.setStyle("-fx-text-fill: black;"); // Reset to black
			selectedButton.setStyle("-fx-text-fill: black;"); // Reset to black
		}));

		resetColorTimeline.setCycleCount(1); // Run only once
		resetColorTimeline.play();
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
		boolean isSentenceCompletionQuiz = currentQuiz.getName().startsWith(COMPLETION_PREFIX);

		// Generate appropriate summary text
		String summaryText = QuizUIHelper.generateSummaryText(correctAnswersCount, totalQuestions,
				isSentenceCompletionQuiz);

		summaryLabel.setText(summaryText);
	}

	/**
	 * Return to quiz list from summary screen.
	 */
	@FXML
	private void handleBackToQuizzesClick(ActionEvent event) {
		summaryContent.setVisible(false);
		welcomeContent.setVisible(true);
	}

	/**
	 * Public method to manually refresh quizzes if needed from another controller
	 */
	public void refresh() {
		loadCategories();
		generateSampleQuizzes();
	}
}