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
     * Show a status message in a label
     * 
     * @param statusLabel The label to show the message in
     * @param message The message to show
     * @param isError Whether the message is an error message
     */
    public static void showStatusMessage(Label statusLabel, String message, boolean isError) {
        if (statusLabel == null) return;
        
        Platform.runLater(() -> {
            statusLabel.setText(message);
            statusLabel.getStyleClass().removeAll("error-message", "success-message");
            statusLabel.getStyleClass().add(isError ? "error-message" : "success-message");
            statusLabel.setVisible(true);
            
            // Auto-hide success messages after 5 seconds
            if (!isError) {
                Thread timerThread = new Thread(() -> {
                    try {
                        Thread.sleep(5000);
                        Platform.runLater(() -> statusLabel.setVisible(false));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                timerThread.setDaemon(true);
                timerThread.start();
            }
        });
    }
}