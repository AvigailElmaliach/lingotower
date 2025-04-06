package com.lingotower.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.lingotower.dto.LoginRequest;
import com.lingotower.model.Admin;
import com.lingotower.security.TokenStorage;

/**
 * Service for handling admin authentication
 */
public class AdminAuthService {
	private static final String ADMIN_AUTH_URL = "http://localhost:8080/api/auth/admin";
	private static final String ADMIN_LOGIN_URL = "http://localhost:8080/api/auth/admin/login";

	private RestTemplate restTemplate;

	public AdminAuthService() {
		this.restTemplate = new RestTemplate();
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
			// Create login request
			LoginRequest loginRequest = new LoginRequest(username, password);

			// Set headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			// Create HTTP entity with request body and headers
			HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);

			// Send login request
			ResponseEntity<String> response = restTemplate.exchange(ADMIN_LOGIN_URL, HttpMethod.POST, entity,
					String.class);

			// Check if login was successful
			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				// Store the JWT token
				String token = response.getBody();
				TokenStorage.setToken(token);

				// Create admin object
				Admin admin = new Admin();
				admin.setUsername(username);

				return admin;
			}

			return null;
		} catch (Exception e) {
			System.err.println("Error during admin login: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Logout the current admin
	 */
	public void logout() {
		TokenStorage.clearToken();
	}
}