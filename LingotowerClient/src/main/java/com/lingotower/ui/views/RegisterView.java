package com.lingotower.ui.views;

import java.util.function.Consumer;

import com.lingotower.model.User;
import com.lingotower.ui.controllers.RegisterViewController;

import javafx.fxml.FXMLLoader;

public class RegisterView extends AbstractFormView {

	private Consumer<User> onRegisterSuccess;
	private Runnable onSwitchToLogin;

	public RegisterView(Consumer<User> onRegisterSuccess, Runnable onSwitchToLogin) {
		super("/fxml/RegisterView.fxml");
		this.onRegisterSuccess = onRegisterSuccess;
		this.onSwitchToLogin = onSwitchToLogin;
	}

	@Override
	protected void initializeController(FXMLLoader loader) {
		RegisterViewController controller = loader.getController();
		if (controller != null) {
			controller.setCallbacks(onRegisterSuccess, onSwitchToLogin);
		}
	}
}