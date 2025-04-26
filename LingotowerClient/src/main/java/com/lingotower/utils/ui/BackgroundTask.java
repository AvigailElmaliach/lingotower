package com.lingotower.utils.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for running tasks in background threads
 */
public class BackgroundTask {
	private static final Logger logger = LoggerFactory.getLogger(BackgroundTask.class);

	private BackgroundTask() {
		// Private constructor to prevent instantiation
	}

	/**
	 * Run a task in a background thread
	 * 
	 * @param taskName Name of the task for logging and thread naming
	 * @param action   The action to execute
	 */
	public static void run(String taskName, TaskAction action) {
		long startTime = System.currentTimeMillis();
		Thread thread = new Thread(() -> {
			try {
				action.execute(startTime);
			} catch (Exception e) {
				logger.error("Error in background task '{}': {}", taskName, e.getMessage(), e);
			}
		});
		thread.setDaemon(true);
		thread.setName(taskName + "Thread");
		thread.start();
	}

	/**
	 * Functional interface for task actions
	 */
	@FunctionalInterface
	public interface TaskAction {
		void execute(long startTime) throws Exception;
	}
}