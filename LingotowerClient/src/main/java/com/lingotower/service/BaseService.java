package com.lingotower.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.lingotower.security.TokenStorage;

/**
 * מחלקת בסיס לכל השירותים בצד הלקוח
 */
public abstract class BaseService {
	protected static final Logger logger = LoggerFactory.getLogger(BaseService.class);
	protected RestTemplate restTemplate;

	public BaseService() {
		logger.debug("Initializing BaseService");
		this.restTemplate = new RestTemplate();
		this.restTemplate.setErrorHandler(new AuthenticationErrorHandler());
		logger.debug("RestTemplate configured with AuthenticationErrorHandler");
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

			logger.debug("Added Authorization header: Bearer {}...",
					tokenValue.substring(0, Math.min(10, tokenValue.length())));
		} else {
			logger.warn("No token available when creating auth headers!");
		}
		return headers;
	}

	/**
	 * מחלקה פנימית לטיפול בשגיאות אימות
	 */
	private class AuthenticationErrorHandler extends DefaultResponseErrorHandler {
		private final Logger handlerLogger = LoggerFactory.getLogger(AuthenticationErrorHandler.class);

		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
			// אם קיבלנו תגובת 401 (Unauthorized), כנראה הטוקן פג תוקף
			if (response.getStatusCode().value() == 401) {
				handlerLogger.warn("Authentication failure - JWT token invalid or expired. Need to re-authenticate.");
				TokenStorage.clearToken();
				// כאן יש להוסיף קריאה למסך התחברות
			} else {
				// טיפול בשגיאות אחרות
				handlerLogger.error("API error: Status code {}", response.getStatusCode().value());
				super.handleError(response);
			}
		}
	}
}