package com.lingotower.service;

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

/**
 * שירות אימות מעודכן שעובד עם JWT
 */
public class AuthService {
	private static final String BASE_URL = "http://localhost:8080/api/auth";
	private RestTemplate restTemplate;

	public AuthService() {
		this.restTemplate = new RestTemplate();
	}

	/**
	 * כניסה למערכת וקבלת טוקן JWT
	 */
	public User login(String username, String password) {
		try {
			// יצירת בקשת התחברות
			LoginRequest loginRequest = new LoginRequest();
			loginRequest.setUsername(username);
			loginRequest.setPassword(password);

			System.out.println("שולח בקשת התחברות ל-" + BASE_URL + "/login");
			System.out.println("שם משתמש: " + username);
			System.out.println("סיסמה: " + password);

			// שליחת בקשת ההתחברות לשרת
			ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL + "/login", loginRequest,
					String.class);

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				String token = response.getBody();
				TokenStorage.setToken(token);

				// יצירת אובייקט משתמש והחזרתו
				User user = new User();
				user.setUsername(username);

				return user;
			}

			return null;
		} catch (Exception e) {
			System.err.println("שגיאה בהתחברות: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * הרשמה למערכת וקבלת טוקן JWT
	 */
	public User register(String username, String password, String email, String language) {
		try {
			System.out.println("מנסה לרשום משתמש חדש: " + username);

			// יצירת בקשת ההרשמה
			RegisterRequest registerRequest = new RegisterRequest();
			registerRequest.setUsername(username);
			registerRequest.setPassword(password);
			registerRequest.setEmail(email);
			registerRequest.setLanguage(language);

			// הגדרת כותרות
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<RegisterRequest> entity = new HttpEntity<>(registerRequest, headers);

			// שליחת בקשת ההרשמה לשרת
			ResponseEntity<String> response = restTemplate.exchange(BASE_URL + "/register", HttpMethod.POST, entity,
					String.class);

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				String token = response.getBody();
				TokenStorage.setToken(token);

				// יצירת אובייקט משתמש והחזרתו
				User user = new User();
				user.setUsername(username);
				user.setEmail(email);
				user.setLanguage(language);

				return user;
			}

			return null;
		} catch (Exception e) {
			System.err.println("שגיאה בהרשמה: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * יציאה מהמערכת - מחיקת הטוקן
	 */
	public void logout() {
		TokenStorage.clearToken();
	}
}