package com.lingotower.ui.views;

import java.io.IOException;
import java.util.function.Consumer;

import com.lingotower.model.User;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class LoginView implements View {

	private Parent view;
	private LoginViewController controller;

	public LoginView(Consumer<User> onLoginSuccess, Runnable onSwitchToRegister) {
		initializeView();

		// Set callbacks on the controller
		if (controller != null) {
			controller.setCallbacks(onLoginSuccess, onSwitchToRegister);
		}
	}

	private void initializeView() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
			view = loader.load();
			controller = loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
			// Fallback to create a simple error view
			javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
					"Error loading login view: " + e.getMessage());
			view = errorLabel;
		}
	}

	@Override
	public Parent getView() {
		return view;
	}

	@Override
	public void refresh() {
		if (controller != null) {
			controller.resetError();
		}
	}
}