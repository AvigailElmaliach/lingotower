package com.lingotower.ui.components;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

/**
 * A custom generic table cell that contains Edit and Delete buttons.
 * 
 * @param <S> The type of the TableView items.
 */
public class ActionButtonCell<S> extends TableCell<S, String> {

	private final Button editButton = new Button("Edit");
	private final Button deleteButton = new Button("Delete");
	private final HBox pane = new HBox(10); // Increased spacing between buttons

	public ActionButtonCell(EventHandler<ActionEvent> onEdit, EventHandler<ActionEvent> onDelete) {
		// Style the buttons
		editButton.getStyleClass().addAll("small-button", "edit-button");
		editButton.setPrefWidth(80);

		deleteButton.getStyleClass().addAll("small-button", "danger-button", "delete-button"); // <-- אם רוצים כפתור קטן
		deleteButton.setPrefWidth(80);

		// Set the action handlers
		editButton.setOnAction(event -> {
			if (getTableView() == null || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
				return; // Protect against index errors
			}

			S item = getTableView().getItems().get(getIndex());
			// Call the provided edit handler, sending the item itself as the source
			onEdit.handle(new ActionEvent(item, event.getTarget()));
		});

		deleteButton.setOnAction(event -> {
			if (getTableView() == null || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
				return; // Protect against index errors
			}

			S item = getTableView().getItems().get(getIndex());
			// Call the provided delete handler, sending the item itself as the source
			onDelete.handle(new ActionEvent(item, event.getTarget()));
		});

		// Add buttons to the HBox pane with proper styling
		pane.getChildren().addAll(editButton, deleteButton);
		pane.setPadding(new Insets(2));
		pane.setAlignment(Pos.CENTER); // Center the buttons in the HBox
	}

	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setGraphic(null);
		} else {
			setGraphic(pane);
			// Center the cell content (HBox) vertically and horizontally
			setAlignment(Pos.CENTER);
		}
	}
}