package com.lingotower.ui.views;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

/**
 * Base class for content views that manage state.
 */
public abstract class AbstractContentView implements View {

	protected Parent root;
	protected FXMLLoader loader;
	protected String fxmlPath;

	protected AbstractContentView(String fxmlPath) {
		this.fxmlPath = fxmlPath;
		this.loader = new FXMLLoader(getClass().getResource(fxmlPath));
	}

	@Override
	public Parent createView() {
		try {
			if (root == null) {
				root = loader.load();
				initializeController();
			}
			return root;
		} catch (IOException e) {
			e.printStackTrace();
			return new Label("Error loading view: " + e.getMessage());
		}
	}

	/**
	 * Abstract method to initialize the controller with any necessary data.
	 */
	protected abstract void initializeController();

	/**
	 * Gets the controller from the loader.
	 * 
	 * @return The controller instance or null if not loaded
	 */
	public Object getController() {
		return loader != null ? loader.getController() : null;
	}
}