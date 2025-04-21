package com.lingotower.ui.quiz;

import static com.lingotower.constants.QuizConstants.BLANK_PLACEHOLDER;
import static com.lingotower.constants.QuizConstants.STYLE_TEXT_FILL_GREEN;
import static com.lingotower.constants.QuizConstants.STYLE_TEXT_FILL_RED;

import java.util.List;

import org.slf4j.Logger;

import com.lingotower.model.Question;
import com.lingotower.utils.LoggingUtility;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.VBox;

/**
 * Helper class for Quiz-related UI operations
 */
public class QuizUIHelper {

	private static final Logger logger = LoggingUtility.getLogger(QuizUIHelper.class);

	private QuizUIHelper() {
		// Private constructor to prevent instantiation
		throw new UnsupportedOperationException("Helper class cannot be instantiated");
	}

	/**
	 * Shows the answer feedback UI
	 */
	public static void showAnswerFeedback(boolean correct, String correctAnswer, VBox feedbackBox, Label feedbackLabel,
			Label correctAnswerLabel, Question currentQuestion) {

		feedbackBox.setVisible(true);
		feedbackBox.getStyleClass().removeAll("correct", "incorrect");

		// Check if it's a sentence completion
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
	public static String getCompleteSentence(String question, String answer) {
		if (question == null || answer == null) {
			return question;
		}

		// Replace the blank with the correct answer
		return question.replace(BLANK_PLACEHOLDER, answer);
	}

	/**
	 * Set up radio button answers for a question
	 */
	public static void setUpAnswers(Question question, RadioButton answer1, RadioButton answer2, RadioButton answer3,
			RadioButton answer4, RadioButton answer5) {

		// Get all options for this question
		List<String> options = question.getOptions();

		if (options == null || options.isEmpty()) {
			logger.warn("No answer options available for question: {}", question.getQuestionText());
			return;
		}

		// Update the radio buttons
		answer1.setText(options.get(0));
		answer2.setText(options.get(1));
		answer3.setText(options.get(2));
		answer4.setText(options.get(3));

		// Check if we have a 5th option (some questions might only have 4)
		if (options.size() > 4) {
			answer5.setText(options.get(4));
			answer5.setVisible(true);
		} else {
			answer5.setVisible(false);
		}
	}

	/**
	 * Display an error message dialog
	 */
	public static void showError(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	/**
	 * Display a warning message dialog
	 */
	public static void showWarning(String title, String message) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	/**
	 * Generate a quiz summary message
	 */
	public static String generateSummaryText(int correctAnswersCount, int totalQuestions,
			boolean isSentenceCompletionQuiz) {
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

		return summaryBuilder.toString();
	}
}