package com.lingotower.ui.quiz;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.lingotower.model.Category;
import com.lingotower.model.Question;
import com.lingotower.utils.CategoryMappingUtils;
import com.lingotower.utils.LoggingUtility;

/**
 * Service class to generate fallback quiz questions when the API fails
 */
public class FallbackQuizGenerator {

	private static final Logger logger = LoggingUtility.getLogger(FallbackQuizGenerator.class);

	private FallbackQuizGenerator() {
		// Private constructor to prevent instantiation
		throw new UnsupportedOperationException("Utility class cannot be instantiated");
	}

	/**
	 * Creates fallback sentence completion questions when the API fails. This is
	 * just a temporary solution for testing.
	 */
	public static List<Question> createFallbackSentenceQuestions(String categoryName, String difficulty) {
		List<Question> questions = new ArrayList<>();

		// Create a category object
		Category category = new Category();
		// Handle null categoryName when setting ID and name
		if (categoryName != null && !categoryName.trim().isEmpty()) {
			category.setId(CategoryMappingUtils.getCategoryIdByName(categoryName));
			category.setName(categoryName);
		} else {
			// Provide a default category if categoryName is null or empty
			category.setId(1L); // Default ID
			category.setName("Default Fallback Category"); // Default name
			logger.warn("createFallbackSentenceQuestions received null or empty categoryName, using default category.");
		}

		logger.info("Creating fallback sentence completion questions for category: {}, difficulty: {}", categoryName,
				difficulty);

		// Create sample questions based on category
		if (categoryName != null && categoryName.contains("Everyday Life")) {
			addEverydayLifeQuestions(questions, category);
		} else if (categoryName != null && (categoryName.contains("People") || categoryName.contains("Relationship"))) {
			addPeopleRelationshipsQuestions(questions, category);
		} else if (categoryName != null && (categoryName.contains("Work") || categoryName.contains("Education"))) {
			addWorkEducationQuestions(questions, category);
		} else if (categoryName != null && (categoryName.contains("Health") || categoryName.contains("Well"))) {
			addHealthWellbeingQuestions(questions, category);
		} else if (categoryName != null && (categoryName.contains("Travel") || categoryName.contains("Leisure")
				|| categoryName.contains("טיולים"))) { // Added check for "טיולים"
			addTravelLeisureQuestions(questions, category);
		} else if (categoryName != null && (categoryName.contains("Environment") || categoryName.contains("Nature"))) {
			addEnvironmentNatureQuestions(questions, category);
		} else {
			addDefaultQuestions(questions, category);
		}

		// Add difficulty-based questions
		addDifficultyBasedQuestions(questions, category, difficulty);

		logger.info("Created {} fallback sentence completion questions", questions.size());
		return questions;
	}

	private static void addEverydayLifeQuestions(List<Question> questions, Category category) {
		// Question 1
		Question q1 = new Question();
		q1.setQuestionText("I _____ to make a phone call.");
		q1.setCorrectAnswer("need");
		q1.setOptions(List.of("need", "thank you", "hello", "goodbye", "please"));
		q1.setCategory(category);
		questions.add(q1);

		// Question 2
		Question q2 = new Question();
		q2.setQuestionText("Please _____ me the directions to the store.");
		q2.setCorrectAnswer("give");
		q2.setOptions(List.of("give", "happy", "blue", "seven", "today"));
		q2.setCategory(category);
		questions.add(q2);

		// Question 3
		Question q3 = new Question();
		q3.setQuestionText("What time _____ it?");
		q3.setCorrectAnswer("is");
		q3.setOptions(List.of("is", "are", "car", "house", "day"));
		q3.setCategory(category);
		questions.add(q3);
	}

	private static void addPeopleRelationshipsQuestions(List<Question> questions, Category category) {
		// Question 1
		Question q1 = new Question();
		q1.setQuestionText("My _____ birthday is next week.");
		q1.setCorrectAnswer("sister's");
		q1.setOptions(List.of("sister's", "table", "book", "window", "computer"));
		q1.setCategory(category);
		questions.add(q1);

		// Question 2
		Question q2 = new Question();
		q2.setQuestionText("We are going to _____ my grandparents this weekend.");
		q2.setCorrectAnswer("visit");
		q2.setOptions(List.of("visit", "cat", "red", "play", "sleep"));
		q2.setCategory(category);
		questions.add(q2);
	}

	private static void addWorkEducationQuestions(List<Question> questions, Category category) {
		// Question 1
		Question q1 = new Question();
		q1.setQuestionText("I have a meeting with my _____ today.");
		q1.setCorrectAnswer("boss");
		q1.setOptions(List.of("boss", "apple", "car", "dog", "tree"));
		q1.setCategory(category);
		questions.add(q1);

		// Question 2
		Question q2 = new Question();
		q2.setQuestionText("Please _____ this document by tomorrow.");
		q2.setCorrectAnswer("complete");
		q2.setOptions(List.of("complete", "orange", "table", "chair", "computer"));
		q2.setCategory(category);
		questions.add(q2);
	}

	private static void addHealthWellbeingQuestions(List<Question> questions, Category category) {
		// Question 1
		Question q1 = new Question();
		q1.setQuestionText("I have a _____ appointment next week.");
		q1.setCorrectAnswer("doctor's");
		q1.setOptions(List.of("doctor's", "banana", "pen", "shoes", "book"));
		q1.setCategory(category);
		questions.add(q1);

		// Question 2
		Question q2 = new Question();
		q2.setQuestionText("You should _____ more vegetables for better health.");
		q2.setCorrectAnswer("eat");
		q2.setOptions(List.of("eat", "sing", "shoe", "house", "car"));
		q2.setCategory(category);
		questions.add(q2);
	}

	private static void addTravelLeisureQuestions(List<Question> questions, Category category) {
		// Question 1
		Question q1 = new Question();
		q1.setQuestionText("We need to _____ our tickets before the flight.");
		q1.setCorrectAnswer("book");
		q1.setOptions(List.of("book", "dance", "dog", "table", "window"));
		q1.setCategory(category);
		questions.add(q1);

		// Question 2
		Question q2 = new Question();
		q2.setQuestionText("What time does the _____ leave?");
		q2.setCorrectAnswer("train");
		q2.setOptions(List.of("train", "cat", "book", "apple", "chair"));
		q2.setCategory(category);
		questions.add(q2);
	}

	private static void addEnvironmentNatureQuestions(List<Question> questions, Category category) {
		// Question 1
		Question q1 = new Question();
		q1.setQuestionText("We should _____ trees to help the environment.");
		q1.setCorrectAnswer("plant");
		q1.setOptions(List.of("plant", "happy", "cat", "pen", "table"));
		q1.setCategory(category);
		questions.add(q1);

		// Question 2
		Question q2 = new Question();
		q2.setQuestionText("The _____ is rising due to climate change.");
		q2.setCorrectAnswer("temperature");
		q2.setOptions(List.of("temperature", "shoe", "dog", "book", "window"));
		q2.setCategory(category);
		questions.add(q2);
	}

	private static void addDefaultQuestions(List<Question> questions, Category category) {
		// Default questions for any category
		Question q1 = new Question();
		q1.setQuestionText("I _____ to learn this language.");
		q1.setCorrectAnswer("want");
		q1.setOptions(List.of("want", "blue", "car", "apple", "house"));
		q1.setCategory(category);
		questions.add(q1);

		Question q2 = new Question();
		q2.setQuestionText("What is your _____?");
		q2.setCorrectAnswer("name");
		q2.setOptions(List.of("name", "blue", "water", "chair", "tree"));
		q2.setCategory(category);
		questions.add(q2);
	}

	private static void addDifficultyBasedQuestions(List<Question> questions, Category category, String difficulty) {
		if ("HARD".equals(difficulty)) {
			Question q = new Question();
			q.setQuestionText("The company _____ its annual report yesterday.");
			q.setCorrectAnswer("published");
			q.setOptions(List.of("published", "happy", "blue", "run", "eat"));
			q.setCategory(category);
			questions.add(q);

			Question q2 = new Question();
			q2.setQuestionText("She _____ the complex mathematical problem in record time.");
			q2.setCorrectAnswer("solved");
			q2.setOptions(List.of("solved", "garden", "tree", "pencil", "school"));
			q2.setCategory(category);
			questions.add(q2);
		} else if ("MEDIUM".equals(difficulty)) {
			Question q = new Question();
			q.setQuestionText("Could you _____ me the salt, please?");
			q.setCorrectAnswer("pass");
			q.setOptions(List.of("pass", "desk", "banana", "picture", "sun"));
			q.setCategory(category);
			questions.add(q);
		}
	}
}