package com.lingotower.service;

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
		super(); // Initialize the base service
		logger.debug("ExampleSentencesService initialized");
	}

	/**
	 * Fetches example sentences for a given word from the API.
	 *
	 * @param word The word to fetch example sentences for.
	 * @return A List of example sentences or a default/error message.
	 */
	public List<String> getExampleSentences(String word) {
		try {
			if (word == null || word.isEmpty()) {
				logger.warn("Cannot fetch example sentences: word is null or empty");
				return Collections.singletonList("No word provided to fetch examples for.");
			}

			logger.info("Fetching example sentences for word: {}", word);

			String url = UriComponentsBuilder.fromUriString(BASE_URL).path(SENTENCES_API_PATH).pathSegment(word)
					.toUriString();

			logger.debug("Request URL: {}", url);

			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<ExampleSentenceResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					ExampleSentenceResponseDTO.class);

			ExampleSentenceResponseDTO responseBody = response.getBody();

			if (responseBody != null && responseBody.getSentences() != null && !responseBody.getSentences().isEmpty()) {
				logger.info("Successfully fetched {} sentences for word: {}", responseBody.getSentences().size(), word);
				return responseBody.getSentences();
			} else {
				logger.warn("API returned OK but no sentences found for word: {}", word);
				return Collections.singletonList("No example sentences available for the word \"" + word + "\".");
			}

		} catch (HttpClientErrorException e) {
			logger.error("HTTP Error fetching sentences for '{}': {} - {}", word, e.getStatusCode(),
					e.getResponseBodyAsString());

			if (e.getStatusCode().value() == 404) {
				return Collections.singletonList("No example sentences found for the word \"" + word + "\".");
			} else if (e.getStatusCode().value() == 401) {
				return Collections.singletonList("Authentication error. Please log in again.");
			} else {
				return Collections.singletonList(
						"HTTP Error (" + e.getStatusCode() + ") fetching sentences for \"" + word + "\".");
			}
		} catch (RestClientException e) {
			logger.error("Error fetching example sentences for word '{}': {}", word, e.getMessage(), e);
			return Collections
					.singletonList("Error connecting to the server while fetching sentences for \"" + word + "\".");
		} catch (Exception e) {
			logger.error("Unexpected error fetching sentences for '{}': {}", word, e.getMessage(), e);
			return Collections
					.singletonList("An unexpected error occurred while fetching sentences for \"" + word + "\".");
		}
	}
}