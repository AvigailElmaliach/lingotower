
package com.lingotower.ui.controllers;

import com.lingotower.model.Category;
import com.lingotower.model.User;
import com.lingotower.ui.views.DailyWordView;
import com.lingotower.ui.views.DashboardView;
import com.lingotower.ui.views.QuizView;
import com.lingotower.ui.views.UserProfileView;
import com.lingotower.ui.views.WordLearningView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;

public class MainApplicationController {

	@FXML
	private BorderPane mainLayout;

	private User currentUser;
	private DashboardView dashboardView;
	private Runnable onLogout;

	public void setUser(User user) {
		this.currentUser = user;
	}

	public void setDashboardView(DashboardView dashboardView) {
		this.dashboardView = dashboardView;
	}

	public void setOnLogout(Runnable onLogout) {
		this.onLogout = onLogout;
	}

	public void initialize() {
		// Show dashboard initially
		if (dashboardView != null) {
			showDashboard();
		}
	}

	@FXML
	private void handleDailyWordButtonClick(ActionEvent event) {
		showDailyWord();
	}

	@FXML
	private void handleHomeBtnClick(ActionEvent event) {
		showDashboard();
	}

	@FXML
	private void handleQuizBtnClick(ActionEvent event) {
		showQuiz();
	}

	@FXML
	private void handleProfileBtnClick(ActionEvent event) {
		showUserProfile();
	}

	@FXML
	private void handleTranslatorBtnClick(ActionEvent event) {
		// Open the translator dialog
		new com.lingotower.ui.components.TranslatorDialog(mainLayout.getScene().getWindow()).show();
	}

	@FXML
	private void handleLogoutBtnClick(ActionEvent event) {
		if (onLogout != null) {
			onLogout.run();
		}
	}

	public void showDailyWord() {
		try {

			DailyWordView dailyWordView = new DailyWordView(currentUser, this::showDashboard);
			Parent dailyWordRoot = dailyWordView.createView();
			mainLayout.setCenter(dailyWordRoot);
		} catch (Exception e) {
			handleError("Error showing daily word view", e);
		}
	}

	public void showQuiz() {
		try {
			QuizView quizView = new QuizView();
			Parent quizRoot = quizView.createView();
			mainLayout.setCenter(quizRoot);
		} catch (Exception e) {
			handleError("Error showing quiz view", e);
		}
	}

	public void showUserProfile() {
		try {
			UserProfileView profileView = new UserProfileView();
			profileView.setUser(currentUser);
			Parent view = profileView.createView();
			mainLayout.setCenter(view);
		} catch (Exception e) {
			handleError("Error showing user profile", e);
		}
	}

	public void showDashboard() {
		try {
			if (dashboardView == null) {
				dashboardView = new DashboardView();
			}
			// for show all categories
			dashboardView.setMainController(this);

			Parent dashboardRoot = dashboardView.createView();
			mainLayout.setCenter(dashboardRoot);

			// if the user changes the target language it will reload
			if (dashboardView.getController() != null) {
				dashboardView.getController().loadCategories();
			}
		} catch (Exception e) {
			handleError("Error showing dashboard", e);
		}
	}

	public void showWordLearningForCategory(Category category) {
		try {
			WordLearningView wordLearningView = new WordLearningView(category, this::showDashboard, currentUser);
			mainLayout.setCenter(wordLearningView.createView());
		} catch (Exception e) {
			handleError("Error showing word learning view for category: " + category.getName(), e);
		}
	}

	private void handleError(String message, Exception e) {
		System.err.println(message + ": " + e.getMessage());
		e.printStackTrace();

		// Show an alert dialog to the user
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(message);
		alert.setContentText(e.getMessage());
		alert.showAndWait();
	}
}
