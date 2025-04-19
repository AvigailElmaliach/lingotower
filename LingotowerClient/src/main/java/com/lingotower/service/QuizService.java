package com.lingotower.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lingotower.model.Question;
import com.lingotower.model.Quiz;
import com.lingotower.model.Sentence;

/**
 * Service for managing quizzes and quiz-related functionality.
 */
public class QuizService extends BaseService {

	/**
	 * Constructor for QuizService.
	 */
	public QuizService() {
		super(); // Initialize the base service
		logger.debug("QuizService initialized");
	}

	/**
	 * Fetches all quizzes from the server.
	 * 
	 * @return A list of all quizzes or an empty list if none are found or an error
	 *         occurs
	 */
	public List<Quiz> getAllQuizzes() {
		try {
			logger.info("Fetching all quizzes");

			String url = buildUrl(QUIZZES_PATH);
			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<List<Quiz>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<Quiz>>() {
					});

			if (response.getStatusCode().is2xxSuccessful()) {
				List<Quiz> quizzes = response.getBody();
				logger.info("Successfully retrieved {} quizzes", quizzes != null ? quizzes.size() : 0);
				return quizzes != null ? quizzes : new ArrayList<>();
			} else {
				logger.error("Failed to retrieve quizzes. Status code: {}", response.getStatusCode());
				return new ArrayList<>();
			}
		} catch (Exception e) {
			logger.error("Error fetching quizzes: {}", e.getMessage(), e);
			return new ArrayList<>();
		}
	}

	/**
	 * Fetches a quiz by its ID.
	 * 
	 * @param id The ID of the quiz to fetch
	 * @return The quiz if found, null otherwise
	 */
	public Quiz getQuizById(Long id) {
		try {
			logger.info("Fetching quiz with ID: {}", id);

			String url = buildUrl(QUIZZES_PATH, id.toString());
			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<Quiz> response = restTemplate.exchange(url, HttpMethod.GET, entity, Quiz.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				Quiz quiz = response.getBody();
				logger.info("Successfully retrieved quiz with ID: {}", id);
				return quiz;
			} else {
				logger.error("Failed to retrieve quiz with ID: {}. Status code: {}", id, response.getStatusCode());
				return null;
			}
		} catch (Exception e) {
			logger.error("Error fetching quiz with ID {}: {}", id, e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Creates a new quiz.
	 * 
	 * @param quiz The quiz to create
	 * @return The created quiz with its assigned ID, or null if creation failed
	 */
	public Quiz createQuiz(Quiz quiz) {
		try {
			logger.info("Creating new quiz: {}", quiz.getName());

			String url = buildUrl(QUIZZES_PATH);
			HttpEntity<Quiz> entity = createAuthEntity(quiz);

			ResponseEntity<Quiz> response = restTemplate.exchange(url, HttpMethod.POST, entity, Quiz.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				Quiz createdQuiz = response.getBody();
				logger.info("Successfully created quiz with ID: {}",
						createdQuiz != null ? createdQuiz.getId() : "unknown");
				return createdQuiz;
			} else {
				logger.error("Failed to create quiz. Status code: {}", response.getStatusCode());
				return null;
			}
		} catch (Exception e) {
			logger.error("Error creating quiz: {}", e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Generates questions for a quiz based on category and difficulty.
	 * 
	 * @param categoryId The category ID
	 * @param difficulty The difficulty level
	 * @return A list of generated questions or null if an error occurs
	 */
	public List<Question> generateQuiz(Long categoryId, String difficulty) {
		try {
			logger.info("Generating quiz for category ID: {}, difficulty: {}", categoryId, difficulty);

			String url = buildUrl(QUIZZES_PATH, "generate") + "?categoryId=" + categoryId + "&difficulty=" + difficulty;

			HttpEntity<?> entity = createAuthEntity(null);

			logger.debug("Calling API: {}", url);

			ResponseEntity<List<Question>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<Question>>() {
					});

			List<Question> questions = response.getBody();

			if (questions != null && !questions.isEmpty()) {
				logger.info("Successfully generated {} questions", questions.size());

				// Log sample question for debugging
				Question firstQuestion = questions.get(0);
				logger.debug("Sample question: {}, Correct answer: {}, Options size: {}",
						firstQuestion.getQuestionText(), firstQuestion.getCorrectAnswer(),
						firstQuestion.getOptions() != null ? firstQuestion.getOptions().size() : 0);

				return questions;
			} else {
				logger.warn("No questions generated for category ID: {} and difficulty: {}", categoryId, difficulty);
				return new ArrayList<>();
			}
		} catch (Exception e) {
			logger.error("Error generating quiz: {}", e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Generates sentence completion questions from the server.
	 * 
	 * @param categoryName The name of the category for the sentences
	 * @param difficulty   The difficulty level (EASY, MEDIUM, HARD)
	 * @return A list of Sentence objects, or null if an error occurs
	 */
	public List<Sentence> generateSentenceCompletions(String categoryName, String difficulty) {
		try {
			logger.info("Generating sentence completions for category: {}, difficulty: {}", categoryName, difficulty);

			// Properly encode the category name for URL
			String encodedCategory = java.net.URLEncoder.encode(categoryName,
					java.nio.charset.StandardCharsets.UTF_8.toString());

			String url = buildUrl(COMPLETION_PATH, "generate-multiple") + "?categoryName=" + encodedCategory
					+ "&difficulty=" + difficulty;

			HttpEntity<?> entity = createAuthEntity(null);

			logger.debug("Calling Sentence Completion API: {}", url);

			try {
				ResponseEntity<List<Sentence>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
						new ParameterizedTypeReference<List<Sentence>>() {
						});

				logger.debug("Response status: {}", response.getStatusCode());

				List<Sentence> sentences = response.getBody();

				if (sentences != null && !sentences.isEmpty()) {
					logger.info("Successfully generated {} sentence completion questions", sentences.size());

					// Log sample sentence for debugging
					Sentence firstSentence = sentences.get(0);
					logger.debug("Sample sentence: {}, Correct answer: {}, Options size: {}",
							firstSentence.getQuestionText(), firstSentence.getCorrectAnswer(),
							firstSentence.getOptions() != null ? firstSentence.getOptions().size() : 0);

					return sentences;
				} else {
					logger.warn("No sentence completions generated for category: {} and difficulty: {}", categoryName,
							difficulty);
					return new ArrayList<>();
				}
			} catch (org.springframework.web.client.HttpClientErrorException e) {
				logger.error("HTTP Client Error generating sentence completions: {} - {}", e.getStatusCode(),
						e.getResponseBodyAsString());
				return null;
			} catch (org.springframework.web.client.HttpServerErrorException e) {
				logger.error("HTTP Server Error generating sentence completions: {} - {}", e.getStatusCode(),
						e.getResponseBodyAsString());
				return null;
			}
		} catch (Exception e) {
			logger.error("Error generating sentence completions: {}", e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Converts a Sentence list to a Question list for use with the existing quiz
	 * UI.
	 * 
	 * @param sentences The list of sentence completion questions
	 * @return A list of Question objects
	 */
	public List<Question> convertSentencesToQuestions(List<Sentence> sentences) {
		if (sentences == null) {
			logger.error("Cannot convert sentences to questions: sentence list is null");
			return null;
		}

		if (sentences.isEmpty()) {
			logger.warn("Cannot convert sentences to questions: sentence list is empty");
			return new ArrayList<>();
		}

		logger.info("Converting {} Sentence objects to Questions", sentences.size());

		List<Question> questions = new ArrayList<>();

		// Convert each Sentence to a Question
		for (Sentence sentence : sentences) {
			try {
				Question question = new Question();

				// Set the question text
				question.setQuestionText(sentence.getQuestionText());

				// Set the correct answer
				question.setCorrectAnswer(sentence.getCorrectAnswer());

				// Set the options
				if (sentence.getOptions() != null && !sentence.getOptions().isEmpty()) {
					question.setOptions(sentence.getOptions());
				} else {
					logger.warn("Sentence has null or empty options: {}", sentence.getQuestionText());

					// Create default options if missing
					List<String> defaultOptions = new ArrayList<>();
					defaultOptions.add(sentence.getCorrectAnswer()); // At least include the correct answer

					// Add some dummy options
					defaultOptions.add("option");
					defaultOptions.add("word");
					defaultOptions.add("example");
					defaultOptions.add("blank");

					question.setOptions(defaultOptions);
				}

				// Set the category
				question.setCategory(sentence.getCategory());

				// Add to the list
				questions.add(question);
			} catch (Exception e) {
				logger.error("Error converting sentence to question: {}", e.getMessage(), e);
				// Continue with the next sentence
			}
		}

		logger.info("Successfully converted {} sentences to questions", questions.size());
		return questions;
	}
}