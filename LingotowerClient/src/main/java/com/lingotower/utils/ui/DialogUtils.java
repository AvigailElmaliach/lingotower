package com.lingotower.utils.ui;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lingotower.model.Admin;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

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
	 * Shows a delete confirmation dialog for an object with owner window
	 * 
	 * @param itemType  The type of item to delete (e.g., "admin", "category")
	 * @param itemName  The name of the item to delete
	 * @param owner     The owner window for the dialog
	 * @param onConfirm Callback when deletion is confirmed
	 */
	public static void showDeleteConfirmation(String itemType, String itemName, Window owner, Runnable onConfirm) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete Confirmation");
		alert.setHeaderText("Are you sure you want to delete this " + itemType + "?");
		alert.setContentText("Item: " + itemName);

		// Set owner if provided
		if (owner != null) {
			alert.initOwner(owner);
		}

		ButtonType confirmButton = new ButtonType("Delete");
		ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(confirmButton, cancelButton);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == confirmButton && onConfirm != null) {
			onConfirm.run();
		}
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

	/**
	 * Shows a delete confirmation dialog for an admin
	 * 
	 * @param admin     The admin to delete
	 * @param owner     The owner window for the dialog
	 * @param onConfirm Callback when deletion is confirmed
	 */
	public static void showAdminDeleteConfirmation(Admin admin, Window owner, Runnable onConfirm) {
		if (admin == null) {
			return;
		}

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete Confirmation");
		alert.setHeaderText("Are you sure you want to delete this admin?");
		alert.setContentText("Username: " + admin.getUsername() + "\nEmail: "
				+ (admin.getEmail() != null ? admin.getEmail() : "N/A"));

		// Set owner if provided
		if (owner != null) {
			alert.initOwner(owner);
		}

		ButtonType deleteButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
		ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(deleteButtonType, cancelButtonType);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == deleteButtonType && onConfirm != null) {
			onConfirm.run();
		}
	}
}