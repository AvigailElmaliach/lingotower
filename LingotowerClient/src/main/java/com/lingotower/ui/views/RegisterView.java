package com.lingotower.ui.views;

import java.io.IOException;
import java.util.function.Consumer;

import com.lingotower.model.User;
import com.lingotower.ui.controllers.RegisterViewController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class RegisterView implements View {

	private FXMLLoader loader;
	private Consumer<User> onRegisterSuccess;
	private Runnable onSwitchToLogin;

	public RegisterView(Consumer<User> onRegisterSuccess, Runnable onSwitchToLogin) {
		this.onRegisterSuccess = onRegisterSuccess;
		this.onSwitchToLogin = onSwitchToLogin;
		initializeLoader();
	}

	private void initializeLoader() {
		loader = new FXMLLoader(getClass().getResource("/fxml/RegisterView.fxml"));
	}

	@Override
	public Parent createView() {
		try {
			// Load a fresh copy of the FXML each time getView is called
			Parent view = loader.load();
			RegisterViewController controller = loader.getController();

			// Set the callbacks on the freshly loaded controller
			controller.setCallbacks(onRegisterSuccess, onSwitchToLogin);

			return view;
		} catch (IOException e) {
			e.printStackTrace();
			// Fallback to create a simple error view
			javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
					"Error loading register view: " + e.getMessage());
			return errorLabel;
		} finally {
			// Create a new loader for next time
			initializeLoader();
		}
	}

	@Override
	public void refresh() {
		// No need to do anything here as we'll create a fresh view each time
	}
}