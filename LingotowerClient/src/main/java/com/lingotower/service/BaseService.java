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

	/**
	 * יצירת כותרות HTTP עם טוקן האימות
	 */
	protected HttpHeaders createAuthHeaders() {
		HttpHeaders headers = new HttpHeaders();
		if (TokenStorage.hasToken()) {
			// הוספת טוקן JWT לכותרת Authorization
			// בפורמט "Bearer [הטוקן]"
			headers.set("Authorization", "Bearer " + TokenStorage.getToken());
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