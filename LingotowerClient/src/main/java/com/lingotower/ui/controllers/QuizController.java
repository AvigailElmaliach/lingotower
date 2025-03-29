package com.lingotower.ui.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;
import com.lingotower.model.Question;
import com.lingotower.model.Quiz;
import com.lingotower.service.QuizService;

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

public class QuizController {

	@FXML
	private BorderPane view;
	@FXML
	private ListView<Quiz> quizListView;
	@FXML
	private ComboBox<String> difficultyComboBox;
	@FXML
	private ComboBox<String> categoryComboBox;

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

	// Summary elements
	@FXML
	private Label summaryLabel;

	private QuizService quizService;
	private ToggleGroup answerGroup;

	private Quiz currentQuiz;
	private int currentQuestionIndex = 0;
	private int correctAnswersCount = 0;

	@FXML
	private void initialize() {
		this.quizService = new QuizService();

		// Initialize ComboBoxes
		ObservableList<String> difficulties = FXCollections.observableArrayList("All Levels", "Easy", "Medium", "Hard");
		difficultyComboBox.setItems(difficulties);
		difficultyComboBox.setValue("All Levels");

		ObservableList<String> categories = FXCollections.observableArrayList("All Categories", "Basics", "Food",
				"Travel");
		categoryComboBox.setItems(categories);
		categoryComboBox.setValue("All Categories");

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

		// ListView selection listener
		quizListView.getSelectionModel().selectedItemProperty().addListener((obs, oldQuiz, newQuiz) -> {
			if (newQuiz != null) {
				currentQuiz = newQuiz;
				showQuizPreview(newQuiz);
			}
		});

		// Load quizzes (from service or fallback to mock data)
		loadQuizzes();
	}

	/**
	 * Loads quizzes from QuizService, falling back to mock data if necessary.
	 */
	private void loadQuizzes() {
		try {
			List<Quiz> quizzes = quizService.getAllQuizzes();
			if (quizzes != null && !quizzes.isEmpty()) {
				quizListView.setItems(FXCollections.observableArrayList(quizzes));
			} else {
				createMockQuizzes();
			}
		} catch (Exception e) {
			e.printStackTrace();
			createMockQuizzes();
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

		// Check for null values and set defaults
		if (selectedDifficulty == null)
			selectedDifficulty = "All Levels";
		if (selectedCategory == null)
			selectedCategory = "All Categories";

		// Use a cached list of all quizzes if possible, or load from service if needed
		List<Quiz> allQuizzes;
		try {
			// It's better to cache this list somewhere rather than reload every time
			allQuizzes = quizService.getAllQuizzes();
			if (allQuizzes == null || allQuizzes.isEmpty()) {
				allQuizzes = createAndReturnMockQuizzes();
				System.out.println("Using mock quizzes - service returned no data");
			}
		} catch (Exception e) {
			allQuizzes = createAndReturnMockQuizzes();
			System.out.println("Using mock quizzes due to service error: " + e.getMessage());
		}

		if (allQuizzes == null || allQuizzes.isEmpty()) {
			System.out.println("No quizzes available to filter");
			return;
		}

		// Log what we're filtering
		System.out.println("Filtering " + allQuizzes.size() + " quizzes");

		// Filter quizzes by difficulty & category
		final String finalDifficulty = selectedDifficulty;
		final String finalCategory = selectedCategory;

		List<Quiz> filteredQuizzes = allQuizzes.stream().filter(quiz -> filterByDifficulty(quiz, finalDifficulty))
				.filter(quiz -> filterByCategory(quiz, finalCategory)).collect(Collectors.toList());

		System.out.println("Filter result: " + filteredQuizzes.size() + " quizzes match criteria");

		// Debug the filtered quizzes
		filteredQuizzes.forEach(quiz -> {
			System.out.println("Filtered quiz: " + quiz.getName() + ", Difficulty: "
					+ (quiz.getDifficulty() != null ? quiz.getDifficulty().toString() : "null") + ", Category: "
					+ (quiz.getCategory() != null ? quiz.getCategory().getName() : "null"));
		});

		// Update the ListView
		quizListView.setItems(FXCollections.observableArrayList(filteredQuizzes));
	}

	// Improved filter methods with better null handling and debugging
	private boolean filterByDifficulty(Quiz quiz, String difficulty) {
		if ("All Levels".equals(difficulty)) {
			return true;
		}

		if (quiz.getDifficulty() == null) {
			System.out.println("Warning: Quiz '" + quiz.getName() + "' has null difficulty");
			return false;
		}

		boolean matches = quiz.getDifficulty().toString().equalsIgnoreCase(difficulty);
		if (!matches) {
			System.out.println("Difficulty mismatch for '" + quiz.getName() + "': Expected '" + difficulty
					+ "', but found '" + quiz.getDifficulty().toString() + "'");
		}
		return matches;
	}

	private boolean filterByCategory(Quiz quiz, String category) {
		if ("All Categories".equals(category)) {
			return true;
		}

		if (quiz.getCategory() == null) {
			System.out.println("Warning: Quiz '" + quiz.getName() + "' has null category");
			return false;
		}

		boolean matches = quiz.getCategory().getName().equalsIgnoreCase(category);
		if (!matches) {
			System.out.println("Category mismatch for '" + quiz.getName() + "': Expected '" + category
					+ "', but found '" + quiz.getCategory().getName() + "'");
		}
		return matches;
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
		questionsLabel.setText("Questions: " + (quiz.getQuestions() != null ? quiz.getQuestions().size() : "0"));

		sampleQuestionText.setText("Here is an example of what you'll see!");
	}

	/**
	 * Start quiz button event.
	 */
	@FXML
	private void handleStartQuizClick(ActionEvent event) {
		if (currentQuiz != null) {
			startQuiz(currentQuiz);
		}
	}

	private void startQuiz(Quiz quiz) {
		currentQuestionIndex = 0;
		correctAnswersCount = 0;

		previewContent.setVisible(false);
		welcomeContent.setVisible(false);
		summaryContent.setVisible(false);
		questionContent.setVisible(true);
		feedbackBox.setVisible(false);

		activeQuizNameLabel.setText(quiz.getName());
		showCurrentQuestion();
	}

	private void showCurrentQuestion() {
		if (currentQuiz == null || currentQuiz.getQuestions().isEmpty()) {
			return;
		}
		// Ensure index is valid
		if (currentQuestionIndex < 0) {
			currentQuestionIndex = 0;
		} else if (currentQuestionIndex >= currentQuiz.getQuestions().size()) {
			currentQuestionIndex = currentQuiz.getQuestions().size() - 1;
		}

		// Hide feedback whenever a new question loads
		feedbackBox.setVisible(false);

		Question question = currentQuiz.getQuestions().get(currentQuestionIndex);
		questionText.setText(question.getQuestionText());

		// Create a list of possible answers (correct + wrong)
		List<String> allAnswers = new ArrayList<>();
		allAnswers.add(question.getCorrectAnswer());
		allAnswers.addAll(question.getWrongAnswers());

		// Randomize the answers
		Collections.shuffle(allAnswers);

		// Assign to the RadioButtons
		answer1.setText(allAnswers.get(0));
		answer2.setText(allAnswers.get(1));
		answer3.setText(allAnswers.get(2));
		answer4.setText(allAnswers.get(3));

		// Clear any previous selection
		answerGroup.selectToggle(null);

		// Update progress label
		int total = currentQuiz.getQuestions().size();
		progressLabel.setText(String.format("Question %d of %d", currentQuestionIndex + 1, total));
	}

	/**
	 * Check whether selected answer is correct, show feedback.
	 */
	@FXML
	private void handleSubmitAnswerClick(ActionEvent event) {
		if (answerGroup.getSelectedToggle() == null) {
			return; // no answer selected
		}

		RadioButton selectedButton = (RadioButton) answerGroup.getSelectedToggle();
		String selectedAnswer = selectedButton.getText();

		Question question = currentQuiz.getQuestions().get(currentQuestionIndex);
		boolean isCorrect = selectedAnswer.equals(question.getCorrectAnswer());

		if (isCorrect) {
			correctAnswersCount++;
		}

		showAnswerFeedback(isCorrect, question.getCorrectAnswer());
	}

	private void showAnswerFeedback(boolean correct, String correctAnswer) {
		feedbackBox.setVisible(true);
		if (correct) {
			feedbackBox.setStyle("-fx-background-color: #e8f5e9; -fx-background-radius: 5;");
			feedbackLabel.setText("Correct!");
			feedbackLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 16px;");
			correctAnswerLabel.setVisible(false);
		} else {
			feedbackBox.setStyle("-fx-background-color: #ffebee; -fx-background-radius: 5;");
			feedbackLabel.setText("Incorrect!");
			feedbackLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 16px;");
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
	 * (Optional) Return to quiz list from summary screen.
	 */
	@FXML
	private void handleBackToQuizzesClick(ActionEvent event) {
		summaryContent.setVisible(false);
		welcomeContent.setVisible(true);
	}

	// Public method to manually refresh quizzes if needed
	public void refresh() {
		loadQuizzes();
	}

	/**
	 * Create mock quizzes if the service is unavailable or returns nothing.
	 */
	private void createMockQuizzes() {
		List<Quiz> mockQuizzes = createAndReturnMockQuizzes();
		quizListView.setItems(FXCollections.observableArrayList(mockQuizzes));
	}

	/**
	 * Helper method to create a list of mock quizzes.
	 */
	private List<Quiz> createAndReturnMockQuizzes() {
		List<Quiz> mockQuizzes = new ArrayList<>();

		// Quiz 1
		Quiz quiz1 = new Quiz();
		quiz1.setId(1L);
		quiz1.setName("Basic Hebrew Words");
		Category category1 = new Category();
		category1.setId(1L);
		category1.setName("Basics");
		quiz1.setCategory(category1);
		quiz1.setDifficulty(Difficulty.EASY);

		Question q1 = new Question();
		q1.setId(1L);
		q1.setQuestionText("What is the translation of 'Hello' in Hebrew?");
		q1.setCorrectAnswer("שלום");
		q1.setWrongAnswers(Arrays.asList("להתראות", "תודה", "בבקשה"));
		quiz1.addQuestion(q1);

		Question q2 = new Question();
		q2.setId(2L);
		q2.setQuestionText("What is the translation of 'Thank you' in Hebrew?");
		q2.setCorrectAnswer("תודה");
		q2.setWrongAnswers(Arrays.asList("שלום", "להתראות", "בבקשה"));
		quiz1.addQuestion(q2);

		// Quiz 2
		Quiz quiz2 = new Quiz();
		quiz2.setId(2L);
		quiz2.setName("Food and Dining");
		Category category2 = new Category();
		category2.setId(2L);
		category2.setName("Food");
		quiz2.setCategory(category2);
		quiz2.setDifficulty(Difficulty.MEDIUM);

		Question q3 = new Question();
		q3.setId(3L);
		q3.setQuestionText("What is the translation of 'Food' in Hebrew?");
		q3.setCorrectAnswer("אוכל");
		q3.setWrongAnswers(Arrays.asList("מים", "לחם", "חלב"));
		quiz2.addQuestion(q3);

		Question q4 = new Question();
		q4.setId(4L);
		q4.setQuestionText("What is the translation of 'Water' in Hebrew?");
		q4.setCorrectAnswer("מים");
		q4.setWrongAnswers(Arrays.asList("אוכל", "לחם", "חלב"));
		quiz2.addQuestion(q4);

		// Quiz 3
		Quiz quiz3 = new Quiz();
		quiz3.setId(3L);
		quiz3.setName("Travel Essentials");
		Category category3 = new Category();
		category3.setId(3L);
		category3.setName("Travel");
		quiz3.setCategory(category3);
		quiz3.setDifficulty(Difficulty.HARD);

		Question q5 = new Question();
		q5.setId(5L);
		q5.setQuestionText("What is the translation of 'Passport' in Hebrew?");
		q5.setCorrectAnswer("דרכון");
		q5.setWrongAnswers(Arrays.asList("מלון", "מטוס", "מונית"));
		quiz3.addQuestion(q5);

		Question q6 = new Question();
		q6.setId(6L);
		q6.setQuestionText("What is the translation of 'Hotel' in Hebrew?");
		q6.setCorrectAnswer("מלון");
		q6.setWrongAnswers(Arrays.asList("דרכון", "מטוס", "מונית"));
		quiz3.addQuestion(q6);

		mockQuizzes.add(quiz1);
		mockQuizzes.add(quiz2);
		mockQuizzes.add(quiz3);

		return mockQuizzes;
	}

}
