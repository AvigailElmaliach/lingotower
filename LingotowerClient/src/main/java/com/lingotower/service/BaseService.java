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
	protected RestTemplate restTemplate;
	protected final TokenStorage tokenStorage;
	protected static final String BASE_URL = "http://localhost:8080"; // Base URL for the API

	/**
	 * Constructor that initializes the RestTemplate with an error handler.
	 */
	public BaseService() {
		this.restTemplate = new RestTemplate();
		this.restTemplate.setErrorHandler(new ApiResponseErrorHandler());
		this.tokenStorage = new TokenStorage();
		logger.debug("BaseService initialized");
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
			logger.debug("Added authorization token to request");
		} else {
			logger.warn("No token available for request");
		}

		return headers;
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
			} else {
				logger.error("API error: Status code {}", statusCode);
			}

			// Let the default handler process the error
			super.handleError(response);
		}
	}

	/**
	 * Creates an HttpEntity with the Authorization header set using the stored
	 * token. Sets Content-Type to application/json by default if a body is present.
	 *
	 * @param body The object to be sent as the request body (can be null for
	 *             GET/DELETE).
	 * @param <T>  The type of the body.
	 * @return An HttpEntity containing the headers and the body.
	 */
	protected <T> HttpEntity<T> createAuthHttpEntity(T body) {
		HttpHeaders headers = new HttpHeaders();
		String token = tokenStorage.getToken(); // Assumes tokenStorage is accessible here

		if (token != null && !token.isEmpty()) {
			headers.setBearerAuth(token);
		}

		// Set Content-Type if a body is provided. Adjust if needed (e.g., for file
		// uploads).
		if (body != null) {
			headers.setContentType(MediaType.APPLICATION_JSON);
		}
		// Optional: Set Accept header if needed
		// headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		return new HttpEntity<>(body, headers);
	}

}