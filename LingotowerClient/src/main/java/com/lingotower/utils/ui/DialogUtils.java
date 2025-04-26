package com.lingotower.utils.ui;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

/**
 * Utility class for dialog-related operations
 */
public class DialogUtils {
	private static final Logger logger = LoggerFactory.getLogger(DialogUtils.class);

	private DialogUtils() {
		// Private constructor to prevent instantiation
	}

	/**
	 * Show a delete confirmation dialog
	 * 
	 * @param itemType The type of item to delete (e.g., "category", "word")
	 * @param itemName The name of the item to delete
	 * @return true if the user confirmed the deletion, false otherwise
	 */
	public static boolean showDeleteConfirmation(String itemType, String itemName) {
		logger.info("Showing delete confirmation for {}: {}", itemType, itemName);

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete Confirmation");
		alert.setHeaderText("Are you sure you want to delete this " + itemType + "?");
		alert.setContentText("Item: " + itemName);

		ButtonType confirmButton = new ButtonType("Delete");
		ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(confirmButton, cancelButton);

		Optional<ButtonType> result = alert.showAndWait();
		boolean confirmed = result.isPresent() && result.get() == confirmButton;

		logger.info("Delete confirmation result: {}", confirmed ? "confirmed" : "cancelled");
		return confirmed;
	}

	/**
	 * Show an error alert
	 * 
	 * @param title   The title of the alert
	 * @param header  The header text
	 * @param content The content text
	 */
	public static void showErrorAlert(String title, String header, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	/**
	 * Show an information alert
	 * 
	 * @param title   The title of the alert
	 * @param header  The header text
	 * @param content The content text
	 */
	public static void showInfoAlert(String title, String header, String content) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
}