package com.lingotower.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import com.lingotower.security.TokenStorage;

/**
 * Custom error handler for REST template responses, with special handling for
 * authentication errors.
 */
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

	private static final Logger logger = LoggerFactory.getLogger(RestTemplateResponseErrorHandler.class);
	private final DefaultResponseErrorHandler defaultHandler = new DefaultResponseErrorHandler();

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return defaultHandler.hasError(response);
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		HttpStatusCode statusCode = response.getStatusCode();
		int status = statusCode.value();

		// Handle authentication failures
		if (status == HttpStatus.UNAUTHORIZED.value()) {
			// Token is invalid or expired
			logger.warn("Authentication failure (HTTP 401) - JWT token invalid or expired");
			TokenStorage.clearToken();

			// In a real application, you might trigger UI events to show a login screen
			logger.info("User should re-authenticate. Token has been cleared.");
		} else if (status == HttpStatus.FORBIDDEN.value()) {
			// User doesn't have permission for the requested resource
			logger.warn("Authorization failure (HTTP 403) - Insufficient permissions");
		} else if (status >= 400 && status < 500) {
			// Handle other client errors
			logger.error("Client error: HTTP {}", status);

			// Attempt to extract error message from response body
			try {
				String errorBody = new String(response.getBody().readAllBytes());
				logger.error("Error response body: {}", errorBody);
			} catch (Exception e) {
				logger.warn("Could not read error response body", e);
			}
		} else if (status >= 500) {
			// Handle server errors
			logger.error("Server error: HTTP {}", status);

			try {
				String errorBody = new String(response.getBody().readAllBytes());
				logger.error("Error response body: {}", errorBody);
			} catch (Exception e) {
				logger.warn("Could not read error response body", e);
			}
		}

		// Let the default handler process the error
		// This will throw appropriate exceptions that can be caught by service methods
		defaultHandler.handleError(response);
	}
}