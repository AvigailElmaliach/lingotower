package com.lingotower.ui.controllers.admin.admin;

import org.slf4j.Logger;

import com.lingotower.constants.ValidationConstants;
import com.lingotower.model.Admin;
import com.lingotower.utils.LoggingUtility;
import com.lingotower.utils.ValidationUtility;

/**
 * Validator class for Admin form inputs. Separates validation logic from
 * controllers. Leverages existing ValidationUtility and ValidationConstants.
 */
public class AdminFormValidator {
	private static final Logger logger = LoggingUtility.getLogger(AdminFormValidator.class);

	private AdminFormValidator() {
		// Private constructor to prevent instantiation
		throw new UnsupportedOperationException("Utility class");
	}

	/**
	 * Validates admin form inputs.
	 * 
	 * @param username   The username to validate
	 * @param email      The email to validate
	 * @param password   The password to validate (can be empty for updates)
	 * @param role       The role to validate
	 * @param isNewAdmin Whether this is for a new admin (requires password)
	 * @return A validation result with error message if validation fails
	 */
	public static ValidationResult validateAdminForm(String username, String email, String password, String role,
			boolean isNewAdmin) {
		// Check username using existing ValidationUtility
		String usernameError = ValidationUtility.validateUsername(username);
		if (usernameError != null) {
			logger.warn("Validation failed: invalid username: {}", usernameError);
			return new ValidationResult(false, usernameError);
		}

		// Check email using existing ValidationUtility
		String emailError = ValidationUtility.validateEmail(email);
		if (emailError != null) {
			logger.warn("Validation failed: invalid email: {}", emailError);
			return new ValidationResult(false, emailError);
		}

		// Check password (required for new admins, optional for updates)
		if (isNewAdmin && (password == null || password.isEmpty())) {
			logger.warn("Validation failed: password required for new admin");
			return new ValidationResult(false, ValidationConstants.ERROR_ALL_FIELDS_REQUIRED);
		}

		// If password is provided (new admin or password change), validate it
		if (password != null && !password.isEmpty()) {
			String passwordError = ValidationUtility.validatePassword(password);
			if (passwordError != null) {
				logger.warn("Validation failed: invalid password: {}", passwordError);
				return new ValidationResult(false, passwordError);
			}
		}

		// Check role
		if (role == null || role.trim().isEmpty()) {
			logger.warn("Validation failed: role is empty");
			return new ValidationResult(false, "Role is required");
		}

		// All validations passed
		logger.debug("Admin form validation passed");
		return new ValidationResult(true, null);
	}

	/**
	 * Validates if an admin can be deleted.
	 * 
	 * @param adminToDelete The admin to delete
	 * @param currentAdmin  The current logged-in admin
	 * @return A validation result with error message if validation fails
	 */
	public static ValidationResult validateAdminDeletion(Admin adminToDelete, Admin currentAdmin) {
		if (adminToDelete == null || adminToDelete.getId() == null) {
			logger.warn("Validation failed: admin to delete is null or has no ID");
			return new ValidationResult(false, "Invalid admin data for deletion");
		}

		// Don't allow deleting yourself
		if (currentAdmin != null && currentAdmin.getId() != null
				&& adminToDelete.getId().equals(currentAdmin.getId())) {
			logger.warn("Validation failed: attempting to delete own admin account");
			return new ValidationResult(false, "You cannot delete your own admin account");
		}

		return new ValidationResult(true, null);
	}

	/**
	 * Class to encapsulate validation results.
	 */
	public static class ValidationResult {
		private final boolean valid;
		private final String errorMessage;

		public ValidationResult(boolean valid, String errorMessage) {
			this.valid = valid;
			this.errorMessage = errorMessage;
		}

		public boolean isValid() {
			return valid;
		}

		public String getErrorMessage() {
			return errorMessage;
		}
	}
}