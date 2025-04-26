package com.lingotower.ui.quiz;

import static com.lingotower.constants.QuizConstants.COMPLETION_PREFIX;

import org.slf4j.Logger;

import com.lingotower.model.Quiz;
import com.lingotower.utils.LoggingUtility;

import javafx.scene.control.Label;

/**
 * Manages quiz results, scoring, and summary.
 */
public class QuizResultsManager {
	private static final Logger logger = LoggingUtility.getLogger(QuizResultsManager.class);

	// Summary components
	private final Label summaryLabel;

	// State
	private int correctAnswersCount;
	private Quiz currentQuiz;

	/**
	 * Constructs a new QuizResultsManager.
	 * 
	 * @param summaryLabel The label to show the quiz summary
	 */
	public QuizResultsManager(Label summaryLabel) {
		this.summaryLabel = summaryLabel;
		this.correctAnswersCount = 0;
	}

	/**
	 * Resets the quiz results for a new quiz.
	 * 
	 * @param quiz The new quiz
	 */
	public void resetForNewQuiz(Quiz quiz) {
		logger.debug("Resetting results for new quiz: {}", quiz.getName());
		this.correctAnswersCount = 0;
		this.currentQuiz = quiz;
	}

	/**
	 * Records a correct answer.
	 */
	public void recordCorrectAnswer() {
		correctAnswersCount++;
		logger.debug("Recorded correct answer. Total correct: {}", correctAnswersCount);
	}

	/**
	 * Gets the current number of correct answers.
	 * 
	 * @return The number of correct answers
	 */
	public int getCorrectAnswersCount() {
		return correctAnswersCount;
	}

	/**
	 * Displays the quiz summary with the final score.
	 */
	public void displaySummary() {
		if (currentQuiz == null || currentQuiz.getQuestions() == null) {
			logger.warn("Cannot display summary: quiz or questions are null");
			summaryLabel.setText("Quiz results unavailable");
			return;
		}

		int totalQuestions = currentQuiz.getQuestions().size();
		boolean isSentenceCompletionQuiz = currentQuiz.getName().startsWith(COMPLETION_PREFIX);

		// Generate appropriate summary text
		String summaryText = QuizUIHelper.generateSummaryText(correctAnswersCount, totalQuestions,
				isSentenceCompletionQuiz);

		summaryLabel.setText(summaryText);
		logger.info("Quiz completed with score: {}/{} ({}%)", correctAnswersCount, totalQuestions,
				(correctAnswersCount * 100) / totalQuestions);
	}

	/**
	 * Gets the current quiz.
	 * 
	 * @return The current quiz
	 */
	public Quiz getCurrentQuiz() {
		return currentQuiz;
	}
}