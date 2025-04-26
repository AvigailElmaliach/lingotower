package com.lingotower.utils.ui;

import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 * Utility class for UI-related operations
 */
public class UIUtils {

	private UIUtils() {
		// Private constructor to prevent instantiation
	}

	/**
	 * Shows a status message with auto-hiding for success messages
	 * 
	 * @param statusLabel The label to show the message in
	 * @param message     The message to show
	 * @param isError     Whether this is an error message
	 * @param hideDelayMs The delay in milliseconds before hiding success messages
	 *                    (0 to not auto-hide)
	 */
	public static void showStatusMessage(Label statusLabel, String message, boolean isError, long hideDelayMs) {
		if (statusLabel == null)
			return;

		Platform.runLater(() -> {
			statusLabel.setText(message);
			statusLabel.getStyleClass().removeAll("error-message", "success-message");
			statusLabel.getStyleClass().add(isError ? "error-message" : "success-message");
			statusLabel.setVisible(true);

			// Auto-hide success messages after delay if specified
			if (!isError && hideDelayMs > 0) {
				Thread timerThread = new Thread(() -> {
					try {
						Thread.sleep(hideDelayMs);
						Platform.runLater(() -> statusLabel.setVisible(false));
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				});
				timerThread.setDaemon(true);
				timerThread.setName("StatusMessageTimer");
				timerThread.start();
			}
		});
	}

	// Overload for backward compatibility
	public static void showStatusMessage(Label statusLabel, String message, boolean isError) {
		showStatusMessage(statusLabel, message, isError, isError ? 0 : 5000); // Auto-hide success after 5s by default
	}
}