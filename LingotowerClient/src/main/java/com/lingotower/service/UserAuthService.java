package com.lingotower.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import com.lingotower.dto.LoginRequest;
import com.lingotower.dto.RegisterRequest;
import com.lingotower.model.User;
import com.lingotower.security.TokenStorage;
import com.lingotower.utils.LoggingUtility;

/**
 * Service for user authentication and registration.
 */
public class UserAuthService extends BaseService {

	private static final String USER_AUTH_PATH = "/api/auth/user";

	/**
	 * Constructor for UserAuthService.
	 */
	public UserAuthService() {
		super(); // Initialize the base service
		logger.debug("UserAuthService initialized");
	}

	/**
	 * Login to the system and receive a JWT token.
	 * 
	 * @param username Username or email
	 * @param password User password
	 * @return User object if login successful, null otherwise
	 */
	public User login(String username, String password) {
		try {
			long startTime = System.currentTimeMillis();
			logger.info("Login attempt for user: {}", username);

			// Create login request
			LoginRequest loginRequest = new LoginRequest();
			loginRequest.setIdentifier(username);
			loginRequest.setPassword(password);

			// Prepare URL
			String url = buildUrl(USER_AUTH_PATH, "login");
			logger.debug("Sending login request to: {}", url);

			// Set headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			// Create HTTP entity with request body and headers
			HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);

			// Send login request to the server
			ResponseEntity<String> response = restTemplate.postForEntity(url, loginRequest, String.class);

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				String token = response.getBody();
				TokenStorage.setToken(token);

				logger.debug("JWT token received and stored, length: {}", token.length());

				// Create user object
				User user = new User();
				user.setUsername(username);

				long duration = System.currentTimeMillis() - startTime;
				LoggingUtility.logPerformance(logger, "login", duration, "success");
				logger.info("Login successful for user: {}", username);

				return user;
			} else {
				logger.warn("Login failed for user: {}. Status code: {}", username, response.getStatusCode());
				return null;
			}
		} catch (HttpClientErrorException e) {
			logger.error("HTTP Error during login for user {}: {} - {}", username, e.getStatusCode(),
					e.getResponseBodyAsString());
			return null;
		} catch (RestClientException e) {
			logger.error("REST client error during login for user {}: {}", username, e.getMessage());
			return null;
		} catch (Exception e) {
			logger.error("Unexpected error during login for user {}: {}", username, e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Register a new user and receive a JWT token.
	 * 
	 * @param username Username
	 * @param password Password
	 * @param email    Email address
	 * @param language Language preference code (e.g., "en", "he")
	 * @return User object if registration successful, null otherwise
	 */
	public User register(String username, String password, String email, String language) {
		try {
			long startTime = System.currentTimeMillis();
			logger.info("Registration attempt for username: {}, email: {}", username, email);

			// Create registration request
			RegisterRequest registerRequest = new RegisterRequest();
			registerRequest.setUsername(username);
			registerRequest.setPassword(password);
			registerRequest.setEmail(email);
			registerRequest.setSourceLanguage(language);

			// Set headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			// Create HTTP entity with request body and headers
			HttpEntity<RegisterRequest> entity = new HttpEntity<>(registerRequest, headers);

			// Prepare URL
			String url = buildUrl(USER_AUTH_PATH, "register");
			logger.debug("Sending registration request to: {}", url);

			// Send registration request to the server
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				String token = response.getBody();
				TokenStorage.setToken(token);

				logger.debug("JWT token received and stored, length: {}", token.length());

				// Create user object
				User user = new User();
				user.setUsername(username);
				user.setEmail(email);
				user.setLanguage(language);

				long duration = System.currentTimeMillis() - startTime;
				LoggingUtility.logPerformance(logger, "register", duration, "success");
				logger.info("Registration successful for user: {}", username);

				return user;
			} else {
				logger.warn("Registration failed for user: {}. Status code: {}", username, response.getStatusCode());
				return null;
			}
		} catch (HttpClientErrorException e) {
			logger.error("HTTP Error during registration for user {}: {} - {}", username, e.getStatusCode(),
					e.getResponseBodyAsString());
			return null;
		} catch (RestClientException e) {
			logger.error("REST client error during registration for user {}: {}", username, e.getMessage());
			return null;
		} catch (Exception e) {
			logger.error("Unexpected error during registration for user {}: {}", username, e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Logout from the system - clear the token.
	 */
	public void logout() {
		logger.info("User logout - clearing token");
		TokenStorage.clearToken();
		LoggingUtility.logAction(logger, "logout", "user", "system", "success");
	}
}