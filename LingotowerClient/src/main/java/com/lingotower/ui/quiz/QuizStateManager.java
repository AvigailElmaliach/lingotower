package com.lingotower.ui.quiz;

import org.slf4j.Logger;

import com.lingotower.model.Quiz;
import com.lingotower.utils.LoggingUtility;

import javafx.scene.layout.VBox;

/**
 * Manages the UI state of the quiz screens. Handles transitions between
 * different screens (welcome, preview, questions, summary).
 */
public class QuizStateManager {
	private static final Logger logger = LoggingUtility.getLogger(QuizStateManager.class);

	// Content containers
	private final VBox welcomeContent;
	private final VBox previewContent;
	private final VBox questionContent;
	private final VBox summaryContent;

	/**
	 * Constructs a new QuizStateManager.
	 * 
	 * @param welcomeContent  The welcome screen content
	 * @param previewContent  The quiz preview content
	 * @param questionContent The question content
	 * @param summaryContent  The summary content
	 */
	public QuizStateManager(VBox welcomeContent, VBox previewContent, VBox questionContent, VBox summaryContent) {
		this.welcomeContent = welcomeContent;
		this.previewContent = previewContent;
		this.questionContent = questionContent;
		this.summaryContent = summaryContent;
	}

	/**
	 * Initializes the UI state to show the welcome screen.
	 */
	public void initializeState() {
		logger.debug("Initializing quiz state to welcome screen");
		welcomeContent.setVisible(true);
		previewContent.setVisible(false);
		questionContent.setVisible(false);
		summaryContent.setVisible(false);
	}

	/**
	 * Shows the quiz preview screen for the selected quiz.
	 * 
	 * @param quiz The selected quiz
	 */
	public void showPreview(Quiz quiz) {
		logger.debug("Showing preview for quiz: {}", quiz.getName());
		welcomeContent.setVisible(false);
		previewContent.setVisible(true);
		questionContent.setVisible(false);
		summaryContent.setVisible(false);
	}

	/**
	 * Shows the question screen for an active quiz.
	 */
	public void showQuestions() {
		logger.debug("Showing questions screen");
		welcomeContent.setVisible(false);
		previewContent.setVisible(false);
		questionContent.setVisible(true);
		summaryContent.setVisible(false);
	}

	/**
	 * Shows the quiz summary screen with results.
	 */
	public void showSummary() {
		logger.debug("Showing summary screen");
		welcomeContent.setVisible(false);
		previewContent.setVisible(false);
		questionContent.setVisible(false);
		summaryContent.setVisible(true);
	}

	/**
	 * Returns to the welcome screen.
	 */
	public void returnToWelcome() {
		logger.debug("Returning to welcome screen");
		welcomeContent.setVisible(true);
		previewContent.setVisible(false);
		questionContent.setVisible(false);
		summaryContent.setVisible(false);
	}

	/**
	 * Indicates if the question screen is currently active.
	 * 
	 * @return true if question screen is visible
	 */
	public boolean isQuestionScreenActive() {
		return questionContent.isVisible();
	}
}