//package com.lingotower.ui.controllers.admin.admin;
//
//import java.util.Optional;
//
//import org.slf4j.Logger;
//
//import com.lingotower.model.Admin;
//import com.lingotower.utils.LoggingUtility;
//
//import javafx.scene.control.Alert;
//import javafx.scene.control.ButtonBar;
//import javafx.scene.control.ButtonType;
//import javafx.scene.control.Label;
//import javafx.stage.Window;
//
///**
// * Helper class for creating and managing dialogs and alerts. Centralizes dialog
// * creation logic.
// */
//public class DialogHelper {
//	private static final Logger logger = LoggingUtility.getLogger(DialogHelper.class);
//
//	private DialogHelper() {
//		// Private constructor to prevent instantiation
//		throw new UnsupportedOperationException("Utility class");
//	}
//
//	/**
//	 * Shows a status message in the UI.
//	 * 
//	 * @param statusLabel The label to show the message in
//	 * @param message     The message to show
//	 * @param isError     Whether this is an error message
//	 */
//	public static void showStatusMessage(Label statusLabel, String message, boolean isError) {
//		if (statusLabel == null)
//			return;
//
//		logger.debug("Status message: {}", message);
//
//		statusLabel.setText(message);
//		statusLabel.getStyleClass().removeAll("error-message", "success-message");
//		statusLabel.getStyleClass().add(isError ? "error-message" : "success-message");
//		statusLabel.setVisible(true);
//	}
//
//	/**
//	 * Auto-hides a status message after a delay.
//	 * 
//	 * @param statusLabel The label showing the message
//	 * @param delayMs     The delay in milliseconds before hiding
//	 */
//	public static void autoHideStatusMessage(Label statusLabel, long delayMs) {
//		if (statusLabel == null)
//			return;
//
//		Thread timerThread = new Thread(() -> {
//			try {
//				Thread.sleep(delayMs);
//				javafx.application.Platform.runLater(() -> statusLabel.setVisible(false));
//			} catch (InterruptedException e) {
//				logger.debug("Status message timer interrupted", e);
//			}
//		});
//		timerThread.setDaemon(true);
//		timerThread.setName("StatusMessageTimer");
//		timerThread.start();
//	}
//
//	/**
//	 * Shows a confirmation dialog for deleting an admin.
//	 * 
//	 * @param admin     The admin to delete
//	 * @param owner     The owner window for the dialog
//	 * @param onConfirm Callback when deletion is confirmed
//	 */
//	public static void showDeleteConfirmation(Admin admin, Window owner, Runnable onConfirm) {
//		if (admin == null) {
//			logger.warn("Cannot show confirmation for null admin");
//			return;
//		}
//
//		logger.info("Showing delete confirmation for admin: {} (ID: {})", admin.getUsername(), admin.getId());
//
//		// Create a confirmation dialog
//		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//		alert.setTitle("Delete Confirmation");
//		alert.setHeaderText("Are you sure you want to delete this admin?");
//		alert.setContentText("Username: " + admin.getUsername() + "\nEmail: "
//				+ (admin.getEmail() != null ? admin.getEmail() : "N/A"));
//
//		// Set the owner window if provided
//		if (owner != null) {
//			alert.initOwner(owner);
//		}
//
//		// Add buttons
//		ButtonType deleteButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
//		ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
//		alert.getButtonTypes().setAll(deleteButtonType, cancelButtonType);
//
//		// Show the dialog
//		Optional<ButtonType> result = alert.showAndWait();
//		if (result.isPresent() && result.get() == deleteButtonType) {
//			logger.info("Delete confirmed through dialog");
//			LoggingUtility.logAction(logger, "delete_admin", "system", "admin:" + admin.getUsername(), "confirmed");
//
//			if (onConfirm != null) {
//				onConfirm.run();
//			}
//		} else {
//			logger.info("Delete cancelled through dialog");
//			LoggingUtility.logAction(logger, "delete_admin", "system", "admin:" + admin.getUsername(), "cancelled");
//		}
//	}
//
//	/**
//	 * Shows an error alert.
//	 * 
//	 * @param title   The title of the alert
//	 * @param header  The header text
//	 * @param content The content text
//	 * @param owner   The owner window
//	 */
//	public static void showErrorAlert(String title, String header, String content, Window owner) {
//		logger.debug("Showing error alert: {}", content);
//
//		Alert alert = new Alert(Alert.AlertType.ERROR);
//		alert.setTitle(title);
//		alert.setHeaderText(header);
//		alert.setContentText(content);
//
//		if (owner != null) {
//			alert.initOwner(owner);
//		}
//
//		alert.showAndWait();
//	}
//
//	/**
//	 * Shows an information alert.
//	 * 
//	 * @param title   The title of the alert
//	 * @param header  The header text
//	 * @param content The content text
//	 * @param owner   The owner window
//	 */
//	public static void showInfoAlert(String title, String header, String content, Window owner) {
//		logger.debug("Showing info alert: {}", content);
//
//		Alert alert = new Alert(Alert.AlertType.INFORMATION);
//		alert.setTitle(title);
//		alert.setHeaderText(header);
//		alert.setContentText(content);
//
//		if (owner != null) {
//			alert.initOwner(owner);
//		}
//
//		alert.showAndWait();
//	}
//}