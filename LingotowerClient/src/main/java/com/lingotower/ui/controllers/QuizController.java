package com.lingotower.ui.controllers;

import org.slf4j.Logger;

import com.lingotower.model.Question;
import com.lingotower.model.Quiz;
import com.lingotower.service.CategoryService;
import com.lingotower.service.QuizService;
import com.lingotower.ui.quiz.QuizDisplayManager;
import com.lingotower.ui.quiz.QuizGenerator;
import com.lingotower.ui.quiz.QuizListManager;
import com.lingotower.ui.quiz.QuizResultsManager;
import com.lingotower.ui.quiz.QuizStateManager;
import com.lingotower.ui.quiz.QuizUIHelper;
import com.lingotower.utils.LoggingUtility;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
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
	private ProgressBar progressBar;

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

	// Managers
	private QuizStateManager stateManager;
	private QuizListManager listManager;
	private QuizDisplayManager displayManager;
	private QuizResultsManager resultsManager;
	private QuizGenerator quizGenerator;

	// State
	private ToggleGroup answerGroup;
	private Quiz currentQuiz;
	private int currentQuestionIndex = 0;

	private static final Logger logger = LoggingUtility.getLogger(QuizController.class);

	@FXML
	private void initialize() {
		// Initialize services
		this.quizService = new QuizService();
		this.categoryService = new CategoryService();
		this.quizGenerator = new QuizGenerator(quizService, categoryService);

		// Set up ToggleGroup for radio buttons
		setupRadioButtons();

		// Initialize managers
		initializeManagers();

		// Initialize UI state
		stateManager.initializeState();
	}

	/**
	 * Sets up the radio button toggle group
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
	 * Initializes all the managers
	 */
	private void initializeManagers() {
		// Initialize state manager
		stateManager = new QuizStateManager(welcomeContent, previewContent, questionContent, summaryContent);

		// Initialize list manager
		listManager = new QuizListManager(quizListView, difficultyComboBox, categoryComboBox, categoryService,
				quizGenerator);

		// Create component groups for the display manager
		QuizDisplayManager.PreviewComponents previewComponents = new QuizDisplayManager.PreviewComponents(quizNameLabel,
				categoryLabel, difficultyLabel, sampleQuestionText);

		QuizDisplayManager.QuestionComponents questionComponents = new QuizDisplayManager.QuestionComponents(
				activeQuizNameLabel, progressLabel, progressBar, questionText, answerGroup, answer1, answer2, answer3,
				answer4, answer5);

		QuizDisplayManager.FeedbackComponents feedbackComponents = new QuizDisplayManager.FeedbackComponents(
				feedbackBox, feedbackLabel, correctAnswerLabel);

		// Initialize display manager with component groups instead of individual
		// components
		displayManager = new QuizDisplayManager(previewComponents, questionComponents, feedbackComponents);

		// Initialize results manager
		resultsManager = new QuizResultsManager(summaryLabel);

		// Set up the quiz list selection listener
		listManager.setSelectionListener(quiz -> {
			currentQuiz = quiz;
			showQuizPreview(quiz);
		});

		// Initialize the quiz list
		listManager.initialize();
	}

	/**
	 * Shows the preview for a selected quiz
	 */
	private void showQuizPreview(Quiz quiz) {
		stateManager.showPreview(quiz);
		displayManager.displayQuizPreview(quiz);
	}

	/**
	 * Applies filter and updates the ListView.
	 */
	@FXML
	private void handleFilterButtonClick(ActionEvent event) {
		listManager.applyFilter();
	}

	/**
	 * Start quiz button event.
	 */
	@FXML
	private void handleStartQuizClick(ActionEvent event) {
		Quiz selectedQuiz = listManager.getSelectedQuiz();
		if (selectedQuiz != null) {
			logger.info("Starting quiz: {}", selectedQuiz.getName());
			startQuiz(selectedQuiz);
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
		resultsManager.resetForNewQuiz(quiz);

		// Show loading screen
		stateManager.showQuestions();
		displayManager.setLoadingState(true);

		// Disable controls while loading
		submitBtn.setDisable(true);
		nextBtn.setDisable(true);

		try {
			// Generate the quiz using the service
			Quiz generatedQuiz = quizGenerator.generateQuizWithQuestions(quiz);

			if (generatedQuiz != null && !generatedQuiz.getQuestions().isEmpty()) {
				currentQuiz = generatedQuiz;

				// Update quiz name display
				displayManager.setActiveQuizName(currentQuiz.getName());

				// Enable controls
				answersBox.setDisable(false);
				displayManager.setLoadingState(false);

				// Show first question
				showCurrentQuestion();
			} else {
				// Handle error - no questions generated
				String errorMessage = "No questions available for the selected category and difficulty. Please try a different selection";
				QuizUIHelper.showError(errorMessage);

				// Go back to welcome screen
				stateManager.returnToWelcome();
			}
		} catch (Exception e) {
			QuizUIHelper.showError("Error starting quiz: " + e.getMessage());
			e.printStackTrace();
			// Go back to welcome screen
			stateManager.returnToWelcome();
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
		Question currentQuestion = currentQuiz.getQuestions().get(currentQuestionIndex);

		// Display the question
		displayManager.displayQuestion(currentQuestion, currentQuestionIndex, currentQuiz.getQuestions().size());

		// Enable submit button, disable next button
		submitBtn.setDisable(false);
		nextBtn.setDisable(true);
	}

	/**
	 * Check whether selected answer is correct, show feedback.
	 */
	@FXML
	private void handleSubmitAnswerClick(ActionEvent event) {
		String selectedAnswer = displayManager.getSelectedAnswer();
		if (selectedAnswer == null) {
			return; // No answer selected
		}

		Question question = currentQuiz.getQuestions().get(currentQuestionIndex);
		boolean isCorrect = selectedAnswer.equals(question.getCorrectAnswer());

		// Record result
		if (isCorrect) {
			resultsManager.recordCorrectAnswer();
		}

		// Show feedback
		displayManager.showAnswerFeedback(question, selectedAnswer, isCorrect);

		// Disable submit button, enable next
		submitBtn.setDisable(true);
		nextBtn.setDisable(false);
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
			stateManager.showSummary();
			resultsManager.displaySummary();
		}
	}

	/**
	 * Return to quiz list from summary screen.
	 */
	@FXML
	private void handleBackToQuizzesClick(ActionEvent event) {
		stateManager.returnToWelcome();
	}

	/**
	 * Public method to manually refresh quizzes if needed from another controller
	 */
	public void refresh() {
		// Re-initialize the list manager
		listManager.initialize();
	}
}