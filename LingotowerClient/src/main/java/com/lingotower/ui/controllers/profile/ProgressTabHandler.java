package com.lingotower.ui.controllers.profile;

import java.util.List;

import org.slf4j.Logger;

import com.lingotower.dto.user.UserProgressDTO;
import com.lingotower.model.Category;
import com.lingotower.model.User;
import com.lingotower.model.Word;
import com.lingotower.service.CategoryService;
import com.lingotower.service.UserService;
import com.lingotower.service.WordService;
import com.lingotower.utils.LoggingUtility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

/**
 * Handles the Learning Progress tab functionality
 */
public class ProgressTabHandler {
	private static final Logger logger = LoggingUtility.getLogger(ProgressTabHandler.class);

	// UI Components
	private final PieChart progressChart;
	private final Label wordsLearnedLabel;
	private final Label totalWordsLabel;
	private final Label percentLabel;
	private final Label recommendationLabel1;
	private final Label recommendationLabel2;
	private final Label recommendationLabel3;

	// Services
	private final UserService userService;
	private final WordService wordService;
	private final CategoryService categoryService;

	/**
	 * Constructor with UI components
	 */
	public ProgressTabHandler(PieChart progressChart, Label wordsLearnedLabel, Label totalWordsLabel,
			Label percentLabel, Label recommendationLabel1, Label recommendationLabel2, Label recommendationLabel3) {

		this.progressChart = progressChart;
		this.wordsLearnedLabel = wordsLearnedLabel;
		this.totalWordsLabel = totalWordsLabel;
		this.percentLabel = percentLabel;
		this.recommendationLabel1 = recommendationLabel1;
		this.recommendationLabel2 = recommendationLabel2;
		this.recommendationLabel3 = recommendationLabel3;

		// Initialize services
		this.userService = new UserService();
		this.wordService = new WordService();
		this.categoryService = new CategoryService();

		// Initialize progress chart with empty data
		initializeEmptyChart();
	}

	/**
	 * Initialize empty chart
	 */
	private void initializeEmptyChart() {
		ObservableList<PieChart.Data> emptyChartData = FXCollections
				.observableArrayList(new PieChart.Data("Learned", 0), new PieChart.Data("To Learn", 100));
		progressChart.setData(emptyChartData);
		progressChart.setTitle("");
	}

	/**
	 * Sets the user for this handler
	 */
	public void setUser(User user) {
		// User is handled at load time
	}

	/**
	 * Loads progress data for the user
	 */
	public void loadData(User user) {
		try {
			logger.debug("Loading user progress data");

			// Load progress data
			loadProgressData();

			logger.debug("Progress tab data loaded successfully");
		} catch (Exception e) {
			logger.error("Error loading progress data: {}", e.getMessage(), e);
		}
	}

	/**
	 * Refreshes the progress data
	 */
	public void refreshData(User user) {
		loadData(user);
	}

	/**
	 * Loads progress data from services
	 */
	private void loadProgressData() {
		try {
			// Get user learning progress
			UserProgressDTO progressDTO = userService.getUserProgress();
			Double learningProgress = (progressDTO != null) ? progressDTO.getProgressPercentage() : 0.0;

			// Update progress chart
			updateProgressChart(learningProgress);

			// Get statistics
			updateStatistics(learningProgress);

			// Update recommendations
			updateRecommendations();

			logger.debug("Progress data loaded: learned={}, total={}, progress={}%", wordsLearnedLabel.getText(),
					totalWordsLabel.getText(), learningProgress);

		} catch (Exception e) {
			logger.error("Error loading progress data: {}", e.getMessage(), e);
		}
	}

	/**
	 * Updates the progress chart
	 */
	private void updateProgressChart(double learningProgress) {
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
				new PieChart.Data("Learned", learningProgress), new PieChart.Data("To Learn", 100 - learningProgress));
		progressChart.setData(pieChartData);
	}

	/**
	 * Updates statistics labels
	 */
	private void updateStatistics(double learningProgress) {
		// Get word counts
		List<Word> learnedWords = userService.getLearnedWords();
		int learnedWordsCount = learnedWords != null ? learnedWords.size() : 0;

		List<Word> allWords = wordService.getAllWords();
		int totalWordsCount = allWords != null ? allWords.size() : 0;

		// Update labels
		wordsLearnedLabel.setText(String.valueOf(learnedWordsCount));
		totalWordsLabel.setText(String.valueOf(totalWordsCount));
		percentLabel.setText(String.format("%.2f%%", learningProgress));
	}

	/**
	 * Updates recommendation labels
	 */
	private void updateRecommendations() {
		try {
			logger.debug("Updating learning recommendations");
			List<Category> categories = categoryService.getAllCategories();

			if (categories != null && !categories.isEmpty()) {
				// Basic recommendations
				recommendationLabel1.setText("• Complete the \"" + categories.get(0).getName() + "\" category");

				if (categories.size() > 1) {
					recommendationLabel2
							.setText("• Review words in the \"" + categories.get(1).getName() + "\" category");
				}

				recommendationLabel3.setText("• Try available quizzes to test your knowledge");
				logger.debug("Recommendations updated based on {} categories", categories.size());
			} else {
				logger.warn("No categories available for recommendations");
				setDefaultRecommendations();
			}
		} catch (Exception e) {
			logger.error("Error updating recommendations: {}", e.getMessage(), e);
			setDefaultRecommendations();
		}
	}

	/**
	 * Sets default recommendations when categories can't be loaded
	 */
	private void setDefaultRecommendations() {
		recommendationLabel1.setText("• Try exploring more categories");
		recommendationLabel2.setText("• Review words you've already learned");
		recommendationLabel3.setText("• Try available quizzes to test your knowledge");
	}
}