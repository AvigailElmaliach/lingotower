package com.lingotower.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingotower.dto.user.PasswordUpdateDTO;
import com.lingotower.dto.user.UserProgressDTO;
import com.lingotower.dto.user.UserUpdateDTO;
import com.lingotower.model.User;
import com.lingotower.model.Word;
import com.lingotower.security.TokenStorage;

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
			// Validate user object
			if (user == null) {
				System.err.println("Cannot update user: user object is null");
				return false;
			}

			if (user.getId() == null) {
				System.err.println("Cannot update user: user ID is null");
				return false;
			}

			// Validate required fields
			if (user.getUsername() == null || user.getUsername().trim().isEmpty() || user.getEmail() == null
					|| user.getEmail().trim().isEmpty() || user.getLanguage() == null
					|| user.getLanguage().trim().isEmpty()) {
				System.err.println("Cannot update user: required fields missing");
				return false;
			}

			// Create a DTO with the required fields
			UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
			userUpdateDTO.setUsername(user.getUsername());
			userUpdateDTO.setEmail(user.getEmail());
			userUpdateDTO.setSourceLanguage(user.getLanguage()); // Map to sourceLanguage

			// Convert the DTO to JSON for inspection
			ObjectMapper mapper = new ObjectMapper();
			String requestBody = "";
			try {
				requestBody = mapper.writeValueAsString(userUpdateDTO);
				System.out.println("Request JSON body: " + requestBody);
			} catch (Exception e) {
				System.err.println("Error converting DTO to JSON: " + e.getMessage());
				// Continue anyway since RestTemplate will handle serialization
			}

			// Set up headers with authentication and content type
			HttpHeaders headers = createAuthHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			// Print headers for debugging
			System.out.println("Request headers:");
			headers.forEach((key, value) -> System.out.println("  " + key + ": " + value));

			// Create HTTP entity with the DTO and headers
			HttpEntity<UserUpdateDTO> entity = new HttpEntity<>(userUpdateDTO, headers);

			// Construct the URL with the user ID
			String url = BASE_URL + "/" + user.getId();

			// Log the full request for debugging
			System.out.println("Updating user profile:");
			System.out.println("  URL: " + url);
			System.out.println("  Method: PUT");
			System.out.println("  User ID: " + user.getId());
			System.out.println("  Username: " + userUpdateDTO.getUsername());
			System.out.println("  Email: " + userUpdateDTO.getEmail());
			System.out.println("  SourceLanguage: " + userUpdateDTO.getSourceLanguage());

			// Make the PUT request to update the user
			try {
				ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);

				// Log the response for debugging
				System.out.println("User update response: " + response.getStatusCode());

				// Return true if successful (HTTP 200 OK)
				return response.getStatusCode().is2xxSuccessful();
			} catch (Exception e) {
				System.err.println("HTTP request error: " + e.getMessage());
				if (e instanceof org.springframework.web.client.HttpStatusCodeException) {
					org.springframework.web.client.HttpStatusCodeException httpEx = (org.springframework.web.client.HttpStatusCodeException) e;
					System.err.println("HTTP Status: " + httpEx.getStatusCode());
					System.err.println("Response body: " + httpEx.getResponseBodyAsString());
				}
				throw e; // Re-throw to be caught by outer catch
			}
		} catch (Exception e) {
			System.err.println("Error updating user: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteUser(Long id) {
		if (id == null) {
			System.err.println("Error: Cannot delete user with null ID");
			return false;
		}

		try {
			// Log the action
			System.out.println("UserService: Attempting to delete user with ID: " + id);

			// Get authentication headers
			HttpHeaders headers = createAuthHeaders();

			// Log headers for debugging
			System.out.println("Request headers:");
			headers.forEach((key, values) -> {
				System.out.println(key + ": " + String.join(", ", values));
			});

			// Create the HTTP entity with headers
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Construct the URL
			String url = BASE_URL + "/" + id;
			System.out.println("Delete URL: " + url);

			// Make the DELETE request
			ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);

			// Log the response
			boolean success = response.getStatusCode() == HttpStatus.NO_CONTENT;
			System.out.println("Delete response status: " + response.getStatusCode());
			System.out.println("Delete operation successful: " + success);

			return success;
		} catch (Exception e) {
			System.err.println("Error deleting user with ID " + id + ": " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public UserProgressDTO getUserProgress() {
		try {
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			String url = BASE_URL + "/progress";
			ResponseEntity<UserProgressDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					UserProgressDTO.class);

			return response.getBody();
		} catch (Exception e) {
			System.err.println("Error getting user progress: " + e.getMessage());
			return null;
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

			// Make the request to the endpoint using the correct URL
			String url = BASE_URL + "/learned";

			// Trace the HTTP request for debugging
			System.out.println("\n==== GET LEARNED WORDS REQUEST ====");
			System.out.println("URL: " + url);
			System.out.println("Method: GET");
			System.out.println("Headers: " + headers);
			System.out.println("==================================\n");

			ResponseEntity<List<Word>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<Word>>() {
					});

			List<Word> learnedWords = response.getBody();

			System.out.println("\n==== GET LEARNED WORDS RESPONSE ====");
			System.out.println("Status Code: " + response.getStatusCode().value());
			System.out.println("Words Count: " + (learnedWords != null ? learnedWords.size() : "null"));
			if (learnedWords != null && !learnedWords.isEmpty()) {
				System.out.println("First word sample: " + learnedWords.get(0).getWord() + " (ID: "
						+ learnedWords.get(0).getId() + ")");
			}
			System.out.println("====================================\n");

			return learnedWords != null ? learnedWords : new ArrayList<>();
		} catch (Exception e) {
			System.err.println("\n==== GET LEARNED WORDS ERROR ====");
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
			System.err.println("================================\n");
			return new ArrayList<>();
		}
	}

	/**
	 * Gets learned words for the authenticated user filtered by category The
	 * filtering is done client-side since the server doesn't have a
	 * category-specific endpoint
	 * 
	 * @param categoryId The category ID to filter by
	 * @return List of learned words in the specified category
	 */
	public List<Word> getLearnedWordsByCategory(Long categoryId) {
		try {
			// Get all learned words first
			List<Word> allLearnedWords = getLearnedWords();
			System.out.println("Performing client-side filtering for category ID: " + categoryId);

			// Filter by category
			List<Word> filteredWords = allLearnedWords.stream()
					.filter(word -> word.getCategory() != null && word.getCategory().getId().equals(categoryId))
					.collect(Collectors.toList());

			System.out.println("Filtered from " + allLearnedWords.size() + " words to " + filteredWords.size()
					+ " words in category " + categoryId);

			return filteredWords;
		} catch (Exception e) {
			System.err.println("Error filtering learned words by category: " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public boolean addWordToLearned(Long wordId) {
		if (wordId == null) {
			System.err.println("Error: Word ID is null. Cannot mark as learned.");
			return false;
		}

		try {
			// Construct the URL using BASE_URL
			String url = BASE_URL + "/learned/" + wordId;

			// Create headers with authentication
			HttpHeaders headers = createAuthHeaders();

			// Create an HTTP entity with headers
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Make the POST request
			ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);

			// Return true if the response status is HTTP 200 OK
			return response.getStatusCode() == HttpStatus.OK;
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			System.err.println("HTTP Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
		} catch (Exception e) {
			System.err.println("Unexpected Error: " + e.getMessage());
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Updates a user's password using a dedicated DTO
	 * 
	 * @param newPassword The new password
	 * @return true if successful, false otherwise
	 */
	public boolean updateUserPassword(String newPassword) {
		try {
			// Create the password update DTO
			PasswordUpdateDTO passwordDTO = new PasswordUpdateDTO(newPassword);

			// Set up headers with authentication and content type
			HttpHeaders headers = createAuthHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			// Create HTTP entity with the password DTO and headers
			HttpEntity<PasswordUpdateDTO> entity = new HttpEntity<>(passwordDTO, headers);

			// URL for password update (no user ID in path - uses token only)
			String url = BASE_URL + "/password";

			// Log the request for debugging (don't log the actual password)
			System.out.println("Updating password at URL: " + url);

			// Make the PUT request to update the password
			ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);

			// Log the response for debugging
			System.out.println("Password update response: " + response.getStatusCode());

			// Return true if successful (HTTP 200 OK)
			return response.getStatusCode().is2xxSuccessful();
		} catch (Exception e) {
			System.err.println("Error updating password: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Gets the currently authenticated user's full details
	 * 
	 * @return User object with complete details or null if not authenticated
	 */
	public User getCurrentUser() {
		try {
			// Create headers with authentication
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Make a request to fetch current user details
			String url = BASE_URL + "/me";

			System.out.println("Fetching current user details from: " + url);
			ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.GET, entity, User.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				User user = response.getBody();
				if (user != null) {
					System.out
							.println("Retrieved current user: ID=" + user.getId() + ", Username=" + user.getUsername());
				} else {
					System.out.println("Received empty user object from server");
				}
				return user;
			} else {
				System.err.println("Failed to get current user. Status: " + response.getStatusCode());
				return null;
			}
		} catch (Exception e) {
			System.err.println("Error fetching current user: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Creates HTTP headers with authentication token for API requests
	 * 
	 * @return HttpHeaders with Authorization header if token is available
	 */
	@Override
	protected HttpHeaders createAuthHeaders() {
		HttpHeaders headers = new HttpHeaders();

		// Log token status for debugging
		TokenStorage.logTokenStatus("UserService createAuthHeaders");

		// Add the authentication token if available
		if (TokenStorage.hasToken()) {
			String tokenValue = TokenStorage.getToken();
			String authHeader = "Bearer " + tokenValue;
			headers.set("Authorization", authHeader);
			System.out.println("Added Authorization header: Bearer "
					+ tokenValue.substring(0, Math.min(10, tokenValue.length())) + "...");

			// Set content type to application/json for POST requests
			headers.setContentType(MediaType.APPLICATION_JSON);
		} else {
			System.err.println("WARNING: No authentication token available when creating auth headers!");
		}

		return headers;
	}

}