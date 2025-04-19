package com.lingotower.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.lingotower.security.TokenStorage;

/**
 * Base class for all client-side services that handles REST API communication.
 */
public abstract class BaseService {

	protected static final Logger logger = LoggerFactory.getLogger(BaseService.class);
	protected final RestTemplate restTemplate;

	// Common base URL for all API endpoints
	protected static final String BASE_URL = "http://localhost:8080";

	// Common API endpoint paths
	protected static final String API_AUTH_PATH = "/api/auth";
	protected static final String CATEGORIES_PATH = "/categories";
	protected static final String WORDS_PATH = "/words";
	protected static final String QUIZZES_PATH = "/quizzes";
	protected static final String USERS_PATH = "/users";
	protected static final String ADMINS_PATH = "/admins";
	protected static final String TRANSLATE_PATH = "/api/translate";
	protected static final String COMPLETION_PATH = "/completion-practice";

	/**
	 * Constructor that initializes the RestTemplate with an error handler.
	 */
	protected BaseService() {
		this.restTemplate = new RestTemplate();
		this.restTemplate.setErrorHandler(new ApiResponseErrorHandler());
		logger.debug("{} initialized", this.getClass().getSimpleName());
	}

	/**
	 * Creates HTTP headers with authorization token if available.
	 *
	 * @return HttpHeaders with authorization if token exists
	 */
	protected HttpHeaders createAuthHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		if (TokenStorage.hasToken()) {
			String token = TokenStorage.getToken();
			headers.set("Authorization", "Bearer " + token);
			logger.trace("Added authorization token to request headers");
		} else {
			logger.warn("No token available for request in {}", this.getClass().getSimpleName());
		}

		return headers;
	}

	/**
	 * Creates an HttpEntity with the Authorization header.
	 *
	 * @param body The object to be sent as the request body (can be null for
	 *             GET/DELETE)
	 * @param <T>  The type of the body
	 * @return An HttpEntity containing the headers and the body
	 */
	protected <T> HttpEntity<T> createAuthEntity(T body) {
		HttpHeaders headers = createAuthHeaders();
		return new HttpEntity<>(body, headers);
	}

	// Removed the duplicated createAuthHttpEntity method

	/**
	 * Builds a complete URL with the base URL and the provided path.
	 *
	 * @param path The API endpoint path
	 * @return The complete URL
	 */
	protected String buildUrl(String path) {
		return BASE_URL + path;
	}

	/**
	 * Builds a complete URL with the base URL, path, and additional path segments.
	 *
	 * @param path         The API endpoint path
	 * @param pathSegments Additional path segments to append
	 * @return The complete URL
	 */
	protected String buildUrl(String path, String... pathSegments) {
		StringBuilder url = new StringBuilder(BASE_URL).append(path);
		for (String segment : pathSegments) {
			if (!segment.startsWith("/")) {
				url.append("/");
			}
			url.append(segment);
		}
		return url.toString();
	}

	/**
	 * Handles API response errors, with special handling for authentication
	 * failures.
	 */
	private class ApiResponseErrorHandler extends DefaultResponseErrorHandler {

		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
			if (response == null) {
				logger.error("Cannot handle error from a null ClientHttpResponse.");
				throw new IOException("Received null response, cannot determine error details.");
			}

			HttpStatusCode statusCode = response.getStatusCode();
			int statusCodeValue = statusCode.value();

			try {
				if (statusCodeValue == HttpStatus.UNAUTHORIZED.value()) { // 401
					logger.warn("Authentication failure - token expired or invalid. Status code: {}", statusCodeValue);
					TokenStorage.clearToken();
				} else if (statusCodeValue == HttpStatus.FORBIDDEN.value()) { // 403
					logger.warn("Authorization failure - insufficient permissions. Status code: {}", statusCodeValue);
				} else if (statusCodeValue == HttpStatus.NOT_FOUND.value()) { // 404
					logger.warn("Resource not found. Status code: {}", statusCodeValue);
				} else if (statusCode.isError()) {
					logger.error("API error: Status code {}", statusCodeValue);
				}

				super.handleError(response);

			} catch (IllegalArgumentException e) {
				// Log the exception with contextual information
				logger.error("Received an unknown HTTP status code from response. Status code: {}",
						response.getStatusCode().value(), e);

			}
		}

	}
}