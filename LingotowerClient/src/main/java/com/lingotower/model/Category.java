package com.lingotower.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a category in the system. A category can have quizzes and
 * questions associated with it.
 */
public class Category {

	private Long id; // The unique identifier of the category
	private String name; // The name of the category
	private String translation; // The translation of the category name
	private List<Quiz> quizzes = new ArrayList<>(); // The list of quizzes associated with the category
	private List<Question> questions = new ArrayList<>(); // The list of questions associated with the category

	/**
	 * Default constructor for creating an empty Category object.
	 */
	public Category() {
	}

	/**
	 * Constructor for creating a Category object with an ID and name.
	 *
	 * @param id   The unique identifier of the category.
	 * @param name The name of the category.
	 */
	public Category(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Gets the unique identifier of the category.
	 *
	 * @return The category ID.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the unique identifier of the category.
	 *
	 * @param id The category ID to set.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the name of the category.
	 *
	 * @return The category name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the category.
	 *
	 * @param name The category name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the translation of the category name.
	 *
	 * @return The category translation.
	 */
	public String getTranslation() {
		return translation;
	}

	/**
	 * Sets the translation of the category name.
	 *
	 * @param translation The category translation to set.
	 */
	public void setTranslation(String translation) {
		this.translation = translation;
	}

	/**
	 * Gets the list of quizzes associated with the category.
	 *
	 * @return The list of quizzes.
	 */
	public List<Quiz> getQuizzes() {
		return quizzes;
	}

	/**
	 * Adds a quiz to the category.
	 *
	 * @param quiz The quiz to add.
	 */
	public void addQuiz(Quiz quiz) {
		quizzes.add(quiz);
	}

	/**
	 * Gets the list of questions associated with the category.
	 *
	 * @return The list of questions.
	 */
	public List<Question> getQuestions() {
		return questions;
	}

	/**
	 * Adds a question to the category.
	 *
	 * @param question The question to add.
	 */
	public void addQuestion(Question question) {
		questions.add(question);
	}

	/**
	 * Checks if this category is equal to another object. Two categories are
	 * considered equal if their IDs are the same.
	 *
	 * @param o The object to compare.
	 * @return True if the categories are equal, false otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Category category = (Category) o;
		return id != null && id.equals(category.id);
	}

	/**
	 * Returns a string representation of the category.
	 *
	 * @return A string containing the category details.
	 */
	@Override
	public String toString() {
		return "Category{id=" + id + ", name='" + name + "'}";
	}
}
