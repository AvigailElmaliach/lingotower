package com.lingotower.service;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.lingotower.security.TokenStorage;

/**
 * מחלקת בסיס לכל השירותים בצד הלקוח
 */
public abstract class BaseService {
	protected RestTemplate restTemplate;

	public BaseService() {
		this.restTemplate = new RestTemplate();
		this.restTemplate.setErrorHandler(new AuthenticationErrorHandler());
	}

	// Method to create headers with authentication token
	protected HttpHeaders createAuthHeaders() {
		HttpHeaders headers = new HttpHeaders();

		// Important debugging
		TokenStorage.logTokenStatus("Creating auth headers");

		// Add the authentication token if available
		if (TokenStorage.hasToken()) {
			String tokenValue = TokenStorage.getToken();
			String authHeader = "Bearer " + tokenValue;
			headers.set("Authorization", authHeader);
			System.out.println("Added Authorization header: Bearer "
					+ tokenValue.substring(0, Math.min(10, tokenValue.length())) + "...");
		} else {
			System.out.println("WARNING: No token available when creating auth headers!");
		}
		return headers;
	}

	/**
	 * מחלקה פנימית לטיפול בשגיאות אימות
	 */
	private class AuthenticationErrorHandler extends DefaultResponseErrorHandler {
		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
			// אם קיבלנו תגובת 401 (Unauthorized), כנראה הטוקן פג תוקף
			if (response.getStatusCode().value() == 401) {
				System.out.println("טוקן JWT לא תקף או פג תוקף. יש להתחבר מחדש.");
				TokenStorage.clearToken();
				// כאן יש להוסיף קריאה למסך התחברות
			} else {
				// טיפול בשגיאות אחרות
				super.handleError(response);
			}
		}
	}
}