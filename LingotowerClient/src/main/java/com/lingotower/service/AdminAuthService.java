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
import com.lingotower.model.Role;
import com.lingotower.security.TokenStorage;

/**
 * Service for handling admin authentication
 */
public class AdminAuthService {

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
			System.out.println("Admin login attempt for: " + username);

			// Create login request
			LoginRequest loginRequest = new LoginRequest(username, password);

			// Set headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			// Create HTTP entity with request body and headers
			HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);

			// Send login request to the admin login endpoint
			ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/api/auth/admin/login",
					HttpMethod.POST, entity, String.class);

			// Check if login was successful
			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				// Store the JWT token
				String token = response.getBody();
				System.out.println("Admin login successful! Token received (length: " + token.length() + ")");

				// CRITICAL: Store the token
				TokenStorage.setToken(token);
				TokenStorage.logTokenStatus("After admin login");

				// Create admin object
				Admin admin = new Admin();
				admin.setUsername(username);
				admin.setRole(Role.ADMIN.toString());

				return admin;
			} else {
				System.out.println("Admin login failed: " + response.getStatusCode());
				return null;
			}
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