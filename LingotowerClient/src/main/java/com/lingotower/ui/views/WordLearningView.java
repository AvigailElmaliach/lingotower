package com.lingotower.ui.views;

import java.io.IOException;

import com.lingotower.model.Category;
import com.lingotower.ui.controllers.WordLearningController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

public class WordLearningView implements View {

	private Category category;
	private Runnable onBackToDashboard;

	public WordLearningView(Category category, Runnable onBackToDashboard) {
		this.category = category;
		this.onBackToDashboard = onBackToDashboard;
	}

	@Override
	public Parent createView() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WordLearning.fxml"));
			Parent view = loader.load();

			WordLearningController controller = loader.getController();
			controller.setCategory(category);
			controller.setOnBackToDashboard(onBackToDashboard);

			return view;
		} catch (IOException e) {
			e.printStackTrace();
			return new Label("Error loading word learning view: " + e.getMessage());
		}
	}

	@Override
	public void refresh() {
		// Nothing to do here
	}
}