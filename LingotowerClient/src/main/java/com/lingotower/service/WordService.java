package com.lingotower.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.lingotower.model.Word;

public class WordService extends BaseService {

	private static final String BASE_URL = "http://localhost:8080/words";

	public WordService() {
		super(); // Initialize the base service
	}

	public List<Word> getAllWords() {
		try {
			// Create headers with authentication
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Make the request to http://localhost:8080/words
			ResponseEntity<List<Word>> response = restTemplate.exchange(BASE_URL, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<Word>>() {
					});

			if (response.getStatusCode().is2xxSuccessful()) {
				return response.getBody() != null ? response.getBody() : new ArrayList<>();
			} else {
				System.err.println("Error response from server: " + response.getStatusCode());
				return new ArrayList<>();
			}
		} catch (Exception e) {
			System.err.println("Error fetching words: " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	/**
	 * Gets all words from a specific category
	 * 
	 * @param categoryId The category ID
	 * @return A list of words in the category or empty list if none found
	 */
	public List<Word> getWordsByCategory(long categoryId) {
		try {
			// Create headers with authentication
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Make the request to the endpoint with translation parameters
			String url = BASE_URL + "/category/" + categoryId + "/translate";

			System.out.println("Fetching words from category " + categoryId);

			// Use the correct response type for the endpoint
			ResponseEntity<List<Word>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<Word>>() {
					});

			List<Word> words = response.getBody();
			System.out.println("Received " + (words != null ? words.size() : 0) + " words from server");

			// Debug the first word if available
			if (words != null && !words.isEmpty()) {
				Word firstWord = words.get(0);
				System.out.println("First word: " + firstWord.getWord());
				System.out.println("Translation: " + firstWord.getTranslatedText());
				System.out.println(
						"Category: " + (firstWord.getCategory() != null ? firstWord.getCategory().getName() : "null"));
				System.out.println("Difficulty: " + firstWord.getDifficulty());
			}

			return words != null ? words : new ArrayList<>();
		} catch (Exception e) {
			System.err.println("Error fetching words by category: " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	// Get a word by ID
	public Word getWordById(Long id) {
		try {
			// Create headers with authentication
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Make the request
			String url = BASE_URL + "/" + id;
			ResponseEntity<Word> response = restTemplate.exchange(url, HttpMethod.GET, entity, Word.class);

			return response.getBody();
		} catch (Exception e) {
			System.err.println("Error getting word by ID: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	// Create a new word
	public Word createWord(Word word) {
		try {
			// Create headers with authentication
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<Word> entity = new HttpEntity<>(word, headers);

			// Make the request
			ResponseEntity<Word> response = restTemplate.exchange(BASE_URL, HttpMethod.POST, entity, Word.class);

			return response.getBody();
		} catch (Exception e) {
			System.err.println("Error creating word: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets random words from a specific category and difficulty level
	 */
	public List<Word> getRandomWordsByCategory(long categoryId, String difficulty, String sourceLang,
			String targetLang) {
		try {
			// Create headers with authentication
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Make the request to the endpoint
			String url = BASE_URL + "/category/" + categoryId + "/difficulty/" + difficulty
					+ "/random/translate?sourceLang=" + sourceLang + "&targetLang=" + targetLang;

			ResponseEntity<List<Word>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<Word>>() {
					});

			return response.getBody();
		} catch (Exception e) {
			System.err.println("Error fetching random words: " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	/**
	 * Gets words with translations from a specific category Updated to handle the
	 * correct response format
	 * 
	 * @param categoryId The category ID
	 * @return A list of words with translations
	 */
	public List<Word> getWordsByCategoryWithTranslation(long categoryId) {
		try {
			// Create headers with authentication
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Construct the full URL
			String url = BASE_URL + "/category/" + categoryId + "/translate";

			System.out.println("Fetching words from: " + url);

			// Use the correct ParameterizedTypeReference for the expected response format
			ResponseEntity<List<Word>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<Word>>() {
					});

			List<Word> words = response.getBody();

			if (words != null) {
				System.out.println("Successfully received " + words.size() + " words");
				// Print the first word if available for debugging
				if (!words.isEmpty()) {
					Word firstWord = words.get(0);
					System.out.println("Sample word: " + firstWord.getWord());
					System.out.println("Translation: " + firstWord.getTranslatedText());
					System.out.println("Category ID: "
							+ (firstWord.getCategory() != null ? firstWord.getCategory().getId() : "null"));
					System.out.println("Difficulty: " + firstWord.getDifficulty());
				}
			} else {
				System.out.println("No words received from server");
			}

			return words != null ? words : new ArrayList<>();
		} catch (Exception e) {
			System.err.println("Error fetching words with translations: " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public Word updateWord(Long id, Word wordDetails) {
		try {
			// Create headers with authentication
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<Word> entity = new HttpEntity<>(wordDetails, headers);

			// Make the request
			String url = BASE_URL + "/" + id;
			ResponseEntity<Word> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Word.class);

			return response.getBody();
		} catch (Exception e) {
			System.err.println("Error updating word: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public boolean deleteWord(Long id) {
		try {
			// Create headers with authentication
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Make the request
			String url = BASE_URL + "/" + id;
			ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);

			return response.getStatusCode().is2xxSuccessful();
		} catch (Exception e) {
			System.err.println("Error deleting word: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public Word getDailyWord() {
		try {
			// Create headers with authentication
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Make the request to the daily word endpoint
			String url = BASE_URL + "/daily";
			ResponseEntity<Word> response = restTemplate.exchange(url, HttpMethod.GET, entity, Word.class);

			return response.getBody();
		} catch (Exception e) {
			System.err.println("Error fetching daily word: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

}