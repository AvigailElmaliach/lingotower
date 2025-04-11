package com.lingotower.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lingotower.model.Question;
import com.lingotower.model.Quiz;

public class QuizService extends BaseService {

	private static final String BASE_URL = "http://localhost:8080/quizzes";

	public QuizService() {
		super(); // This is critical - it initializes the restTemplate
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

}