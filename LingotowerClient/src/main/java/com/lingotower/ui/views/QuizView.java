package com.lingotower.ui.views;

import java.io.IOException;

import com.lingotower.ui.controllers.QuizController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

public class QuizView implements View {

	private FXMLLoader loader;
	private Parent root;
	private QuizController controller;

	public QuizView() {
		initializeLoader();
	}

	private void initializeLoader() {
		loader = new FXMLLoader(getClass().getResource("/fxml/Quiz.fxml"));
	}

	@Override
	public Parent createView() {
		try {
			if (root == null) {
				root = loader.load();
				controller = loader.getController();
			}
			return root;
		} catch (IOException e) {
			e.printStackTrace();
			// Create a simple error view as fallback
			return new Label("Error loading quiz view: " + e.getMessage());
		}
	}

	@Override
	public void refresh() {
		if (controller != null) {
			controller.refresh();
		}
	}


	public QuizController getController() {
		return controller;
	}
}