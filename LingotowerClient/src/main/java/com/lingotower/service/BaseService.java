package com.lingotower.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	/**
	 * Constructor that initializes the RestTemplate with an error handler.
	 */
	public BaseService() {
		this.restTemplate = new RestTemplate();
		this.restTemplate.setErrorHandler(new ApiResponseErrorHandler());
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
}