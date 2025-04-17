package com.lingotower.constants;

public class ValidationConstants {
    // Validation messages
    public static final String ERROR_ALL_FIELDS_REQUIRED = "All fields are required";
    public static final String ERROR_USERNAME_LENGTH = "Username must be between 3 and 20 characters";
    public static final String ERROR_USERNAME_FORMAT = "Username can only contain letters, numbers, and underscores";
    public static final String ERROR_EMAIL_FORMAT = "Invalid email format";
    public static final String ERROR_PASSWORD_LENGTH = "Password must be at least 8 characters long";
    public static final String ERROR_PASSWORD_UPPERCASE = "Password must contain at least one uppercase letter";
    public static final String ERROR_PASSWORD_LOWERCASE = "Password must contain at least one lowercase letter";
    public static final String ERROR_PASSWORD_NUMBER = "Password must contain at least one number";
    public static final String ERROR_PASSWORD_MISMATCH = "Passwords do not match";
    public static final String ERROR_LANGUAGE_SELECTION = "Please select a valid language";

    // Length limits
    public static final int USERNAME_MIN_LENGTH = 3;
    public static final int USERNAME_MAX_LENGTH = 20;
    public static final int PASSWORD_MIN_LENGTH = 8;

    // Regex patterns
    public static final String REGEX_USERNAME = "^[a-zA-Z0-9_]+$";
    public static final String REGEX_EMAIL = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
    public static final String REGEX_PASSWORD_UPPERCASE = ".*[A-Z].*";
    public static final String REGEX_PASSWORD_LOWERCASE = ".*[a-z].*";
    public static final String REGEX_PASSWORD_NUMBER = ".*\\d.*";
}
