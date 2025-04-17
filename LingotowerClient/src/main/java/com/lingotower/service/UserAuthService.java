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
import com.lingotower.dto.RegisterRequest;
import com.lingotower.model.User;
import com.lingotower.security.TokenStorage;
import com.lingotower.utils.LoggingUtility;

/**
 * שירות אימות מעודכן שעובד עם JWT
 */
public class UserAuthService {
	private static final String USER_BASE_URL = "http://localhost:8080/api/auth/user";
	private static final Logger logger = LoggerFactory.getLogger(UserAuthService.class);

	private RestTemplate restTemplate;

	public UserAuthService() {
		this.restTemplate = new RestTemplate();
		logger.debug("UserAuthService initialized");
	}

	/**
	 * כניסה למערכת וקבלת טוקן JWT
	 */
	public User login(String username, String password) {
		try {
			long startTime = System.currentTimeMillis();
			logger.info("Login attempt for user: {}", username);

			// יצירת בקשת התחברות
			LoginRequest loginRequest = new LoginRequest();
			loginRequest.setIdentifier(username);
			loginRequest.setPassword(password);

			logger.debug("Sending login request to: {}", USER_BASE_URL + "/login");

			// שליחת בקשת ההתחברות לשרת
			ResponseEntity<String> response = restTemplate.postForEntity(USER_BASE_URL + "/login", loginRequest,
					String.class);

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				String token = response.getBody();
				TokenStorage.setToken(token);

				logger.debug("JWT token received and stored, length: {}", token.length());

				// יצירת אובייקט משתמש והחזרתו
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
		} catch (Exception e) {
			logger.error("Error during login for user: {}: {}", username, e.getMessage(), e);
			return null;
		}
	}

	/**
	 * הרשמה למערכת וקבלת טוקן JWT
	 */
	public User register(String username, String password, String email, String language) {
		try {
			long startTime = System.currentTimeMillis();
			logger.info("Registration attempt for username: {}, email: {}", username, email);

			// יצירת בקשת ההרשמה
			RegisterRequest registerRequest = new RegisterRequest();
			registerRequest.setUsername(username);
			registerRequest.setPassword(password);
			registerRequest.setEmail(email);
			registerRequest.setSourceLanguage(language);

			// הגדרת כותרות
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<RegisterRequest> entity = new HttpEntity<>(registerRequest, headers);

			logger.debug("Sending registration request to: {}", USER_BASE_URL + "/register");

			// שליחת בקשת ההרשמה לשרת
			ResponseEntity<String> response = restTemplate.exchange(USER_BASE_URL + "/register", HttpMethod.POST,
					entity, String.class);

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				String token = response.getBody();
				TokenStorage.setToken(token);

				logger.debug("JWT token received and stored, length: {}", token.length());

				// יצירת אובייקט משתמש והחזרתו
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
		} catch (Exception e) {
			logger.error("Error during registration for user: {}: {}", username, e.getMessage(), e);
			return null;
		}
	}

	/**
	 * יציאה מהמערכת - מחיקת הטוקן
	 */
	public void logout() {
		logger.info("User logout - clearing token");
		TokenStorage.clearToken();
	}
}