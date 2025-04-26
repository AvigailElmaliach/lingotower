package com.lingotower.ui.controllers.wordlearning;

import org.slf4j.Logger;

import com.lingotower.model.Category;
import com.lingotower.utils.HebrewUtils;
import com.lingotower.utils.LoggingUtility;

import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Manages navigation and category display
 */
public class NavigationManager {
    private static final Logger logger = LoggingUtility.getLogger(NavigationManager.class);
    
    // UI Components
    private final Button backButton;
    private final Label categoryNameLabel;
    
    // Navigation callback
    private Runnable onBackToDashboard;
    
    // Current category
    private Category currentCategory;
    
    public NavigationManager(Button backButton, Label categoryNameLabel) {
        this.backButton = backButton;
        this.categoryNameLabel = categoryNameLabel;
    }
    
    /**
     * Sets the callback for navigating back to dashboard
     */
    public void setOnBackToDashboard(Runnable callback) {
        this.onBackToDashboard = callback;
    }
    
    /**
     * Sets the current category and updates the UI
     */
    public void setCategory(Category category) {
        this.currentCategory = category;
        
        if (category != null) {
            categoryNameLabel.setText(category.getName());
            
            // Set RTL if needed
            if (HebrewUtils.containsHebrew(category.getName())) {
                categoryNameLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            } else {
                categoryNameLabel.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            }
        } else {
            categoryNameLabel.setText("No Category Selected");
        }
    }
    
    /**
     * Navigates back to the dashboard
     */
    public void navigateBackToDashboard() {
        if (onBackToDashboard != null) {
            logger.debug("Navigating back to dashboard");
            onBackToDashboard.run();
        } else {
            logger.warn("Navigation callback is not set");
        }
    }
    
    /**
     * Gets the current category
     */
    public Category getCurrentCategory() {
        return currentCategory;
    }
}