package com.lingotower.utils;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lingotower.constants.ValidationConstants;

/**
 * Utility class for validating user input across the application. Centralizes
 * validation logic for consistency.
 */
public class ValidationUtility {

	private static final Logger logger = LoggerFactory.getLogger(ValidationUtility.class);

	/**
	 * Validates username format and length
	 * 
	 * @param username The username to validate
	 * @return Validation error message or null if valid
	 */
	public static String validateUsername(String username) {
		if (username == null || username.trim().isEmpty()) {
			return ValidationConstants.ERROR_ALL_FIELDS_REQUIRED;
		}

		username = username.trim();

		if (username.length() < ValidationConstants.USERNAME_MIN_LENGTH
				|| username.length() > ValidationConstants.USERNAME_MAX_LENGTH) {
			return ValidationConstants.ERROR_USERNAME_LENGTH;
		}

		if (!Pattern.matches(ValidationConstants.REGEX_USERNAME, username)) {
			return ValidationConstants.ERROR_USERNAME_FORMAT;
		}

		return null; // No error
	}

	/**
	 * Validates email format
	 * 
	 * @param email The email to validate
	 * @return Validation error message or null if valid
	 */
	public static String validateEmail(String email) {
		if (email == null || email.trim().isEmpty()) {
			return ValidationConstants.ERROR_ALL_FIELDS_REQUIRED;
		}

		email = email.trim();

		if (!Pattern.matches(ValidationConstants.REGEX_EMAIL, email)) {
			return ValidationConstants.ERROR_EMAIL_FORMAT;
		}

		return null; // No error
	}

	/**
	 * Validates password strength and format
	 * 
	 * @param password The password to validate
	 * @return Validation error message or null if valid
	 */
	public static String validatePassword(String password) {
		if (password == null || password.isEmpty()) {
			return ValidationConstants.ERROR_ALL_FIELDS_REQUIRED;
		}

		if (password.length() < ValidationConstants.PASSWORD_MIN_LENGTH) {
			return ValidationConstants.ERROR_PASSWORD_LENGTH;
		}

		if (!Pattern.matches(ValidationConstants.REGEX_PASSWORD_UPPERCASE, password)) {
			return ValidationConstants.ERROR_PASSWORD_UPPERCASE;
		}

		if (!Pattern.matches(ValidationConstants.REGEX_PASSWORD_LOWERCASE, password)) {
			return ValidationConstants.ERROR_PASSWORD_LOWERCASE;
		}

		if (!Pattern.matches(ValidationConstants.REGEX_PASSWORD_NUMBER, password)) {
			return ValidationConstants.ERROR_PASSWORD_NUMBER;
		}

		return null; // No error
	}

	/**
	 * Checks if password confirmation matches
	 * 
	 * @param password     The original password
	 * @param confirmation The confirmation password to compare
	 * @return Validation error message or null if valid
	 */
	public static String validatePasswordMatch(String password, String confirmation) {
		if (confirmation == null || confirmation.isEmpty()) {
			return ValidationConstants.ERROR_ALL_FIELDS_REQUIRED;
		}

		if (!password.equals(confirmation)) {
			return ValidationConstants.ERROR_PASSWORD_MISMATCH;
		}

		return null; // No error
	}

	/**
	 * Validates all registration fields in one call
	 * 
	 * @param username        The username to validate
	 * @param email           The email to validate
	 * @param password        The password to validate
	 * @param confirmPassword The confirmation password
	 * @param language        The selected language
	 * @return Validation error message or null if all fields are valid
	 */
	public static String validateRegistration(String username, String email, String password, String confirmPassword,
			String language) {
		logger.debug("Validating registration data for user: {}", username);

		// Check required language
		if (language == null || language.isEmpty()) {
			return ValidationConstants.ERROR_LANGUAGE_SELECTION;
		}

		// Check username
		String usernameError = validateUsername(username);
		if (usernameError != null) {
			return usernameError;
		}

		// Check email
		String emailError = validateEmail(email);
		if (emailError != null) {
			return emailError;
		}

		// Check password
		String passwordError = validatePassword(password);
		if (passwordError != null) {
			return passwordError;
		}

		// Check password confirmation
		String confirmError = validatePasswordMatch(password, confirmPassword);
		if (confirmError != null) {
			return confirmError;
		}

		// All validations passed
		logger.debug("All validation checks passed for user: {}", username);
		return null;
	}
}