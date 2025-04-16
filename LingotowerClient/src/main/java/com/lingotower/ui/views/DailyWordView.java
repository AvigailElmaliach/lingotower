package com.lingotower.ui.views;

import com.lingotower.model.User;
import com.lingotower.ui.controllers.DailyWordController;

import javafx.fxml.FXMLLoader;

public class DailyWordView extends AbstractFormView {

	private Runnable onBackToDashboard;
	private User currentUser;

	public DailyWordView(User user, Runnable onBackToDashboard) {
		super("/fxml/DailyWordView.fxml");
		this.currentUser = user;
		this.onBackToDashboard = onBackToDashboard;
	}

	@Override
	protected void initializeController(FXMLLoader loader) {
		DailyWordController controller = loader.getController();
		if (controller != null) {
			// Set the user to the controller
			if (currentUser != null) {
				controller.setUser(currentUser);
			}
		}
	}

	@Override
	public DailyWordController getController() {
		return (DailyWordController) super.getController();
	}
}