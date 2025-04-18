package com.lingotower.dto.sentence;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data Transfer Object (DTO) representing the response from the example
 * sentences API endpoint. It maps the JSON structure containing the word and
 * its example sentences.
 */
@JsonIgnoreProperties(ignoreUnknown = true) // ignore any JSON properties not defined in this class
public class ExampleSentenceResponseDTO {

	private String word; // The word for which sentences were requested
	private List<String> sentences; // The list of example sentences

	// Getters and Setters

	/**
	 * Gets the word associated with these sentences.
	 * 
	 * @return The word string.
	 */
	public String getWord() {
		return word;
	}

	/**
	 * Sets the word.
	 * 
	 * @param word The word string.
	 */
	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * Gets the list of example sentences.
	 * 
	 * @return A List of strings, where each string is an example sentence.
	 */
	public List<String> getSentences() {
		return sentences;
	}

	/**
	 * Sets the list of example sentences.
	 * 
	 * @param sentences The list of example sentence strings.
	 */
	public void setSentences(List<String> sentences) {
		this.sentences = sentences;
	}
}