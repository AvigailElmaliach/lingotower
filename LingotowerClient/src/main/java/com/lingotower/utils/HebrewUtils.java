package com.lingotower.utils;

import org.slf4j.Logger;

/**
 * Utility class for Hebrew language related operations
 */
public class HebrewUtils {

	private static final Logger logger = LoggingUtility.getLogger(HebrewUtils.class);

	private HebrewUtils() {
		// Private constructor to prevent instantiation
		throw new UnsupportedOperationException("Utility class cannot be instantiated");
	}

	/**
	 * Checks if a string contains Hebrew characters.
	 * 
	 * @param text The text to check
	 * @return True if the text contains Hebrew characters, false otherwise
	 */
	public static boolean containsHebrew(String text) {
		if (text == null || text.isEmpty()) {
			return false;
		}

		// Check if any character in the string belongs to the Hebrew Unicode block
		return text.codePoints().anyMatch(c -> Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HEBREW);
	}
}