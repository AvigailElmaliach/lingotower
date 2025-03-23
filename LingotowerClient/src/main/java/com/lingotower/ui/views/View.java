package com.lingotower.ui.views;

import javafx.scene.Parent;

/**
 * Interface for all views in the LingoTower application
 */
public interface View {

	/**
	 * Returns the root node of this view
	 * 
	 * @return The root javafx node
	 */
	Parent createView();

	/**
	 * Refreshes the view content with updated data
	 */
	void refresh();
}