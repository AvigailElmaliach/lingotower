package com.lingotower.ui.controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.lingotower.dto.sentence.SentenceCompletionDTO;
import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;
import com.lingotower.model.Question;
import com.lingotower.model.Quiz;
import com.lingotower.service.CategoryService;
import com.lingotower.service.QuizService;
import com.lingotower.utils.LoggingUtility;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
	private VBox summaryContent; // Summaries/score display

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

	private QuizService quizService;
	private CategoryService categoryService;
	private ToggleGroup answerGroup;

	private Quiz currentQuiz;
	private int currentQuestionIndex = 0;
	private int correctAnswersCount = 0;
	private static final Logger logger = LoggingUtility.getLogger(QuizController.class);

	private static final ObservableList<String> FALLBACK_CATEGORIES = FXCollections.unmodifiableObservableList(
			FXCollections.observableArrayList("Everyday Life and Essential Vocabulary", "People and Relationships",
					"Work and Education", "Health and Well-being", "Travel and Leisure", "Environment and Nature"));

	private static final String STYLE_TEXT_FILL_RED = "-fx-text-fill: #c62828;";
	private static final String STYLE_TEXT_FILL_GREEN = "-fx-text-fill: #2e7d32;";

	private static final String BLANK_PLACEHOLDER = "_____";
	private static final String QUIZ_PREFIX = "Words Quiz - ";
	private static final String COMPLETION_PREFIX = "Sentence Completion - ";

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
		answer5.setToggleGroup(answerGroup);

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
				categoryComboBox.setItems(FALLBACK_CATEGORIES);
				categoryComboBox.setValue(FALLBACK_CATEGORIES.get(0));
			}
		} catch (Exception e) {
			logger.error("Error starting quiz: {}", e.getMessage(), e);
			// Fallback categories
			categoryComboBox.setItems(FALLBACK_CATEGORIES);
			categoryComboBox.setValue(FALLBACK_CATEGORIES.get(0));
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

		// For each category, create both regular quizzes and sentence completion
		// quizzes
		long quizId = 1;
		for (String categoryName : categories) {
			for (String difficultyName : difficulties) {
				// Create regular vocabulary quiz
				Quiz vocabQuiz = new Quiz();
				vocabQuiz.setId(quizId++);
				vocabQuiz.setName(QUIZ_PREFIX + categoryName + " (" + difficultyName + ")");

				// Set category
				Category category = new Category();
				category.setId(getCategoryIdByName(categoryName));
				category.setName(categoryName);
				vocabQuiz.setCategory(category);

				// Set difficulty
				vocabQuiz.setDifficulty(Difficulty.valueOf(difficultyName));

				sampleQuizzes.add(vocabQuiz);

				// Create sentence completion quiz
				Quiz sentenceQuiz = new Quiz();
				sentenceQuiz.setId(quizId++);
				sentenceQuiz.setName(COMPLETION_PREFIX + categoryName + " (" + difficultyName + ")");

				// Use the same category
				sentenceQuiz.setCategory(category);

				// Set difficulty
				sentenceQuiz.setDifficulty(Difficulty.valueOf(difficultyName));

				sampleQuizzes.add(sentenceQuiz);
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

		logger.debug("Filter button clicked!");
		logger.debug("Selected difficulty = {}", selectedDifficulty);
		logger.debug("Selected category = {}", selectedCategory);
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
			filteredQuiz.setName(QUIZ_PREFIX + selectedCategory + " (" + selectedDifficulty + ")");

			Category category = new Category();
			category.setId(getCategoryIdByName(selectedCategory));
			category.setName(selectedCategory);
			filteredQuiz.setCategory(category);

			filteredQuiz.setDifficulty(Difficulty.valueOf(selectedDifficulty));

			filteredQuizzes.add(filteredQuiz);

			// Add sentence completion quiz
			Quiz sentenceQuiz = new Quiz();
			sentenceQuiz.setId(System.currentTimeMillis() + 1);
			sentenceQuiz.setName(COMPLETION_PREFIX + selectedCategory + " (" + selectedDifficulty + ")");
			sentenceQuiz.setCategory(category);
			sentenceQuiz.setDifficulty(Difficulty.valueOf(selectedDifficulty));
			filteredQuizzes.add(sentenceQuiz);

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

		// Check if this is a sentence completion quiz
		boolean isSentenceCompletionQuiz = quiz.getName().startsWith("Sentence Completion");

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
			showError("Please select a quiz first");
		}
	}

	private void startQuiz(Quiz quiz) {
		// Clear previous state
		currentQuestionIndex = 0;
		correctAnswersCount = 0;

		// Get category and difficulty from the quiz
		Long categoryId = quiz.getCategory().getId();
		String categoryName = quiz.getCategory().getName();
		String difficulty = quiz.getDifficulty().toString();
		String quizName = quiz.getName();

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
			List<Question> generatedQuestions = null;

			// Check if this is a sentence completion quiz or a regular quiz
			boolean isSentenceCompletionQuiz = quizName.startsWith("Sentence Completion");

			if (isSentenceCompletionQuiz) {
				// Generate sentence completion questions
				logger.info("Starting sentence completion quiz with category: {}, difficulty: {}", categoryName,
						difficulty);
				List<SentenceCompletionDTO> sentences = quizService.generateSentenceCompletions(categoryName,
						difficulty);

				if (sentences != null && !sentences.isEmpty()) {
					generatedQuestions = quizService.convertSentencesToQuestions(sentences);
					logger.info("Successfully converted {} sentences to questions", sentences.size());
				} else {
					logger.warn("Failed to get sentence completion questions. Using fallback method...");
					// FALLBACK: Create sample sentence completion questions
					generatedQuestions = createFallbackSentenceQuestions(categoryName, difficulty);

					// Show a warning to the user that these are fallback questions
					Platform.runLater(() -> {
						showError(
								"Could not get real sentence completion questions from the server. Using sample questions instead.");
					});
				}

				// Set the quiz name
				quizName = COMPLETION_PREFIX + categoryName + " (" + difficulty + ")";
			} else {
				// Generate regular quiz questions
				logger.info("Starting regular quiz with categoryId: {}, difficulty: {}", categoryId, difficulty);
				generatedQuestions = quizService.generateQuiz(categoryId, difficulty);

				// Set the quiz name
				quizName = QUIZ_PREFIX + categoryName + " (" + difficulty + ")";
			}

			// Process generated questions
			if (generatedQuestions != null && !generatedQuestions.isEmpty()) {
				// Replace the quiz's questions with the generated ones
				quiz.getQuestions().clear();
				for (Question question : generatedQuestions) {
					quiz.addQuestion(question);
				}

				currentQuiz = quiz;
				quiz.setName(quizName);

				// Update quiz name display
				activeQuizNameLabel.setText(quizName);

				// Enable controls
				answersBox.setDisable(false);

				// Show first question
				showCurrentQuestion();
			} else {
				// Handle error - no questions generated
				String errorMessage = "No questions available for the selected category and difficulty. Please try a different selection";
				showError(errorMessage);

				// Go back to welcome screen
				questionContent.setVisible(false);
				welcomeContent.setVisible(true);
			}
		} catch (Exception e) {
			showError("Error starting quiz: " + e.getMessage());
			e.printStackTrace();
			// Go back to welcome screen
			questionContent.setVisible(false);
			welcomeContent.setVisible(true);
		}
	}

	/**
	 * Creates fallback sentence completion questions when the API fails. This is
	 * just a temporary solution for testing.
	 */
	private List<Question> createFallbackSentenceQuestions(String categoryName, String difficulty) {
		List<Question> questions = new ArrayList<>();

		// Create a category object
		Category category = new Category();
		category.setId(getCategoryIdByName(categoryName));
		category.setName(categoryName);

		// Create sample questions based on category
		if (categoryName.contains("Everyday Life")) {
			// Question 1
			Question q1 = new Question();
			q1.setQuestionText("I _____ to make a phone call.");
			q1.setCorrectAnswer("need");
			q1.setOptions(List.of("need", "thank you", "hello", "goodbye", "please"));
			q1.setCategory(category);
			questions.add(q1);

			// Question 2
			Question q2 = new Question();
			q2.setQuestionText("Please _____ me the directions to the store.");
			q2.setCorrectAnswer("give");
			q2.setOptions(List.of("give", "happy", "blue", "seven", "today"));
			q2.setCategory(category);
			questions.add(q2);

			// Question 3
			Question q3 = new Question();
			q3.setQuestionText("What time _____ it?");
			q3.setCorrectAnswer("is");
			q3.setOptions(List.of("is", "are", "car", "house", "day"));
			q3.setCategory(category);
			questions.add(q3);
		} else if (categoryName.contains("Work")) {
			// Question 1
			Question q1 = new Question();
			q1.setQuestionText("I have a meeting with my _____ today.");
			q1.setCorrectAnswer("boss");
			q1.setOptions(List.of("boss", "apple", "car", "dog", "tree"));
			q1.setCategory(category);
			questions.add(q1);

			// Question 2
			Question q2 = new Question();
			q2.setQuestionText("Please _____ this document by tomorrow.");
			q2.setCorrectAnswer("complete");
			q2.setOptions(List.of("complete", "orange", "table", "chair", "computer"));
			q2.setCategory(category);
			questions.add(q2);
		} else {
			// Default questions for any category
			Question q1 = new Question();
			q1.setQuestionText("I _____ to learn this language.");
			q1.setCorrectAnswer("want");
			q1.setOptions(List.of("want", "blue", "car", "apple", "house"));
			q1.setCategory(category);
			questions.add(q1);

			Question q2 = new Question();
			q2.setQuestionText("What is your _____?");
			q2.setCorrectAnswer("name");
			q2.setOptions(List.of("name", "blue", "water", "chair", "tree"));
			q2.setCategory(category);
			questions.add(q2);
		}

		// Add difficulty-based questions
		if (difficulty.equals("HARD")) {
			Question q = new Question();
			q.setQuestionText("The company _____ its annual report yesterday.");
			q.setCorrectAnswer("published");
			q.setOptions(List.of("published", "happy", "blue", "run", "eat"));
			q.setCategory(category);
			questions.add(q);
		}

		logger.info("Created {} fallback sentence completion questions", questions.size());
		return questions;
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
		answer5.setText(options.get(4));

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
			selectedButton.setStyle(STYLE_TEXT_FILL_GREEN); // Green for selected answer
		} else {
			selectedButton.setStyle(STYLE_TEXT_FILL_RED); // Red for selected answer
		}

		// Show feedback
		showAnswerFeedback(isCorrect, question.getCorrectAnswer());

		// Disable submit button, enable next
		submitBtn.setDisable(true);
		nextBtn.setDisable(false);

		// If this is a sentence completion question, highlight the blank with the
		// selected answer
		boolean isSentenceCompletion = question.getQuestionText().contains(BLANK_PLACEHOLDER);
		if (isSentenceCompletion) {
			// Replace the blank with the selected answer and show it in the question text
			String completedSentence = getCompleteSentence(question.getQuestionText(), selectedAnswer);
			// Set a different color based on correctness
			if (isCorrect) {
				correctAnswersCount++;
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

	private void showAnswerFeedback(boolean correct, String correctAnswer) {
		feedbackBox.setVisible(true);
		feedbackBox.getStyleClass().removeAll("correct", "incorrect");

		// Get current question to check if it's a sentence completion
		Question currentQuestion = currentQuiz.getQuestions().get(currentQuestionIndex);
		boolean isSentenceCompletion = currentQuestion.getQuestionText().contains(BLANK_PLACEHOLDER);

		if (correct) {
			feedbackBox.getStyleClass().add("correct");
			feedbackLabel.setText("Correct!");
			feedbackLabel.setStyle(STYLE_TEXT_FILL_GREEN);

			if (isSentenceCompletion) {
				// Show the complete sentence for sentence completion questions
				String completeSentence = getCompleteSentence(currentQuestion.getQuestionText(), correctAnswer);
				correctAnswerLabel.setText("Complete sentence: " + completeSentence);
				correctAnswerLabel.setVisible(true);
			} else {
				correctAnswerLabel.setVisible(false);
			}
		} else {
			feedbackBox.getStyleClass().add("incorrect");
			feedbackLabel.setText("Incorrect!");
			feedbackLabel.setStyle(STYLE_TEXT_FILL_RED);

			if (isSentenceCompletion) {
				// Show the complete sentence for sentence completion questions
				String completeSentence = getCompleteSentence(currentQuestion.getQuestionText(), correctAnswer);
				correctAnswerLabel.setText(
						"The correct answer is: " + correctAnswer + "\nComplete sentence: " + completeSentence);
			} else {
				correctAnswerLabel.setText("The correct answer is: " + correctAnswer);
			}
			correctAnswerLabel.setVisible(true);
		}
	}

	/**
	 * Replaces the blank in a sentence completion question with the correct answer.
	 * 
	 * @param question The question text containing blanks (_____)
	 * @param answer   The correct answer to insert
	 * @return The complete sentence with the answer inserted
	 */
	private String getCompleteSentence(String question, String answer) {
		if (question == null || answer == null) {
			return question;
		}

		// Replace the blank with the correct answer
		return question.replace(BLANK_PLACEHOLDER, answer);
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

		// Check if this was a sentence completion quiz
		boolean isSentenceCompletionQuiz = currentQuiz.getName().startsWith("Sentence Completion");

		// Calculate score percentage
		int scorePercentage = (correctAnswersCount * 100) / totalQuestions;

		// Create appropriate summary message
		StringBuilder summaryBuilder = new StringBuilder();
		summaryBuilder.append(
				String.format("You scored %d out of %d! (%d%%)", correctAnswersCount, totalQuestions, scorePercentage));

		// Add specific feedback based on quiz type
		if (isSentenceCompletionQuiz) {
			summaryBuilder.append("\n\nYou're doing great with sentence completion!");

			// Add level-based feedback
			if (scorePercentage >= 90) {
				summaryBuilder.append("\nExcellent job! You have a strong grasp of sentence structure.");
			} else if (scorePercentage >= 70) {
				summaryBuilder.append("\nGood work! Keep practicing to improve your language skills.");
			} else {
				summaryBuilder.append(
						"\nKeep practicing! Sentence completion exercises will help you understand language context better.");
			}
		} else {
			// Regular quiz feedback
			if (scorePercentage >= 90) {
				summaryBuilder.append("\n\nExcellent vocabulary knowledge!");
			} else if (scorePercentage >= 70) {
				summaryBuilder.append("\n\nGood vocabulary skills! Keep learning new words.");
			} else {
				summaryBuilder.append("\n\nKeep building your vocabulary with more practice.");
			}
		}

		summaryLabel.setText(summaryBuilder.toString());
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