package com.lingotower.ui.quiz;

import static com.lingotower.constants.QuizConstants.COMPLETION_PREFIX;
import static com.lingotower.constants.QuizConstants.QUIZ_PREFIX;

import java.util.List;

import org.slf4j.Logger;

import com.lingotower.dto.sentence.SentenceCompletionDTO;
import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;
import com.lingotower.model.Question;
import com.lingotower.model.Quiz;
import com.lingotower.service.CategoryService;
import com.lingotower.service.QuizService;
import com.lingotower.utils.CategoryMappingUtils;
import com.lingotower.utils.HebrewUtils;
import com.lingotower.utils.LoggingUtility;

/**
 * Service to generate quizzes, handling both word quizzes and sentence
 * completion quizzes
 */
public class QuizGenerator {

	private static final Logger logger = LoggingUtility.getLogger(QuizGenerator.class);

	private final QuizService quizService;
	private final CategoryService categoryService;

	public QuizGenerator(QuizService quizService, CategoryService categoryService) {
		this.quizService = quizService;
		this.categoryService = categoryService;
	}

	/**
	 * Generate a quiz with questions based on category and difficulty
	 * 
	 * @param quiz The quiz shell to populate with questions
	 * @return The quiz with questions, or null if generation failed
	 */
	public Quiz generateQuizWithQuestions(Quiz quiz) {
		if (quiz == null || quiz.getCategory() == null || quiz.getDifficulty() == null) {
			logger.error("Cannot generate quiz: Quiz or required properties are null");
			return null;
		}

		// Get category and difficulty from the quiz
		Long categoryId = quiz.getCategory().getId();
		String categoryName = quiz.getCategory().getName();
		String categoryTranslation = quiz.getCategory().getTranslation(); // Hebrew name if available
		String difficulty = quiz.getDifficulty().toString();
		String quizName = quiz.getName();

		List<Question> generatedQuestions = null;
		boolean isSentenceCompletionQuiz = quizName.startsWith(COMPLETION_PREFIX);

		try {
			if (isSentenceCompletionQuiz) {
				// Generate sentence completion quiz
				generatedQuestions = generateSentenceCompletionQuiz(categoryName, difficulty);

				// Set the quiz name using the appropriate category name
				// If the category has a Hebrew translation, use it for display if in Hebrew
				// mode
				if (categoryTranslation != null && !categoryTranslation.isEmpty()
						&& HebrewUtils.containsHebrew(categoryTranslation)) {
					quiz.setName(COMPLETION_PREFIX + categoryTranslation + " (" + difficulty + ")");
				} else {
					quiz.setName(COMPLETION_PREFIX + categoryName + " (" + difficulty + ")");
				}
			} else {
				// Generate regular quiz questions
				generatedQuestions = generateRegularQuiz(categoryId, difficulty);

				// Set the quiz name - use translation if available and we're in Hebrew mode
				if (categoryTranslation != null && !categoryTranslation.isEmpty()
						&& HebrewUtils.containsHebrew(categoryTranslation)) {
					quiz.setName(QUIZ_PREFIX + categoryTranslation + " (" + difficulty + ")");
				} else {
					quiz.setName(QUIZ_PREFIX + categoryName + " (" + difficulty + ")");
				}
			}

			// Process generated questions
			if (generatedQuestions != null && !generatedQuestions.isEmpty()) {
				// Replace the quiz's questions with the generated ones
				quiz.getQuestions().clear();
				for (Question question : generatedQuestions) {
					quiz.addQuestion(question);
				}

				return quiz;
			}

			return null;
		} catch (Exception e) {
			logger.error("Error generating quiz: {}", e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Creates a regular words quiz
	 */
	private List<Question> generateRegularQuiz(Long categoryId, String difficulty) {
		logger.info("Generating regular quiz with categoryId: {}, difficulty: {}", categoryId, difficulty);
		return quizService.generateQuiz(categoryId, difficulty);
	}

	/**
	 * Creates a sentence completion quiz
	 */
	private List<Question> generateSentenceCompletionQuiz(String categoryName, String difficulty) {
		// Determine which category name to use for the API call
		String apiCategoryName = categoryName;
		boolean usingFallback = false;

		// If the category name is in Hebrew
		if (HebrewUtils.containsHebrew(categoryName)) {
			logger.info("Category name is in Hebrew, mapping to English for API call");
			apiCategoryName = CategoryMappingUtils.mapCategoryName(categoryName, true, categoryService);
		}

		// Generate sentence completion questions using the appropriate category name
		logger.info("Starting sentence completion quiz with API category name: '{}', difficulty: {}", apiCategoryName,
				difficulty);

		List<SentenceCompletionDTO> sentences = quizService.generateSentenceCompletions(apiCategoryName, difficulty);

		if (sentences != null && !sentences.isEmpty()) {
			List<Question> questions = quizService.convertSentencesToQuestions(sentences);
			logger.info("Successfully converted {} sentences to questions", sentences.size());
			return questions;
		} else {
			logger.warn("Failed to get sentence completion questions. Using fallback method...");
			// Set the fallback flag
			usingFallback = true;
			// Fallback to sample questions
			List<Question> fallbackQuestions = FallbackQuizGenerator.createFallbackSentenceQuestions(categoryName,
					difficulty);

			// Mark the questions as fallback so UI can show a notification
			if (fallbackQuestions != null && !fallbackQuestions.isEmpty()) {
				for (Question q : fallbackQuestions) {
					// Add a custom property to mark this as a fallback question
					// We'll use the category name field to store this info
					// since it already exists and we can check for this special value
					if (q.getCategory() != null) {
						q.getCategory().setName(q.getCategory().getName() + "_FALLBACK");
					}
				}
			}

			return fallbackQuestions;
		}
	}

	/**
	 * Creates a sample quiz for UI display
	 */
	public Quiz createSampleQuiz(String categoryName, String difficultyName, boolean isSentenceCompletion) {
		// Create quiz
		Quiz quiz = new Quiz();
		quiz.setId(System.currentTimeMillis() + (isSentenceCompletion ? 1 : 0));

		// Set name with appropriate prefix
		String prefix = isSentenceCompletion ? COMPLETION_PREFIX : QUIZ_PREFIX;
		quiz.setName(prefix + categoryName + " (" + difficultyName + ")");

		// Set category
		Category category = new Category();
		category.setId(CategoryMappingUtils.getCategoryIdByName(categoryName));
		category.setName(categoryName);
		quiz.setCategory(category);

		// Set difficulty
		quiz.setDifficulty(Difficulty.valueOf(difficultyName));

		return quiz;
	}
}