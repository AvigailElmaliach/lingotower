package com.lingotower.ui.views;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

/**
 * Base class for form-based views that load from FXML.
 */
public abstract class AbstractFormView implements View {

	protected FXMLLoader loader;
	protected String fxmlPath;

	/**
	 * Constructs a form view with the given FXML path.
	 * 
	 * @param fxmlPath Path to the FXML file
	 */
	protected AbstractFormView(String fxmlPath) {
		this.fxmlPath = fxmlPath;
		initializeLoader();
	}

	/**
	 * Initializes or reinitializes the FXML loader.
	 */
	protected void initializeLoader() {
		loader = new FXMLLoader(getClass().getResource(fxmlPath));
	}

	@Override
	public Parent createView() {
		try {
			// Load a fresh copy of the FXML
			Parent view = loader.load();
			// Initialize the controller with any callbacks
			initializeController(loader);
			return view;
		} catch (IOException e) {
			e.printStackTrace();
			return createErrorLabel("Error loading view: " + e.getMessage());
		} finally {
			// Create a new loader for next time
			initializeLoader();
		}
	}

	/**
	 * Initialize the controller with callbacks and other necessary data.
	 * 
	 * @param loader The FXMLLoader containing the controller
	 */
	protected abstract void initializeController(FXMLLoader loader);

	/**
	 * Creates an error label as a fallback in case of loading failure.
	 * 
	 * @param message The error message
	 * @return A label with the error message
	 */
	protected Label createErrorLabel(String message) {
		return new Label(message);
	}

	@Override
	public void refresh() {
		// Default implementation does nothing
	}

	/**
	 * Gets the controller from the loader.
	 * 
	 * @return The controller instance or null if not loaded
	 */
	public Object getController() {
		return loader != null ? loader.getController() : null;
	}
}