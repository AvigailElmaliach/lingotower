package com.lingotower.ui.components;

import com.lingotower.model.User;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

/**
 * A custom table cell that contains Edit and Delete buttons for user
 * management.
 */
public class ActionButtonCell extends TableCell<User, String> {

	private final Button editButton = new Button("Edit");
	private final Button deleteButton = new Button("Delete");
	private final HBox pane = new HBox(10); // Increased spacing between buttons

	public ActionButtonCell(EventHandler<ActionEvent> onEdit, EventHandler<ActionEvent> onDelete) {
		// Style the buttons
		editButton.getStyleClass().addAll("small-button", "edit-button");
		editButton.setPrefWidth(80); // Fixed width for consistency

		deleteButton.getStyleClass().addAll("small-button", "danger-button", "delete-button");
		deleteButton.setPrefWidth(80); // Fixed width for consistency

		// Set the action handlers
		editButton.setOnAction(event -> {
			if (getTableView() == null || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
				return; // Protect against index errors
			}

			User user = getTableView().getItems().get(getIndex());
			// Call the provided edit handler
			onEdit.handle(new ActionEvent(user, event.getTarget()));
		});

		deleteButton.setOnAction(event -> {
			if (getTableView() == null || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
				return; // Protect against index errors
			}

			User user = getTableView().getItems().get(getIndex());
			// Call the provided delete handler
			onDelete.handle(new ActionEvent(user, event.getTarget()));
		});

		// Add buttons to the HBox pane with proper styling
		pane.getChildren().addAll(editButton, deleteButton);
		pane.setPadding(new Insets(2));
		pane.setAlignment(Pos.CENTER); // Center the buttons
	}

	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setGraphic(null);
		} else {
			setGraphic(pane);
			// Center the cell content
			setAlignment(Pos.CENTER);
		}
	}
}