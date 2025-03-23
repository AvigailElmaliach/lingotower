package com.lingotower.ui.views;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

public class DashboardView implements View {

	@Override
	public Parent createView() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DashboardView.fxml"));
			return loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return new Label("Error loading dashboard view: " + e.getMessage());
		}
	}

	@Override
	public void refresh() {
		// Refresh logic if needed
	}
}