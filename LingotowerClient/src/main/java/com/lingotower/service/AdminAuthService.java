package com.lingotower.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.lingotower.dto.LoginRequest;
import com.lingotower.model.Admin;
import com.lingotower.model.Role;
import com.lingotower.security.TokenStorage;
import com.lingotower.utils.LoggingUtility;

/**
 * Service for handling admin authentication
 */
public class AdminAuthService {

	private static final Logger logger = LoggerFactory.getLogger(AdminAuthService.class);
	private RestTemplate restTemplate;

	public AdminAuthService() {
		this.restTemplate = new RestTemplate();
		logger.debug("AdminAuthService initialized");
	}

	/**
	 * Login as admin
	 * 
	 * @param username Admin username
	 * @param password Admin password
	 * @return Admin object if login successful, null otherwise
	 */
	public Admin login(String username, String password) {
		try {
			long startTime = System.currentTimeMillis();
			logger.info("Admin login attempt for: {}", username);

			// Create login request
			LoginRequest loginRequest = new LoginRequest(username, password);

			// Set headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			// Create HTTP entity with request body and headers
			HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);

			logger.debug("Sending admin login request to: http://localhost:8080/api/auth/admin/login");

			// Send login request to the admin login endpoint
			ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/api/auth/admin/login",
					HttpMethod.POST, entity, String.class);

			// Check if login was successful
			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				// Store the JWT token
				String token = response.getBody();
				logger.debug("Admin login successful! Token received (length: {})", token.length());

				// CRITICAL: Store the token
				TokenStorage.setToken(token);
				TokenStorage.logTokenStatus("After admin login");

				// Create admin object
				Admin admin = new Admin();
				admin.setUsername(username);
				admin.setRole(Role.ADMIN.toString());

				long duration = System.currentTimeMillis() - startTime;
				LoggingUtility.logPerformance(logger, "admin_login", duration, "success");
				LoggingUtility.logAction(logger, "login", username, "admin", "success");

				return admin;
			} else {
				logger.warn("Admin login failed for: {}. Status code: {}", username, response.getStatusCode());
				LoggingUtility.logAction(logger, "login", username, "admin", "failed");
				return null;
			}
		} catch (Exception e) {
			logger.error("Error during admin login for {}: {}", username, e.getMessage(), e);
			LoggingUtility.logAction(logger, "login", username, "admin", "error: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Logout the current admin
	 */
	public void logout() {
		logger.info("Admin logout - clearing token");
		TokenStorage.clearToken();
		LoggingUtility.logAction(logger, "logout", "admin", "system", "success");
	}
}