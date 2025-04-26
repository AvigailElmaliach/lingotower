package com.lingotower.ui.controllers.wordlearning;

import org.slf4j.Logger;

import com.lingotower.utils.LoggingUtility;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

/**
 * Manages the progress display for word learning
 */
public class ProgressManager {
	private static final Logger logger = LoggingUtility.getLogger(ProgressManager.class);

	// UI Components
	private final ProgressBar progressBar;
	private final Label progressLabel;

	public ProgressManager(ProgressBar progressBar, Label progressLabel) {
		this.progressBar = progressBar;
		this.progressLabel = progressLabel;

		// Initialize to zero progress
		reset();
	}

	/**
	 * Resets progress to zero
	 */
	public void reset() {
		progressBar.setProgress(0);
		progressLabel.setText("0/0");
	}

	/**
	 * Updates the progress display
	 * 
	 * @param currentIndex The current word index (0-based)
	 * @param totalSize    The total number of words
	 */
	public void updateProgress(int currentIndex, int totalSize) {
		if (totalSize <= 0) {
			logger.warn("Cannot update progress: total size is zero or negative");
			reset();
			return;
		}

		// Calculate progress (make sure index is within bounds for display)
		int displayIndex = Math.min(currentIndex, totalSize - 1);
		// Add 1 to convert from 0-based to 1-based for display
		double progress = (double) (displayIndex + 1) / totalSize;

		// Update UI
		progressBar.setProgress(progress);
		progressLabel.setText((displayIndex + 1) + "/" + totalSize);

		logger.trace("Progress updated: {} out of {} words ({}%)", displayIndex + 1, totalSize,
				Math.round(progress * 100));
	}
}