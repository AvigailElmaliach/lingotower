package com.lingotower.ui.controllers;

import com.lingotower.model.Category;

import javafx.fxml.FXML;
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
    
    /**
     * Initialize the controller
     */
    @FXML
    private void initialize() {
        // Set up action
        exploreButton.setOnAction(e -> handleExploreClick());
    }
    
    /**
     * Set the category for this tile
     * 
     * @param category The category to display
     */
    public void setCategory(Category category) {
        this.category = category;
        categoryNameLabel.setText(category.getName());
    }
    
    /**
     * Handle click on the explore button
     */
    private void handleExploreClick() {
        if (category != null) {
            System.out.println("Exploring category: " + category.getName());
            // TODO: Navigate to category details or implement callback
        }
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