package com.lingotower.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.lingotower.model.Category;
import com.lingotower.model.Word;

/**
 * Service for managing words.
 */
public class WordService extends BaseService {

	private final CategoryService categoryService;

	/**
	 * Constructor for WordService.
	 */
	public WordService() {
		super(); // Initialize the base service
		this.categoryService = new CategoryService();
		logger.debug("WordService initialized");
	}

	/**
	 * Constructor for WordService with dependency injection.
	 * 
	 * @param categoryService The CategoryService to use
	 */
	public WordService(CategoryService categoryService) {
		super(); // Initialize the base service
		this.categoryService = categoryService;
		logger.debug("WordService initialized with injected CategoryService");
	}

	/**
	 * Fetches all categories using the CategoryService.
	 * 
	 * @return A list of all categories
	 */
	public List<Category> getAllCategories() {
		return categoryService.getAllCategories();
	}

	/**
	 * Fetches all words from the server.
	 * 
	 * @return A list of all words or an empty list if none are found or an error
	 *         occurs
	 */
	public List<Word> getAllWords() {
		try {
			logger.info("Fetching all words");

			String url = buildUrl(WORDS_PATH);
			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<List<Word>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<Word>>() {
					});

			if (response.getStatusCode().is2xxSuccessful()) {
				List<Word> words = response.getBody();
				logger.info("Successfully retrieved {} words", words != null ? words.size() : 0);
				return words != null ? words : new ArrayList<>();
			} else {
				logger.error("Failed to retrieve words. Status code: {}", response.getStatusCode());
				return new ArrayList<>();
			}
		} catch (Exception e) {
			logger.error("Error fetching words: {}", e.getMessage(), e);
			return new ArrayList<>();
		}
	}

	/**
	 * Fetches words by category ID.
	 * 
	 * @param categoryId The category ID to fetch words for
	 * @return A list of words for the given category or an empty list
	 */
	public List<Word> getWordsByCategory(long categoryId) {
		try {
			logger.info("Fetching words for category ID: {}", categoryId);

			// Use the correct endpoint for words with translations
			String url = buildUrl(WORDS_PATH, "category", String.valueOf(categoryId), "translate");

			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<List<Word>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<Word>>() {
					});

			List<Word> words = response.getBody();

			if (words != null && !words.isEmpty()) {
				logger.info("Successfully retrieved {} words for category ID: {}", words.size(), categoryId);

				// Log sample of first word for debugging
				Word firstWord = words.get(0);
				logger.debug("Sample word: {}, Translation: {}, Category: {}, Difficulty: {}", firstWord.getWord(),
						firstWord.getTranslatedText(),
						firstWord.getCategory() != null ? firstWord.getCategory().getName() : "null",
						firstWord.getDifficulty());
			} else {
				logger.warn("No words found for category ID: {}", categoryId);
			}

			return words != null ? words : new ArrayList<>();
		} catch (Exception e) {
			logger.error("Error fetching words for category ID {}: {}", categoryId, e.getMessage(), e);
			return new ArrayList<>();
		}
	}

	/**
	 * Fetches a word by its ID.
	 * 
	 * @param id The ID of the word to fetch
	 * @return The word if found, null otherwise
	 */
	public Word getWordById(Long id) {
		try {
			logger.info("Fetching word with ID: {}", id);

			String url = buildUrl(WORDS_PATH, id.toString());
			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<Word> response = restTemplate.exchange(url, HttpMethod.GET, entity, Word.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				Word word = response.getBody();
				logger.info("Successfully retrieved word with ID: {}", id);
				return word;
			} else {
				logger.error("Failed to retrieve word with ID: {}. Status code: {}", id, response.getStatusCode());
				return null;
			}
		} catch (Exception e) {
			logger.error("Error fetching word with ID {}: {}", id, e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Creates a new word.
	 * 
	 * @param word The word to create
	 * @return The created word with its assigned ID, or null if creation failed
	 */
	public Word createWord(Word word) {
		try {
			logger.info("Creating new word: {}", word.getWord());

			String url = buildUrl(WORDS_PATH);
			HttpEntity<Word> entity = createAuthEntity(word);

			ResponseEntity<Word> response = restTemplate.exchange(url, HttpMethod.POST, entity, Word.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				Word createdWord = response.getBody();
				logger.info("Successfully created word with ID: {}",
						createdWord != null ? createdWord.getId() : "unknown");
				return createdWord;
			} else {
				logger.error("Failed to create word. Status code: {}", response.getStatusCode());
				return null;
			}
		} catch (Exception e) {
			logger.error("Error creating word: {}", e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Gets random words from a specific category and difficulty level.
	 * 
	 * @param categoryId The category ID
	 * @param difficulty The difficulty level
	 * @param sourceLang The source language
	 * @param targetLang The target language
	 * @return A list of random words
	 */
	public List<Word> getRandomWordsByCategory(long categoryId, String difficulty, String sourceLang,
			String targetLang) {
		try {
			logger.info("Fetching random words for category ID: {}, difficulty: {}", categoryId, difficulty);

			String url = buildUrl(WORDS_PATH, "category", String.valueOf(categoryId), "difficulty", difficulty,
					"random/translate") + "?sourceLang=" + sourceLang + "&targetLang=" + targetLang;

			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<List<Word>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<Word>>() {
					});

			List<Word> words = response.getBody();
			logger.info("Successfully retrieved {} random words", words != null ? words.size() : 0);
			return words != null ? words : new ArrayList<>();
		} catch (Exception e) {
			logger.error("Error fetching random words: {}", e.getMessage(), e);
			return new ArrayList<>();
		}
	}

	/**
	 * Updates an existing word.
	 * 
	 * @param id          The ID of the word to update
	 * @param wordDetails The updated word information
	 * @return The updated word or null if update failed
	 */
	public Word updateWord(Long id, Word wordDetails) {
		try {
			logger.info("Updating word with ID: {}", id);

			String url = buildUrl(WORDS_PATH, id.toString());
			HttpEntity<Word> entity = createAuthEntity(wordDetails);

			ResponseEntity<Word> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Word.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				Word updatedWord = response.getBody();
				logger.info("Successfully updated word with ID: {}", id);
				return updatedWord;
			} else {
				logger.error("Failed to update word with ID: {}. Status code: {}", id, response.getStatusCode());
				return null;
			}
		} catch (Exception e) {
			logger.error("Error updating word with ID {}: {}", id, e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Deletes a word by its ID.
	 * 
	 * @param id The ID of the word to delete
	 * @return true if deletion was successful, false otherwise
	 */
	public boolean deleteWord(Long id) {
		try {
			logger.info("Deleting word with ID: {}", id);

			// Note: Using the admin endpoint for word deletion
			String url = buildUrl(ADMINS_PATH, "word", id.toString());
			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);

			boolean success = response.getStatusCode().is2xxSuccessful();
			if (success) {
				logger.info("Successfully deleted word with ID: {}", id);
			} else {
				logger.error("Failed to delete word with ID: {}. Status code: {}", id, response.getStatusCode());
			}
			return success;
		} catch (Exception e) {
			logger.error("Error deleting word with ID {}: {}", id, e.getMessage(), e);
			return false;
		}
	}

	/**
	 * Gets the daily word.
	 * 
	 * @return The daily word or null if not available
	 */
	public Word getDailyWord() {
		try {
			logger.info("Fetching daily word");

			String url = buildUrl(WORDS_PATH, "daily");
			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<Word> response = restTemplate.exchange(url, HttpMethod.GET, entity, Word.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				Word dailyWord = response.getBody();
				logger.info("Successfully retrieved daily word: {}", dailyWord != null ? dailyWord.getWord() : "null");
				return dailyWord;
			} else {
				logger.error("Failed to retrieve daily word. Status code: {}", response.getStatusCode());
				return null;
			}
		} catch (Exception e) {
			logger.error("Error fetching daily word: {}", e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Uploads a JSON file containing words to the server.
	 * 
	 * @param jsonFile   The JSON file to upload
	 * @param categoryId The category ID to associate with the words
	 * @return A message from the server or null if failed
	 */
	public String uploadWordsJson(File jsonFile, Long categoryId) {
		try {
			logger.info("Uploading words from JSON file for category ID: {}", categoryId);

			// Get category name from ID
			Category category = null;
			for (Category c : getAllCategories()) {
				if (c.getId().equals(categoryId)) {
					category = c;
					break;
				}
			}

			if (category == null) {
				logger.error("Category not found for ID: {}", categoryId);
				return null;
			}

			// Create headers with authentication but without content type
			// (will be set automatically for multipart)
			HttpHeaders headers = createAuthHeaders();
			headers.remove(HttpHeaders.CONTENT_TYPE);

			// Create a MultiValueMap for the form data
			MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
			form.add("category", category.getName());
			form.add("file", new FileSystemResource(jsonFile));

			// Create HTTP entity with form data and headers
			HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(form, headers);

			// Make the POST request
			String url = buildUrl(WORDS_PATH, "upload");
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				String result = response.getBody();
				logger.info("Successfully uploaded words JSON: {}", result);
				return result;
			} else {
				logger.error("Failed to upload words JSON. Status code: {}", response.getStatusCode());
				return null;
			}
		} catch (Exception e) {
			logger.error("Error uploading words JSON: {}", e.getMessage(), e);
			return null;
		}
	}
}