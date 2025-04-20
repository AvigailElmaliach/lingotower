package com.lingotower.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a quiz in the system. A quiz contains a set of questions, belongs
 * to a category, and has a difficulty level.
 */
public class Quiz {

	private Long id; // The unique identifier of the quiz
	private String name; // The name of the quiz
	private Category category; // The category to which the quiz belongs
	private Difficulty difficulty; // The difficulty level of the quiz
	private Admin adminByCreated; // The admin who created the quiz
	private List<Question> questions = new ArrayList<>(); // The list of questions in the quiz

	/**
	 * Default constructor for creating an empty Quiz object.
	 */
	public Quiz() {
	}

	/**
	 * Gets the unique identifier of the quiz.
	 *
	 * @return The quiz ID.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the unique identifier of the quiz.
	 *
	 * @param id The quiz ID to set.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the name of the quiz.
	 *
	 * @return The quiz name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the quiz.
	 *
	 * @param name The quiz name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the category to which the quiz belongs.
	 *
	 * @return The associated category.
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * Sets the category to which the quiz belongs.
	 *
	 * @param category The category to associate with the quiz.
	 */
	public void setCategory(Category category) {
		this.category = category;
	}

	/**
	 * Gets the difficulty level of the quiz.
	 *
	 * @return The difficulty level.
	 */
	public Difficulty getDifficulty() {
		return difficulty;
	}

	/**
	 * Sets the difficulty level of the quiz.
	 *
	 * @param difficulty The difficulty level to set.
	 */
	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	/**
	 * Gets the admin who created the quiz.
	 *
	 * @return The admin who created the quiz.
	 */
	public Admin getAdminByCreated() {
		return adminByCreated;
	}

	/**
	 * Sets the admin who created the quiz.
	 *
	 * @param adminByCreated The admin to associate with the quiz.
	 */
	public void setAdminByCreated(Admin adminByCreated) {
		this.adminByCreated = adminByCreated;
	}

	/**
	 * Gets the list of questions in the quiz.
	 *
	 * @return The list of questions.
	 */
	public List<Question> getQuestions() {
		return questions;
	}

	/**
	 * Adds a question to the quiz. Also sets the quiz reference in the question.
	 *
	 * @param question The question to add.
	 */
	public void addQuestion(Question question) {
		questions.add(question);
		question.setQuiz(this);
	}

	/**
	 * Returns a string representation of the quiz.
	 *
	 * @return A string containing the quiz details.
	 */
	@Override
	public String toString() {
		return "Quiz{" + "id=" + id + ", name='" + name + '\'' + ", category="
				+ (category != null ? category.getName() : "None") + ", difficulty=" + difficulty + ", adminByCreated="
				+ (adminByCreated != null ? adminByCreated.getId() : "None") + '}';
	}
}
