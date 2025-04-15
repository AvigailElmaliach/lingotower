
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
		// User profile functionality would be implemented later
		System.out.println("User profile functionality not yet implemented");
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
			// Create daily word view with current user
			DailyWordView dailyWordView = new DailyWordView(currentUser, this::showDashboard);
			Parent dailyWordRoot = dailyWordView.createView();

			// Set mainLayout center to daily word view
			mainLayout.setCenter(dailyWordRoot);
		} catch (Exception e) {
			System.err.println("Error showing daily word view: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void showQuiz() {
		try {
			// Create quiz view
			QuizView quizView = new QuizView();
			Parent quizRoot = quizView.createView();

			// Set mainLayout center to quiz view
			mainLayout.setCenter(quizRoot);
		} catch (Exception e) {
			System.err.println("Error showing quiz view: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void showUserProfile() {
		try {
			UserProfileView profileView = new UserProfileView();
			profileView.setUser(currentUser);
			Parent view = profileView.createView();
			mainLayout.setCenter(view);
		} catch (Exception e) {
			System.err.println("Error showing user profile: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void showDashboard() {
		try {
			// Create dashboard view with category selection callback
			if (dashboardView == null) {
				dashboardView = new DashboardView();
			}

			Parent dashboardRoot = dashboardView.createView();

			// Get the controller to set up category selection
			DashboardViewController controller = null;
			// You'd need to get the controller from the dashboardView

			// Set mainLayout center to dashboard
			mainLayout.setCenter(dashboardRoot);
		} catch (Exception e) {
			System.err.println("Error showing dashboard: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void showWordLearningForCategory(Category category) {
		System.out.println("MainApplicationController.showWordLearningForCategory() called for: " + category.getName());
		try {
			// Create word learning view with the current user
			System.out.println("Creating WordLearningView");
			WordLearningView wordLearningView = new WordLearningView(category, this::showDashboard, // Go back to
																									// dashboard when
																									// finished
					this.currentUser // Pass the current user
			);

			System.out.println("Setting main layout center to word learning view");
			// Set mainLayout center to word learning view
			mainLayout.setCenter(wordLearningView.createView());
			System.out.println("Word learning view should now be displayed");
		} catch (Exception e) {
			System.err.println("Error showing word learning view: " + e.getMessage());
			e.printStackTrace();
		}
	}
}