package com.lingotower.exception;

import com.fasterxml.jackson.databind.DatabindException;

import jakarta.validation.ConstraintViolationException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * Global exception handler for centralized error management across the
 * application.
 */
@RestControllerAdvice
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Handles AdminNotFoundException when an admin is not found.
	 */
	@ExceptionHandler(AdminNotFoundException.class)
	public ResponseEntity<String> handleAdminNotFoundException(AdminNotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	}

	/**
	 * Handles UserNotFoundException when a user is not found.
	 */
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	}

	/**
	 * Handles IllegalArgumentException for invalid method arguments.
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	}

	/**
	 * Handles SecurityException for unauthorized access attempts.
	 */
	@ExceptionHandler(SecurityException.class)
	public ResponseEntity<String> handleSecurityException(SecurityException e) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
	}

	/**
	 * Handles WordNotFoundException when a requested word is not found.
	 */
	@ExceptionHandler(WordNotFoundException.class)
	public ResponseEntity<String> handleWordNotFound(WordNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	/**
	 * Handles CategoryAlreadyExistsException when attempting to create a category
	 * that already exists.
	 */
	@ExceptionHandler(CategoryAlreadyExistsException.class)
	public ResponseEntity<String> handleCategoryAlreadyExistsException(CategoryAlreadyExistsException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	}

	/**
	 * Handles IOException during file operations.
	 */
	@ExceptionHandler(IOException.class)
	public ResponseEntity<String> handleIOException(IOException e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File handling error: " + e.getMessage());
	}

	/**
	 * Handles DatabindException when processing JSON fails.
	 */
	@ExceptionHandler(DatabindException.class)
	public ResponseEntity<String> handleDatabindException(DatabindException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error reading JSON: " + e.getMessage());
	}

	/**
	 * Handles all uncaught exceptions.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGeneralException(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("An unexpected error occurred. Please try again later.");
	}

	/**
	 * Handles validation errors for request parameters (e.g., @RequestParam). for
	 * get
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
		return ResponseEntity.badRequest().body("Validation failed: " + ex.getMessage());
	}

	/**
	 * Handles validation errors for @RequestBody objects (e.g., DTOs). for post and
	 * put
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
		return ResponseEntity.badRequest().body(errors);
	}

	/**
	 * Handles IncorrectPasswordException when the current password provided is
	 * incorrect.
	 */
	@ExceptionHandler(IncorrectPasswordException.class)
	public ResponseEntity<String> handleIncorrectPasswordException(IncorrectPasswordException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	}
}