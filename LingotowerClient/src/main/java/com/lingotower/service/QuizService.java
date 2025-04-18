package com.lingotower.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lingotower.model.Question;
import com.lingotower.model.Quiz;
import com.lingotower.model.Sentence;

public class QuizService extends BaseService {

	private static final String BASE_URL = "http://localhost:8080/quizzes";
	private static final String COMPLETION_URL = "http://localhost:8080/completion-practice";

	public QuizService() {
		super(); // initializes the restTemplate
	}

	public List<Quiz> getAllQuizzes() {
		try {
			// Create an entity with auth headers
			HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());

			// Make the request
			ResponseEntity<List<Quiz>> response = restTemplate.exchange(BASE_URL, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<Quiz>>() {
					});

			return response.getBody();
		} catch (Exception e) {
			System.err.println("Error loading quizzes: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	// Method to create headers with authentication token
//	protected HttpHeaders createAuthHeaders() {
//		HttpHeaders headers = new HttpHeaders();
//		// Add the authentication token if available
//		if (TokenStorage.hasToken()) {
//			headers.set("Authorization", "Bearer " + TokenStorage.getToken());
//		}
//		return headers;
//	}

	public Quiz getQuizById(Long id) {
		String url = BASE_URL + "/" + id;
		ResponseEntity<Quiz> response = restTemplate.exchange(url, HttpMethod.GET, null, Quiz.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			return response.getBody();
		}
		return null;
	}

	public Quiz createQuiz(Quiz quiz) {
		ResponseEntity<Quiz> response = restTemplate.exchange(BASE_URL, HttpMethod.POST, new HttpEntity<>(quiz),
				Quiz.class);
		return response.getBody();
	}

	public List<Question> generateQuiz(Long categoryId, String difficulty) {
		try {
			// Create headers with authentication
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Construct the URL with parameters
			String url = BASE_URL + "/generate?categoryId=" + categoryId + "&difficulty=" + difficulty;

			System.out.println("Calling API: " + url);

			// Make the request
			ResponseEntity<List<Question>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<Question>>() {
					});

			List<Question> questions = response.getBody();

			// Log the response for debugging
			if (questions != null) {
				System.out.println("Received " + questions.size() + " questions from API");
				if (!questions.isEmpty()) {
					Question firstQuestion = questions.get(0);
					System.out.println("Sample question: " + firstQuestion.getQuestionText());
					System.out.println("Correct answer: " + firstQuestion.getCorrectAnswer());
					System.out.println("Options: " + firstQuestion.getOptions());
				}
			}

			return questions;
		} catch (Exception e) {
			System.err.println("Error generating quiz: " + e.getMessage());
			e.printStackTrace();
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
			// Create headers with authentication
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Properly encode the category name for URL
			String encodedCategory = java.net.URLEncoder.encode(categoryName,
					java.nio.charset.StandardCharsets.UTF_8.toString());

			// Construct the URL with parameters
			String url = COMPLETION_URL + "/generate-multiple?categoryName=" + encodedCategory + "&difficulty="
					+ difficulty;

			System.out.println("Calling Sentence Completion API: " + url);

			// Log the full request details for debugging
			System.out.println("Request headers:");
			headers.forEach((key, value) -> System.out.println(key + ": " + value));

			try {
				// Make the request
				ResponseEntity<List<Sentence>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
						new ParameterizedTypeReference<List<Sentence>>() {
						});

				// Log the response status
				System.out.println("Response status: " + response.getStatusCode());

				List<Sentence> sentences = response.getBody();

				// Log the response for debugging
				if (sentences != null) {
					System.out.println("Received " + sentences.size() + " sentence completion questions from API");
					if (!sentences.isEmpty()) {
						Sentence firstSentence = sentences.get(0);
						System.out.println("Sample sentence: " + firstSentence.getQuestionText());
						System.out.println("Correct answer: " + firstSentence.getCorrectAnswer());
						System.out.println("Options: " + firstSentence.getOptions());
					} else {
						System.out.println("WARNING: Received empty list of sentences from API");
					}
				} else {
					System.out.println("WARNING: Received null response body from API");
				}

				return sentences;
			} catch (org.springframework.web.client.HttpClientErrorException e) {
				// Handle specific HTTP errors
				System.err.println("HTTP Client Error: " + e.getStatusCode() + " - " + e.getStatusText());
				System.err.println("Response body: " + e.getResponseBodyAsString());
				return null;
			} catch (org.springframework.web.client.HttpServerErrorException e) {
				// Handle server errors
				System.err.println("HTTP Server Error: " + e.getStatusCode() + " - " + e.getStatusText());
				System.err.println("Response body: " + e.getResponseBodyAsString());
				return null;
			}
		} catch (Exception e) {
			System.err.println("Error generating sentence completions: " + e.getMessage());
			e.printStackTrace();
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
			System.err.println("Cannot convert sentences to questions: sentence list is null");
			return null;
		}

		if (sentences.isEmpty()) {
			System.err.println("Cannot convert sentences to questions: sentence list is empty");
			return new ArrayList<>();
		}

		System.out.println("Converting " + sentences.size() + " Sentence objects to Questions");

		// Create a new list to hold the converted questions
		java.util.List<Question> questions = new java.util.ArrayList<>();

		// Convert each Sentence to a Question
		for (Sentence sentence : sentences) {
			try {
				Question question = new Question();

				// Set the question text
				question.setQuestionText(sentence.getQuestionText());

				// Set the correct answer
				question.setCorrectAnswer(sentence.getCorrectAnswer());

				// Set the options
				if (sentence.getOptions() != null) {
					question.setOptions(sentence.getOptions());
				} else {
					System.err.println("Warning: Sentence has null options: " + sentence.getQuestionText());
					// Create default options if missing
					java.util.List<String> defaultOptions = new java.util.ArrayList<>();
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
				System.err.println("Error converting sentence to question: " + e.getMessage());
				e.printStackTrace();
				// Continue with the next sentence
			}
		}

		System.out.println("Successfully converted " + questions.size() + " sentences to questions");
		return questions;
	}
}