package com.lingotower.ui.views;

import com.lingotower.model.Category;
import com.lingotower.model.User;
import com.lingotower.ui.controllers.WordLearningController;

import javafx.fxml.FXMLLoader;

public class WordLearningView extends AbstractFormView {

	private Category category;
	private Runnable onBackToDashboard;
	private User currentUser;

	public WordLearningView(Category category, Runnable onBackToDashboard, User currentUser) {
		super("/fxml/WordLearning.fxml");
		this.category = category;
		this.onBackToDashboard = onBackToDashboard;
		this.currentUser = currentUser;
	}

	@Override
	protected void initializeController(FXMLLoader loader) {
		WordLearningController controller = loader.getController();
		if (controller != null) {
			// Set user first
			if (currentUser != null) {
				controller.setUser(currentUser);
			}

			// Set category and callbacks
			controller.setCategory(category);
			controller.setOnBackToDashboard(onBackToDashboard);
		}
	}

	@Override
	public WordLearningController getController() {
		return (WordLearningController) super.getController();
	}
}
