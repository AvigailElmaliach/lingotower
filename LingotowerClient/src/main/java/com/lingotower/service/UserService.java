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

import com.lingotower.dto.user.PasswordUpdateDTO;
import com.lingotower.dto.user.UserProgressDTO;
import com.lingotower.dto.user.UserUpdateDTO;
import com.lingotower.model.User;
import com.lingotower.model.Word;

/**
 * Service for managing users and user-related functionality.
 */
public class UserService extends BaseService {

	/**
	 * Constructor for UserService.
	 */
	public UserService() {
		super(); // Initialize the base service
		logger.debug("UserService initialized");
	}

	/**
	 * Fetches all users from the server.
	 * 
	 * @return A list of all users or an empty list if none are found or an error
	 *         occurs
	 */
	public List<User> getAllUsers() {
		try {
			logger.info("Fetching all users");

			String url = buildUrl(USERS_PATH);
			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<List<User>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<User>>() {
					});

			if (response.getStatusCode().is2xxSuccessful()) {
				List<User> users = response.getBody();
				logger.info("Successfully retrieved {} users", users != null ? users.size() : 0);
				return users != null ? users : new ArrayList<>();
			} else {
				logger.error("Failed to retrieve users. Status code: {}", response.getStatusCode());
				return new ArrayList<>();
			}
		} catch (Exception e) {
			logger.error("Error fetching users: {}", e.getMessage(), e);
			return new ArrayList<>();
		}
	}

	/**
	 * Fetches a user by their ID.
	 * 
	 * @param id The ID of the user to fetch
	 * @return The user if found, null otherwise
	 */
	public User getUserById(Long id) {
		try {
			logger.info("Fetching user with ID: {}", id);

			String url = buildUrl(USERS_PATH, id.toString());
			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.GET, entity, User.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				User user = response.getBody();
				logger.info("Successfully retrieved user with ID: {}", id);
				return user;
			} else {
				logger.error("Failed to retrieve user with ID: {}. Status code: {}", id, response.getStatusCode());
				return null;
			}
		} catch (Exception e) {
			logger.error("Error fetching user with ID {}: {}", id, e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Creates a new user.
	 * 
	 * @param user The user to create
	 * @return The created user with its assigned ID, or null if creation failed
	 */
	public User createUser(User user) {
		try {
			logger.info("Creating new user: {}", user.getUsername());

			String url = buildUrl(USERS_PATH);
			HttpEntity<User> entity = createAuthEntity(user);

			ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.POST, entity, User.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				User createdUser = response.getBody();
				logger.info("Successfully created user with ID: {}",
						createdUser != null ? createdUser.getId() : "unknown");
				return createdUser;
			} else {
				logger.error("Failed to create user. Status code: {}", response.getStatusCode());
				return null;
			}
		} catch (Exception e) {
			logger.error("Error creating user: {}", e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Updates an existing user.
	 * 
	 * @param user The updated user information
	 * @return true if update was successful, false otherwise
	 */
	public boolean updateUser(User user) {
		try {
			// Validate user object
			if (user == null) {
				logger.error("Cannot update user: user object is null");
				return false;
			}

			if (user.getId() == null) {
				logger.error("Cannot update user: user ID is null");
				return false;
			}

			// Validate required fields
			if (user.getUsername() == null || user.getUsername().trim().isEmpty() || user.getEmail() == null
					|| user.getEmail().trim().isEmpty() || user.getLanguage() == null
					|| user.getLanguage().trim().isEmpty()) {
				logger.error("Cannot update user: required fields missing");
				return false;
			}

			logger.info("Updating user with ID: {}", user.getId());

			// Create a DTO with the required fields
			UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
			userUpdateDTO.setUsername(user.getUsername());
			userUpdateDTO.setEmail(user.getEmail());
			userUpdateDTO.setSourceLanguage(user.getLanguage()); // Map to sourceLanguage

			// Set up headers with authentication and content type
			HttpHeaders headers = createAuthHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			// Create HTTP entity with the DTO and headers
			HttpEntity<UserUpdateDTO> entity = new HttpEntity<>(userUpdateDTO, headers);

			// Construct the URL with the user ID
			String url = buildUrl(USERS_PATH, user.getId().toString());

			// Log the request for debugging
			logger.debug("Updating user profile - URL: {}, User ID: {}, Username: {}, Email: {}, SourceLanguage: {}",
					url, user.getId(), userUpdateDTO.getUsername(), userUpdateDTO.getEmail(),
					userUpdateDTO.getSourceLanguage());

			// Make the PUT request to update the user
			ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);

			// Log the response for debugging
			boolean success = response.getStatusCode().is2xxSuccessful();
			logger.debug("User update response: {}", response.getStatusCode());

			if (success) {
				logger.info("Successfully updated user with ID: {}", user.getId());
			} else {
				logger.error("Failed to update user with ID: {}. Status code: {}", user.getId(),
						response.getStatusCode());
			}

			return success;
		} catch (Exception e) {
			logger.error("Error updating user: {}", e.getMessage(), e);
			return false;
		}
	}

	/**
	 * Deletes a user by their ID.
	 * 
	 * @param id The ID of the user to delete
	 * @return true if deletion was successful, false otherwise
	 */
	public boolean deleteUser(Long id) {
		try {
			if (id == null) {
				logger.error("Cannot delete user with null ID");
				return false;
			}

			logger.info("Deleting user with ID: {}", id);

			String url = buildUrl(USERS_PATH, id.toString());
			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);

			boolean success = response.getStatusCode() == HttpStatus.NO_CONTENT;
			if (success) {
				logger.info("Successfully deleted user with ID: {}", id);
			} else {
				logger.error("Failed to delete user with ID: {}. Status code: {}", id, response.getStatusCode());
			}
			return success;
		} catch (Exception e) {
			logger.error("Error deleting user with ID {}: {}", id, e.getMessage(), e);
			return false;
		}
	}

	/**
	 * Gets the currently authenticated user's progress.
	 * 
	 * @return The user's progress or null if an error occurs
	 */
	public UserProgressDTO getUserProgress() {
		try {
			logger.info("Fetching user progress");

			String url = buildUrl(USERS_PATH, "progress");
			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<UserProgressDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					UserProgressDTO.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				UserProgressDTO progress = response.getBody();
				logger.info("Successfully retrieved user progress: {}%",
						progress != null ? progress.getProgressPercentage() : "unknown");
				return progress;
			} else {
				logger.error("Failed to retrieve user progress. Status code: {}", response.getStatusCode());
				return null;
			}
		} catch (Exception e) {
			logger.error("Error fetching user progress: {}", e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Gets learned words for the authenticated user.
	 * 
	 * @return List of learned words or an empty list if none found or an error
	 *         occurs
	 */
	public List<Word> getLearnedWords() {
		try {
			logger.info("Fetching learned words for authenticated user");

			String url = buildUrl(USERS_PATH, "learned");
			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<List<Word>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<Word>>() {
					});

			if (response.getStatusCode().is2xxSuccessful()) {
				List<Word> learnedWords = response.getBody();
				logger.info("Successfully retrieved {} learned words", learnedWords != null ? learnedWords.size() : 0);

				if (learnedWords != null && !learnedWords.isEmpty()) {
					logger.debug("First word sample: {} (ID: {})", learnedWords.get(0).getWord(),
							learnedWords.get(0).getId());
				}

				return learnedWords != null ? learnedWords : new ArrayList<>();
			} else {
				logger.error("Failed to retrieve learned words. Status code: {}", response.getStatusCode());
				return new ArrayList<>();
			}
		} catch (Exception e) {
			logger.error("Error fetching learned words: {}", e.getMessage(), e);
			return new ArrayList<>();
		}
	}

	/**
	 * Gets learned words for the authenticated user filtered by category. The
	 * filtering is done client-side since the server doesn't have a
	 * category-specific endpoint.
	 * 
	 * @param categoryId The category ID to filter by
	 * @return List of learned words in the specified category
	 */
	public List<Word> getLearnedWordsByCategory(Long categoryId) {
		try {
			logger.info("Fetching learned words filtered by category ID: {}", categoryId);

			// Get all learned words first
			List<Word> allLearnedWords = getLearnedWords();

			// Filter by category
			List<Word> filteredWords = allLearnedWords.stream()
					.filter(word -> word.getCategory() != null && word.getCategory().getId().equals(categoryId))
					.collect(Collectors.toList());

			logger.info("Filtered from {} words to {} words in category {}", allLearnedWords.size(),
					filteredWords.size(), categoryId);

			return filteredWords;
		} catch (Exception e) {
			logger.error("Error filtering learned words by category: {}", e.getMessage(), e);
			return new ArrayList<>();
		}
	}

	/**
	 * Adds a word to the authenticated user's learned words list.
	 * 
	 * @param wordId The ID of the word to add
	 * @return true if successful, false otherwise
	 */
	public boolean addWordToLearned(Long wordId) {
		try {
			if (wordId == null) {
				logger.error("Cannot mark word as learned: Word ID is null");
				return false;
			}

			logger.info("Adding word with ID {} to learned words", wordId);

			String url = buildUrl(USERS_PATH, "learned", wordId.toString());
			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);

			boolean success = response.getStatusCode() == HttpStatus.OK;
			if (success) {
				logger.info("Successfully added word with ID {} to learned words", wordId);
			} else {
				logger.error("Failed to add word with ID {} to learned words. Status code: {}", wordId,
						response.getStatusCode());
			}
			return success;
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			logger.error("HTTP Error adding word to learned: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
			return false;
		} catch (Exception e) {
			logger.error("Unexpected error adding word to learned: {}", e.getMessage(), e);
			return false;
		}
	}

	/**
	 * Updates a user's password.
	 * 
	 * @param newPassword The new password
	 * @return true if successful, false otherwise
	 */
	public boolean updateUserPassword(String newPassword) {
		try {
			if (newPassword == null || newPassword.isEmpty()) {
				logger.error("Cannot update password: New password is null or empty");
				return false;
			}

			logger.info("Updating user password");

			// Create the password update DTO
			PasswordUpdateDTO passwordDTO = new PasswordUpdateDTO(newPassword);

			String url = buildUrl(USERS_PATH, "password");
			HttpEntity<PasswordUpdateDTO> entity = createAuthEntity(passwordDTO);

			ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);

			boolean success = response.getStatusCode().is2xxSuccessful();
			if (success) {
				logger.info("Successfully updated user password");
			} else {
				logger.error("Failed to update user password. Status code: {}", response.getStatusCode());
			}
			return success;
		} catch (Exception e) {
			logger.error("Error updating user password: {}", e.getMessage(), e);
			return false;
		}
	}

	/**
	 * Gets the currently authenticated user's full details.
	 * 
	 * @return User object with complete details or null if not authenticated
	 */
	public User getCurrentUser() {
		try {
			logger.info("Fetching current user details");

			String url = buildUrl(USERS_PATH, "me");
			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.GET, entity, User.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				User user = response.getBody();
				if (user != null) {
					logger.info("Successfully retrieved current user: ID={}, Username={}", user.getId(),
							user.getUsername());
				} else {
					logger.warn("Received empty user object from server");
				}
				return user;
			} else {
				logger.error("Failed to get current user. Status code: {}", response.getStatusCode());
				return null;
			}
		} catch (Exception e) {
			logger.error("Error fetching current user: {}", e.getMessage(), e);
			return null;
		}
	}
}