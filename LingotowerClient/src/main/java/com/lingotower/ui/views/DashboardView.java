package com.lingotower.ui.views;

import java.io.IOException;

import com.lingotower.ui.controllers.DashboardViewController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

public class DashboardView implements View {
	private Parent root;
	private DashboardViewController controller;

	@Override
	public Parent createView() {
		if (root != null) {
			return root;
		}

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DashboardView.fxml"));
			root = loader.load();
			controller = loader.getController();
			return root;
		} catch (IOException e) {
			e.printStackTrace();
			return new Label("Error loading dashboard view: " + e.getMessage());
		}
	}

	@Override
	public void refresh() {
		if (controller != null) {
			controller.loadCategories();
		}
	}

	public void setRoot(Parent root) {
		this.root = root;
	}

	public void setController(DashboardViewController controller) {
		this.controller = controller;
	}

	public DashboardViewController getController() {
		return controller;
	}
}