package com.lingotower.ui.views;

import java.io.IOException;
import java.util.List;

import com.lingotower.model.Category;
import com.lingotower.service.CategoryService;
import com.lingotower.ui.controllers.DashboardViewController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

public class DashboardView implements View {
	private DashboardViewController controller;
	private CategoryService categoryService;

	public DashboardView() {
		this.categoryService = new CategoryService();
	}

	@Override
	public Parent createView() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DashboardView.fxml"));
			Parent view = loader.load();

			// Get the controller
			controller = loader.getController();

			// Load categories
			refreshCategories();

			return view;
		} catch (IOException e) {
			e.printStackTrace();
			return new Label("Error loading dashboard view: " + e.getMessage());
		}
	}

	@Override
	public void refresh() {
		refreshCategories();
	}

	private void refreshCategories() {
		if (controller != null) {
			try {
				List<Category> categories = categoryService.getAllCategories();
				controller.updateCategories(categories);
			} catch (Exception e) {
				controller.showErrorMessage("Error loading categories: " + e.getMessage());
			}
		}
	}

	public void updateCategories(List<Category> categories) {
		if (controller != null) {
			controller.updateCategories(categories);
		}
	}
}