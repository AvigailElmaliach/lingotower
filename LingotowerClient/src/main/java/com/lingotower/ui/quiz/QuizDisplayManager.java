package com.lingotower.ui.quiz;

import static com.lingotower.constants.QuizConstants.BLANK_PLACEHOLDER;
import static com.lingotower.constants.QuizConstants.COMPLETION_PREFIX;
import static com.lingotower.constants.QuizConstants.STYLE_TEXT_FILL_GREEN;
import static com.lingotower.constants.QuizConstants.STYLE_TEXT_FILL_RED;

import org.slf4j.Logger;

import com.lingotower.model.Question;
import com.lingotower.model.Quiz;
import com.lingotower.utils.LoggingUtility;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Manages the display of quiz questions, answers, and feedback. Handles the UI
 * for showing quiz preview, questions, and feedback.
 */
public class QuizDisplayManager {
	private static final Logger logger = LoggingUtility.getLogger(QuizDisplayManager.class);

	// Preview elements
	private final PreviewComponents previewComponents;

	// Question elements
	private final QuestionComponents questionComponents;

	// Feedback elements
	private final FeedbackComponents feedbackComponents;

	/**
	 * Constructs a new QuizDisplayManager with component groups.
	 * 
	 * @param previewComponents  The preview components group
	 * @param questionComponents The question components group
	 * @param feedbackComponents The feedback components group
	 */
	public QuizDisplayManager(PreviewComponents previewComponents, QuestionComponents questionComponents,
			FeedbackComponents feedbackComponents) {

		this.previewComponents = previewComponents;
		this.questionComponents = questionComponents;
		this.feedbackComponents = feedbackComponents;
	}

	/**
	 * Displays the quiz preview information.
	 * 
	 * @param quiz The selected quiz to preview
	 */
	public void displayQuizPreview(Quiz quiz) {
		// Check if this is a sentence completion quiz
		boolean isSentenceCompletionQuiz = quiz.getName().startsWith(COMPLETION_PREFIX);

		// Populate preview
		previewComponents.quizNameLabel.setText(quiz.getName());
		previewComponents.categoryLabel
				.setText("Category: " + (quiz.getCategory() != null ? quiz.getCategory().getName() : "N/A"));
		previewComponents.difficultyLabel
				.setText("Difficulty: " + (quiz.getDifficulty() != null ? quiz.getDifficulty().toString() : "N/A"));

		// Set appropriate description based on quiz type
		if (isSentenceCompletionQuiz) {
			previewComponents.sampleQuestionText
					.setText("This quiz will test your language skills with sentence completion exercises. "
							+ "You'll be shown sentences with missing words and asked to select the correct word "
							+ "to fill in the blank.");
		} else {
			previewComponents.sampleQuestionText
					.setText("This quiz will generate random vocabulary questions based on the selected category "
							+ "and difficulty level. Test your language knowledge!");
		}

		logger.debug("Preview displayed for quiz: {}", quiz.getName());
	}

	/**
	 * Prepares the question screen to display loading state.
	 * 
	 * @param isLoading Whether the quiz is currently loading
	 */
	public void setLoadingState(boolean isLoading) {
		if (isLoading) {
			// Show loading state
			questionComponents.activeQuizNameLabel.setText("Loading quiz questions...");
			questionComponents.questionText.setText("Loading...");

			// Disable controls while loading
			questionComponents.answer1.setDisable(true);
			questionComponents.answer2.setDisable(true);
			questionComponents.answer3.setDisable(true);
			questionComponents.answer4.setDisable(true);
			questionComponents.answer5.setDisable(true);

			// Hide feedback box
			feedbackComponents.feedbackBox.setVisible(false);
		} else {
			// Reset controls state
			questionComponents.answer1.setDisable(false);
			questionComponents.answer2.setDisable(false);
			questionComponents.answer3.setDisable(false);
			questionComponents.answer4.setDisable(false);
			questionComponents.answer5.setDisable(false);
		}
	}

	/**
	 * Sets the name of the active quiz.
	 * 
	 * @param quizName The name of the active quiz
	 */
	public void setActiveQuizName(String quizName) {
		questionComponents.activeQuizNameLabel.setText(quizName);
	}

	/**
	 * Displays the current question.
	 * 
	 * @param question       The question to display
	 * @param currentIndex   The current question index (0-based)
	 * @param totalQuestions The total number of questions
	 */
	public void displayQuestion(Question question, int currentIndex, int totalQuestions) {
		// Check if this is a sentence completion question
		boolean isSentenceCompletion = question.getQuestionText().contains(BLANK_PLACEHOLDER);

		// Update question text and style it appropriately
		questionComponents.questionText.setText(question.getQuestionText());
		questionComponents.questionText.setStyle("-fx-text-fill: black;"); // Reset style

		if (isSentenceCompletion) {
			// Add special styling for sentence completion questions
			questionComponents.questionText.getStyleClass().add("sentence-completion");
		} else {
			// Remove the special styling if not a sentence completion
			questionComponents.questionText.getStyleClass().removeAll("sentence-completion");
		}

		// Set up the answer options
		QuizUIHelper.setUpAnswers(question, questionComponents.answer1, questionComponents.answer2,
				questionComponents.answer3, questionComponents.answer4, questionComponents.answer5);

		// Clear previous selection
		questionComponents.answerGroup.selectToggle(null);

		// Hide feedback
		feedbackComponents.feedbackBox.setVisible(false);

		// Update progress label and bar
		double progress = (double) (currentIndex + 1) / totalQuestions;
		questionComponents.progressBar.setProgress(progress);
		questionComponents.progressLabel.setText(String.format("Question %d of %d", currentIndex + 1, totalQuestions));

		logger.debug("Displayed question {}/{}: {}", currentIndex + 1, totalQuestions,
				question.getQuestionText().length() > 30 ? question.getQuestionText().substring(0, 30) + "..."
						: question.getQuestionText());
	}

	/**
	 * Processes the selected answer and displays feedback.
	 * 
	 * @param question The current question
	 * @return true if the answer was correct, false otherwise
	 */
	public boolean processAnswer(Question question) {
		if (questionComponents.answerGroup.getSelectedToggle() == null) {
			// No answer selected
			return false;
		}

		RadioButton selectedButton = (RadioButton) questionComponents.answerGroup.getSelectedToggle();
		String selectedAnswer = selectedButton.getText();

		boolean isCorrect = selectedAnswer.equals(question.getCorrectAnswer());
		logger.debug("Selected answer: {}, Correct: {}", selectedAnswer, isCorrect);

		return isCorrect;
	}

	/**
	 * Shows feedback for the selected answer.
	 * 
	 * @param question       The current question
	 * @param selectedAnswer The selected answer
	 * @param isCorrect      Whether the answer is correct
	 */
	public void showAnswerFeedback(Question question, String selectedAnswer, boolean isCorrect) {
		RadioButton selectedButton = (RadioButton) questionComponents.answerGroup.getSelectedToggle();

		if (isCorrect) {
			selectedButton.setStyle(STYLE_TEXT_FILL_GREEN); // Green for selected answer
		} else {
			selectedButton.setStyle(STYLE_TEXT_FILL_RED); // Red for selected answer
		}

		// Show feedback
		QuizUIHelper.showAnswerFeedback(isCorrect, question.getCorrectAnswer(), feedbackComponents.feedbackBox,
				feedbackComponents.feedbackLabel, feedbackComponents.correctAnswerLabel, question);

		// If this is a sentence completion question, highlight the completed sentence
		boolean isSentenceCompletion = question.getQuestionText().contains(BLANK_PLACEHOLDER);
		if (isSentenceCompletion) {
			// Replace the blank with the selected answer and show it in the question text
			String completedSentence = QuizUIHelper.getCompleteSentence(question.getQuestionText(), selectedAnswer);
			// Set a different color based on correctness
			if (isCorrect) {
				questionComponents.questionText.setStyle(STYLE_TEXT_FILL_GREEN); // Green for correct
			} else {
				questionComponents.questionText.setStyle(STYLE_TEXT_FILL_RED); // Red for incorrect
			}
			questionComponents.questionText.setText(completedSentence);
		}

		// Create a Timeline to reset the color after a delay
		Timeline resetColorTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
			questionComponents.questionText.setStyle("-fx-text-fill: black;"); // Reset to black
			selectedButton.setStyle("-fx-text-fill: black;"); // Reset to black
		}));

		resetColorTimeline.setCycleCount(1); // Run only once
		resetColorTimeline.play();

		logger.debug("Displayed feedback for answer: {} (correct: {})", selectedAnswer, isCorrect);
	}

	/**
	 * Gets the selected answer text.
	 * 
	 * @return The selected answer text or null if none selected
	 */
	public String getSelectedAnswer() {
		if (questionComponents.answerGroup.getSelectedToggle() == null) {
			return null;
		}
		return ((RadioButton) questionComponents.answerGroup.getSelectedToggle()).getText();
	}

	/**
	 * Container class for quiz preview UI components
	 */
	public static class PreviewComponents {
		public final Label quizNameLabel;
		public final Label categoryLabel;
		public final Label difficultyLabel;
		public final Label sampleQuestionText;

		public PreviewComponents(Label quizNameLabel, Label categoryLabel, Label difficultyLabel,
				Label sampleQuestionText) {
			this.quizNameLabel = quizNameLabel;
			this.categoryLabel = categoryLabel;
			this.difficultyLabel = difficultyLabel;
			this.sampleQuestionText = sampleQuestionText;
		}
	}

	/**
	 * Container class for quiz question UI components
	 */
	public static class QuestionComponents {
		public final Label activeQuizNameLabel;
		public final Label progressLabel;
		public final ProgressBar progressBar;
		public final Label questionText;
		public final ToggleGroup answerGroup;
		public final RadioButton answer1;
		public final RadioButton answer2;
		public final RadioButton answer3;
		public final RadioButton answer4;
		public final RadioButton answer5;

		public QuestionComponents(Label activeQuizNameLabel, Label progressLabel, ProgressBar progressBar,
				Label questionText, ToggleGroup answerGroup, RadioButton answer1, RadioButton answer2,
				RadioButton answer3, RadioButton answer4, RadioButton answer5) {
			this.activeQuizNameLabel = activeQuizNameLabel;
			this.progressLabel = progressLabel;
			this.progressBar = progressBar;
			this.questionText = questionText;
			this.answerGroup = answerGroup;
			this.answer1 = answer1;
			this.answer2 = answer2;
			this.answer3 = answer3;
			this.answer4 = answer4;
			this.answer5 = answer5;
		}
	}

	/**
	 * Container class for quiz feedback UI components
	 */
	public static class FeedbackComponents {
		public final VBox feedbackBox;
		public final Label feedbackLabel;
		public final Label correctAnswerLabel;

		public FeedbackComponents(VBox feedbackBox, Label feedbackLabel, Label correctAnswerLabel) {
			this.feedbackBox = feedbackBox;
			this.feedbackLabel = feedbackLabel;
			this.correctAnswerLabel = correctAnswerLabel;
		}
	}
}