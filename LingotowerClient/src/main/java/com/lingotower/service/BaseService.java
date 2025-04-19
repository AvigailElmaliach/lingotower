package com.lingotower.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
	public BaseService() {
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

	/**
	 * Creates an HttpEntity with the Authorization header. This is the same as
	 * createAuthEntity but with a more standard method name for compatibility with
	 * existing code that might use this name.
	 *
	 * @param body The object to be sent as the request body (can be null for
	 *             GET/DELETE)
	 * @param <T>  The type of the body
	 * @return An HttpEntity containing the headers and the body
	 */
	protected <T> HttpEntity<T> createAuthHttpEntity(T body) {
		return createAuthEntity(body);
	}

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
			int statusCode = response.getStatusCode().value();

			// Handle authentication failures
			if (statusCode == 401) {
				logger.warn("Authentication failure - token expired or invalid");
				TokenStorage.clearToken();
			} else if (statusCode == 403) {
				logger.warn("Authorization failure - insufficient permissions");
			} else if (statusCode == 404) {
				logger.warn("Resource not found");
			} else {
				logger.error("API error: Status code {}", statusCode);
			}

			// Let the default handler process the error
			super.handleError(response);
		}
	}
}