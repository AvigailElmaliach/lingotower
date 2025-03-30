package com.lingotower.ui.controllers;

import com.lingotower.model.Category;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller for an individual category tile
 */
public class CategoryTileController {

	@FXML
	private VBox categoryBox;

	@FXML
	private Label categoryNameLabel;

	@FXML
	private Button exploreButton;

	private Category category;
	private Runnable onCategorySelected;

	/**
	 * Initialize the controller
	 */
	@FXML
	private void initialize() {
		// Any initialization if needed
	}

	/**
	 * Handle click on the explore button This method must be public and have @FXML
	 * annotation to be visible to the FXML loader
	 */
	@FXML
	public void handleExploreClick(ActionEvent event) {
		if (category != null) {
			System.out.println("Explore button clicked for category: " + category.getName());

			// Call the callback if set
			if (onCategorySelected != null) {
				System.out.println("Calling onCategorySelected callback");
				onCategorySelected.run();
			} else {
				System.out.println("ERROR: onCategorySelected callback is null!");
			}
		} else {
			System.out.println("ERROR: category is null!");
		}
	}

	/**
	 * Set the category for this tile
	 * 
	 * @param category The category to display
	 */
	public void setCategory(Category category) {
		this.category = category;
		categoryNameLabel.setText(category.getName());

		// If category name is in Hebrew, set the text direction
		if (containsHebrew(category.getName())) {
			categoryNameLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
			categoryBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		} else {
			categoryNameLabel.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
			categoryBox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
		}
	}

	/**
	 * Check if a string contains Hebrew characters
	 * 
	 * @param text The text to check
	 * @return True if the text contains Hebrew characters
	 */
	private boolean containsHebrew(String text) {
		return text.codePoints().anyMatch(c -> Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HEBREW);
	}

	/**
	 * Set a callback for when this category is selected
	 * 
	 * @param callback The callback to run when the category is selected
	 */
	public void setOnCategorySelected(Runnable callback) {
		this.onCategorySelected = callback;
	}

	/**
	 * Get the root node
	 * 
	 * @return The root VBox
	 */
	public VBox getRoot() {
		return categoryBox;
	}
}