package com.lingotower.service;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.lingotower.dto.sentence.ExampleSentenceResponseDTO;
import com.lingotower.security.TokenStorage;

public class ExampleSentencesService extends BaseService {

	private static final String SENTENCES_API_PATH = "/api/sentences/word/";

	// Constructor expecting dependencies from BaseService
	public ExampleSentencesService(RestTemplate restTemplate, TokenStorage tokenStorage) {
		super(); // Call BaseService constructor
	}

	/**
	 * Fetches example sentences for a given word from the API.
	 *
	 * @param word The word to fetch example sentences for.
	 * @return A List of example sentences or a default/error message.
	 */
	public List<String> getExampleSentences(String word) {
		String url = UriComponentsBuilder.fromUriString(BASE_URL + SENTENCES_API_PATH).pathSegment(word).toUriString();

		System.out.println("Requesting URL: " + url); // Debug log

		try {
			ResponseEntity<ExampleSentenceResponseDTO> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
					createAuthHttpEntity(null), // Assumes createAuthHttpEntity is in BaseService
					ExampleSentenceResponseDTO.class);

			ExampleSentenceResponseDTO responseBody = responseEntity.getBody();

			if (responseBody != null && responseBody.getSentences() != null && !responseBody.getSentences().isEmpty()) {
				System.out.println(
						"Successfully fetched " + responseBody.getSentences().size() + " sentences for word: " + word);
				return responseBody.getSentences();
			} else {
				System.out.println("API returned OK but no sentences found for word: " + word);
				return Collections.singletonList("No example sentences available for the word \"" + word + "\".");
			}

		} catch (HttpClientErrorException e) {
			System.err.println("HTTP Error fetching sentences for '" + word + "': " + e.getStatusCode() + " - "
					+ e.getResponseBodyAsString());
			if (e.getStatusCode().value() == 404) {
				return Collections.singletonList("No example sentences found (404) for the word \"" + word + "\".");
			} else if (e.getStatusCode().value() == 401) {
				return Collections.singletonList("Authentication error (401). Please log in again.");
			} else {
				return Collections.singletonList(
						"HTTP Error (" + e.getStatusCode() + ") fetching sentences for \"" + word + "\".");
			}
		} catch (RestClientException e) {
			System.err.println("Error fetching example sentences for word '" + word + "': " + e.getMessage());
			e.printStackTrace();
			return Collections
					.singletonList("Error connecting to the server while fetching sentences for \"" + word + "\".");
		} catch (Exception e) {
			System.err.println("Unexpected error fetching sentences for '" + word + "': " + e.getMessage());
			e.printStackTrace();
			return Collections
					.singletonList("An unexpected error occurred while fetching sentences for \"" + word + "\".");
		}
	}
}