package com.lingotower.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sentence {
	private Long id;
	private String questionText;
	private List<String> options;
	private String correctAnswer;
	private Category category;

	// Default constructor
	public Sentence() {
	}

	// Getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "SentenceDTO{" + "id=" + id + ", questionText='" + questionText + '\'' + ", correctAnswer='"
				+ correctAnswer + '\'' + ", category=" + (category != null ? category.getName() : "null") + ", options="
				+ (options != null ? options.toString() : "null") + '}';
	}
}