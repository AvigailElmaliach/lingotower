package com.lingotower.ui.views;

import java.util.function.Consumer;

import com.lingotower.model.User;
import com.lingotower.ui.controllers.LoginViewController;

import javafx.fxml.FXMLLoader;

public class LoginView extends AbstractFormView {

	private Consumer<User> onLoginSuccess;
	private Runnable onSwitchToRegister;

	public LoginView(Consumer<User> onLoginSuccess, Runnable onSwitchToRegister) {
		super("/fxml/LoginView.fxml");
		this.onLoginSuccess = onLoginSuccess;
		this.onSwitchToRegister = onSwitchToRegister;
	}

	@Override
	protected void initializeController(FXMLLoader loader) {
		LoginViewController controller = loader.getController();
		if (controller != null) {
			controller.setCallbacks(onLoginSuccess, onSwitchToRegister);
		}
	}
}