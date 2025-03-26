//package com.lingotower.service;
//
//import java.util.List;
//
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//
//import com.lingotower.model.Word;
//
//public class WordService {
//
//	private static final String BASE_URL = "http://localhost:8080/words";
//	private RestTemplate restTemplate;
//
//	public WordService() {
//		this.restTemplate = new RestTemplate();
//	}
//
//	// שליפת כל המילים
//	public List<Word> getAllWords() {
//		ResponseEntity<List> response = restTemplate.exchange(BASE_URL, HttpMethod.GET, null, List.class);
//		return response.getBody();
//	}
//
//	// שליפת מילה לפי ID
//	public Word getWordById(Long id) {
//		String url = BASE_URL + "/" + id;
//		ResponseEntity<Word> response = restTemplate.exchange(url, HttpMethod.GET, null, Word.class);
//		if (response.getStatusCode() == HttpStatus.OK) {
//			return response.getBody();
//		}
//		return null;
//	}
//
//	// יצירת מילה חדשה
//	public Word createWord(Word word) {
//		ResponseEntity<Word> response = restTemplate.exchange(BASE_URL, HttpMethod.POST, new HttpEntity<>(word),
//				Word.class);
//		return response.getBody(); // מחזירים את המילה שנוצרה
//	}
//
//	// עדכון מילה לפי ID
//	public Word updateWord(Long id, Word wordDetails) {
//		String url = BASE_URL + "/" + id;
//		ResponseEntity<Word> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(wordDetails),
//				Word.class);
//		return response.getBody(); // מחזירים את המילה המעודכנת
//	}
//
//	// מחיקת מילה לפי ID
//	public boolean deleteWord(Long id) {
//		String url = BASE_URL + "/" + id;
//		ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
//		return response.getStatusCode() == HttpStatus.NO_CONTENT; // מחזירים true אם נמחק בהצלחה
//	}
//}

package com.lingotower.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.lingotower.model.Word;

public class WordService extends BaseService {

	private static final String BASE_URL = "http://localhost:8080/words";

	public WordService() {
		super(); // Initialize the base service
	}

	// Get all words
	public List<Word> getAllWords() {
		try {
			System.out.println("Fetching all words from server...");

			// Create headers with authentication
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Make the request
			System.out.println("Sending request to: " + BASE_URL);
			ResponseEntity<List<Word>> response = restTemplate.exchange(BASE_URL, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<Word>>() {
					});

			List<Word> words = response.getBody();
			System.out.println("Received " + (words != null ? words.size() : 0) + " words from server");

			if (words != null && !words.isEmpty()) {
				System.out.println("First word: " + words.get(0).getWord());
			}

			return words;
		} catch (Exception e) {
			System.err.println("Error getting all words: " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	// Get words for a specific category
	public List<Word> getWordsByCategory(Long categoryId) {
		try {
			// Create headers with authentication
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Make the request to a hypothetical endpoint - might need to add this to
			// the server
			String url = BASE_URL + "/category/" + categoryId;

			ResponseEntity<List<Word>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<Word>>() {
					});

			return response.getBody();
		} catch (Exception e) {
			System.err.println("Error getting words for category: " + e.getMessage());
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
			headers.setContentType(MediaType.APPLICATION_JSON);

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

	// Add a word with translation
	public Word addWordWithTranslation(String word, String sourceLang) {
		try {
			// Create headers with authentication
			HttpHeaders headers = createAuthHeaders();

			// Create the request URL with parameters
			String url = BASE_URL + "/add?word=" + word + "&sourceLang=" + sourceLang;

			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Make the request
			ResponseEntity<Word> response = restTemplate.exchange(url, HttpMethod.POST, entity, Word.class);

			return response.getBody();
		} catch (Exception e) {
			System.err.println("Error adding word with translation: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
}
