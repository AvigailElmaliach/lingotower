package com.lingotower.service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import com.lingotower.dto.sentence.ExampleSentenceResponseDTO;

/**
 * Service for retrieving example sentences for words.
 */
public class ExampleSentencesService extends BaseService {

	private static final String SENTENCES_API_PATH = "/api/sentences/word";

	/**
	 * Default constructor that initializes the base service.
	 */
	public ExampleSentencesService() {
		super();
		logger.debug("ExampleSentencesService initialized");
	}

	/**
	 * Fetches example sentences for a given word from the API.
	 *
	 * @param word The word to fetch example sentences for.
	 * @return A List of example sentences or a default/error message.
	 */
	public List<String> getExampleSentences(String word) {
		if (word == null || word.trim().isEmpty()) {
			logger.warn("Cannot fetch example sentences: word is null or empty");
			return Collections.singletonList("No word provided to fetch examples for.");
		}

		String trimmedWord = word.trim();
		logger.info("Fetching example sentences for word: '{}'", trimmedWord);

		try {
			// Build properly encoded URI with the word as path variable
			URI uri = UriComponentsBuilder.fromUriString(BASE_URL).path(SENTENCES_API_PATH + "/{word}")
					.buildAndExpand(trimmedWord).encode(StandardCharsets.UTF_8).toUri();

			logger.debug("Request URI: {}", uri);

			// Execute request with authentication
			HttpEntity<?> entity = createAuthEntity(null);
			ResponseEntity<ExampleSentenceResponseDTO> response = restTemplate.exchange(uri, HttpMethod.GET, entity,
					ExampleSentenceResponseDTO.class);

			// Process response
			ExampleSentenceResponseDTO responseBody = response.getBody();
			if (responseBody != null && responseBody.getSentences() != null && !responseBody.getSentences().isEmpty()) {
				logger.info("Successfully fetched {} sentences for word: '{}'", responseBody.getSentences().size(),
						trimmedWord);
				return responseBody.getSentences();
			} else {
				logger.warn("API returned OK but no sentences found for word: '{}'", trimmedWord);
				return Collections
						.singletonList("No example sentences available for the word \"" + trimmedWord + "\".");
			}

		} catch (HttpClientErrorException e) {
			logger.error("HTTP Error fetching sentences for '{}': {} - {}", trimmedWord, e.getStatusCode(),
					e.getResponseBodyAsString());

			if (e.getStatusCode().value() == 404) {
				return Collections.singletonList("No example sentences found for the word \"" + trimmedWord + "\".");
			} else if (e.getStatusCode().value() == 401) {
				return Collections.singletonList("Authentication error. Please log in again.");
			} else {
				return Collections.singletonList(
						"HTTP Error (" + e.getStatusCode() + ") fetching sentences for \"" + trimmedWord + "\".");
			}
		} catch (RestClientException e) {
			logger.error("Error connecting to server for word '{}': {}", trimmedWord, e.getMessage());
			return Collections.singletonList(
					"Error connecting to the server while fetching sentences for \"" + trimmedWord + "\".");
		} catch (Exception e) {
			logger.error("Unexpected error fetching sentences for '{}': {}", trimmedWord, e.getMessage(), e);
			return Collections.singletonList(
					"An unexpected error occurred while fetching sentences for \"" + trimmedWord + "\".");
		}
	}
}