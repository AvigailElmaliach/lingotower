package com.lingotower.constants;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Constants for Quiz-related functionality
 */
public class QuizConstants {

	private QuizConstants() {
		// Private constructor to prevent instantiation
		throw new UnsupportedOperationException("Constants class cannot be instantiated");
	}

	// Style constants
	public static final String STYLE_TEXT_FILL_RED = "-fx-text-fill: #c62828;";
	public static final String STYLE_TEXT_FILL_GREEN = "-fx-text-fill: #2e7d32;";

	// Text constants
	public static final String BLANK_PLACEHOLDER = "_____";
	public static final String QUIZ_PREFIX = "Words Quiz - ";
	public static final String COMPLETION_PREFIX = "Sentence Completion - ";

	// Lists
	public static final ObservableList<String> FALLBACK_CATEGORIES = FXCollections.unmodifiableObservableList(
			FXCollections.observableArrayList("Everyday Life and Essential Vocabulary", "People and Relationships",
					"Work and Education", "Health and Well-being", "Travel and Leisure", "Environment and Nature"));

	// Quiz difficulties
	public static final ObservableList<String> DIFFICULTIES = FXCollections.observableArrayList("EASY", "MEDIUM",
			"HARD");
}