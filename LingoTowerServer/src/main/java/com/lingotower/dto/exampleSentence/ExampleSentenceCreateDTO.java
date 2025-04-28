package com.lingotower.dto.exampleSentence;

import java.util.List;

public class ExampleSentenceCreateDTO {

	private String word;
	private List<String> sentences;
	private List<String> translations;

	public ExampleSentenceCreateDTO(String word, List<String> sentences, List<String> translations) {
		this.word = word;
		this.sentences = sentences;
		this.translations = translations;
	}

	public ExampleSentenceCreateDTO() {
		// for Jackson
	}

	public ExampleSentenceCreateDTO(String word, List<String> sentences) {
		this.word = word;
		this.sentences = sentences;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public List<String> getSentences() {
		return sentences;
	}

	public void setSentences(List<String> sentences) {
		this.sentences = sentences;
	}

	public List<String> getTranslations() {
		return translations;
	}

	public void setTranslations(List<String> translations) {
		this.translations = translations;
	}
}
