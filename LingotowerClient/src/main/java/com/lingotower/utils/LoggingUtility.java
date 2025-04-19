package com.lingotower.utils;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Utility class for enhanced logging functionality. Provides methods for
 * setting up user context and creating standardized log messages.
 */
public class LoggingUtility {

	/**
	 * Private constructor to prevent instantiation of the utility class.
	 */
	private LoggingUtility() {
		throw new UnsupportedOperationException("Utility class cannot be instantiated");
	}

	/**
	 * Sets the current user in the Mapped Diagnostic Context (MDC) for logging.
	 * This allows user information to be included in all log messages within this
	 * thread.
	 * 
	 * @param username The username to include in logs
	 * @return A unique request ID for correlation
	 */
	public static String setUserContext(String username) {
		String requestId = generateRequestId();
		MDC.put("username", username);
		MDC.put("requestId", requestId);
		return requestId;
	}

	/**
	 * Clears the user context from MDC when no longer needed
	 */
	public static void clearUserContext() {
		MDC.remove("username");
		MDC.remove("requestId");
	}

	/**
	 * Generates a unique request ID for tracking log entries across operations
	 * 
	 * @return A unique identifier string
	 */
	private static String generateRequestId() {
		return UUID.randomUUID().toString().substring(0, 8);
	}

	/**
	 * Creates a logger for the specified class
	 * 
	 * @param clazz The class to create a logger for
	 * @return A configured SLF4J Logger instance
	 */
	public static Logger getLogger(Class<?> clazz) {
		return LoggerFactory.getLogger(clazz);
	}

	/**
	 * Logs an application event with parameters in a standardized format
	 * 
	 * @param logger  The SLF4J logger to use
	 * @param event   The event name or type
	 * @param status  The status of the event (SUCCESS, FAILURE, etc.)
	 * @param details Additional details about the event
	 */
	public static void logEvent(Logger logger, String event, String status, String details) {
		logger.info("EVENT [{}] STATUS [{}] DETAILS [{}]", event, status, details);
	}

	/**
	 * Logs an application action (user or system initiated)
	 * 
	 * @param logger    The SLF4J logger to use
	 * @param action    The action being performed
	 * @param performer Who/what is performing the action
	 * @param target    The target of the action
	 * @param result    The result of the action
	 */
	public static void logAction(Logger logger, String action, String performer, String target, String result) {
		logger.info("ACTION [{}] BY [{}] ON [{}] RESULT [{}]", action, performer, target, result);
	}

	/**
	 * Logs performance metrics for important operations
	 * 
	 * @param logger         The SLF4J logger to use
	 * @param operation      The operation being measured
	 * @param durationMs     The duration in milliseconds
	 * @param additionalInfo Any additional context information
	 */
	public static void logPerformance(Logger logger, String operation, long durationMs, String additionalInfo) {
		logger.info("PERF [{}] DURATION [{}ms] INFO [{}]", operation, durationMs, additionalInfo);
	}
}