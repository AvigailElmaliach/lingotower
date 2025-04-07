package com.lingotower.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.lingotower.dto.UserUpdateDTO;
import com.lingotower.model.User;
import com.lingotower.model.Word;

public class UserService extends BaseService {

	private static final String BASE_URL = "http://localhost:8080/users";

	public UserService() {
		super(); // Initialize the base service with RestTemplate and error handler
	}

	public List<User> getAllUsers() {
		try {
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			ResponseEntity<List<User>> response = restTemplate.exchange(BASE_URL, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<User>>() {
					});

			return response.getBody();
		} catch (Exception e) {
			System.err.println("Error getting all users: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	public User getUserById(Long id) {
		try {
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			String url = BASE_URL + "/" + id;
			ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.GET, entity, User.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				return response.getBody();
			}
			return null;
		} catch (Exception e) {
			System.err.println("Error getting user by ID: " + e.getMessage());
			return null;
		}
	}

	public User createUser(User user) {
		try {
			HttpHeaders headers = createAuthHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<User> entity = new HttpEntity<>(user, headers);

			ResponseEntity<User> response = restTemplate.exchange(BASE_URL, HttpMethod.POST, entity, User.class);

			return response.getBody();
		} catch (Exception e) {
			System.err.println("Error creating user: " + e.getMessage());
			return null;
		}
	}

	public boolean updateUser(User user) {
		try {
			// Create a DTO with only the fields that can be updated
			UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
			userUpdateDTO.setUsername(user.getUsername());
			userUpdateDTO.setEmail(user.getEmail());
			userUpdateDTO.setLanguage(user.getLanguage());

			// Set up headers with authentication and content type
			HttpHeaders headers = createAuthHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			// Create HTTP entity with the DTO and headers
			HttpEntity<UserUpdateDTO> entity = new HttpEntity<>(userUpdateDTO, headers);

			// Make the PUT request to update the user
			String url = BASE_URL + "/" + user.getId();
			ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);

			// Return true if successful (HTTP 200 OK)
			return response.getStatusCode() == HttpStatus.OK;
		} catch (Exception e) {
			System.err.println("Error updating user: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteUser(Long id) {
		try {
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			String url = BASE_URL + "/" + id;
			ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);

			return response.getStatusCode() == HttpStatus.NO_CONTENT;
		} catch (Exception e) {
			System.err.println("Error deleting user: " + e.getMessage());
			return false;
		}
	}

	public Double getUserLearningProgress(Long userId) {
		try {
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			String url = BASE_URL + "/" + userId + "/progress";
			ResponseEntity<Double> response = restTemplate.exchange(url, HttpMethod.GET, entity, Double.class);

			return response.getBody();
		} catch (Exception e) {
			System.err.println("Error getting user learning progress: " + e.getMessage());
			return 0.0;
		}
	}

	/**
	 * Gets learned words for the authenticated user
	 * 
	 * @return List of learned words
	 */
	public List<Word> getLearnedWords() {
		try {
			// Create headers with authentication
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Make the request to the endpoint - no user ID needed
			String url = BASE_URL + "/users/learned";

			ResponseEntity<List<Word>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<Word>>() {
					});

			return response.getBody();
		} catch (Exception e) {
			System.err.println("Error fetching learned words: " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public List<Word> getLearnedWordsByCategory(Long userId, Long categoryId) {
		try {
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			String url = BASE_URL + "/" + userId + "/learned-words/category/" + categoryId;
			ResponseEntity<List<Word>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<Word>>() {
					});

			return response.getBody();
		} catch (Exception e) {
			System.err.println("Error getting learned words by category: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	/**
	 * Adds a word to the authenticated user's learned words list
	 * 
	 * @param wordId The ID of the word to add
	 * @return true if successful, false otherwise
	 */
	public boolean addWordToLearned(Long wordId) {
		try {
			// Create headers with authentication
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Make the POST request - note that we don't include a user ID
			// The endpoint will determine the user from the JWT token
			String url = BASE_URL + "/users/learned/" + wordId;

			ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);

			return response.getStatusCode().is2xxSuccessful();
		} catch (Exception e) {
			System.err.println("Error adding word to learned: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public boolean removeLearnedWord(Long userId, Long wordId) {
		try {
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			String url = BASE_URL + "/" + userId + "/learned-word/" + wordId;
			ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);

			return response.getStatusCode() == HttpStatus.OK;
		} catch (Exception e) {
			System.err.println("Error removing learned word: " + e.getMessage());
			return false;
		}
	}
}