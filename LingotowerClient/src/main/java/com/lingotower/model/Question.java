package com.lingotower.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a question in the system. A question belongs to a quiz and a
 * category, and contains the correct answer, wrong answers, and other related
 * details.
 */
@JsonIgnoreProperties(ignoreUnknown = true) // Ignores unknown JSON properties during deserialization
public class Question {

	private Long id; // The unique identifier of the question
	private String questionText; // The text of the question
	private String correctAnswer; // The correct answer to the question
	private List<String> wrongAnswers; // A list of incorrect answers
	private List<String> options; // A list of all possible answer options (including correct and wrong answers)
	private Quiz quiz; // The quiz to which this question belongs
	private Category category; // The category to which this question belongs

	/**
	 * Default constructor for creating an empty Question object. Required for
	 * frameworks like Jackson to deserialize JSON into this class.
	 */
	public Question() {
	}

	/**
	 * Gets the unique identifier of the question.
	 *
	 * @return The question ID.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the unique identifier of the question.
	 *
	 * @param id The question ID to set.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the text of the question.
	 *
	 * @return The question text.
	 */
	public String getQuestionText() {
		return questionText;
	}

	/**
	 * Sets the text of the question.
	 *
	 * @param questionText The question text to set.
	 */
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	/**
	 * Gets the correct answer to the question.
	 *
	 * @return The correct answer.
	 */
	public String getCorrectAnswer() {
		return correctAnswer;
	}

	/**
	 * Sets the correct answer to the question.
	 *
	 * @param correctAnswer The correct answer to set.
	 */
	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	/**
	 * Gets the list of incorrect answers.
	 *
	 * @return The list of wrong answers.
	 */
	public List<String> getWrongAnswers() {
		return wrongAnswers;
	}

	/**
	 * Sets the list of incorrect answers.
	 *
	 * @param wrongAnswers The list of wrong answers to set.
	 */
	public void setWrongAnswers(List<String> wrongAnswers) {
		this.wrongAnswers = wrongAnswers;
	}

	/**
	 * Gets the list of all possible answer options.
	 *
	 * @return The list of options.
	 */
	public List<String> getOptions() {
		return options;
	}

	/**
	 * Sets the list of all possible answer options.
	 *
	 * @param options The list of options to set.
	 */
	public void setOptions(List<String> options) {
		this.options = options;
	}

	/**
	 * Gets the quiz to which this question belongs.
	 *
	 * @return The associated quiz.
	 */
	public Quiz getQuiz() {
		return quiz;
	}

	/**
	 * Sets the quiz to which this question belongs.
	 *
	 * @param quiz The quiz to associate with this question.
	 */
	public void setQuiz(Quiz quiz) {
		this.quiz = quiz;
	}

	/**
	 * Gets the category to which this question belongs.
	 *
	 * @return The associated category.
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * Sets the category to which this question belongs.
	 *
	 * @param category The category to associate with this question.
	 */
	public void setCategory(Category category) {
		this.category = category;
	}
}
