package com.lingotower.ui.views;

import com.lingotower.ui.controllers.QuizController;

public class QuizView extends AbstractContentView {

	public QuizView() {
		super("/fxml/Quiz.fxml");
	}

	@Override
	protected void initializeController() {
		// No initialization needed
	}

	@Override
	public void refresh() {
		QuizController controller = getController();
		if (controller != null) {
			controller.refresh();
		}
	}

	@Override
	public QuizController getController() {
		return (QuizController) super.getController();
	}
}