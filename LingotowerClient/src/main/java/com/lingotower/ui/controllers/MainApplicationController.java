package com.lingotower.ui.controllers;

import com.lingotower.model.User;
import com.lingotower.ui.views.DashboardView;
import com.lingotower.ui.views.View;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class MainApplicationController {

	@FXML
	private BorderPane mainLayout;

	private User currentUser;
	private View dashboardView;
	private View learnWordsView;
	private View quizView;
	private View userProfileView;

	private Runnable onLogout;

//	public void setUser(User user) {
//		this.currentUser = user;
//	}

//	public void setViews(DashboardView dashboardView, LearnWordsView learnWordsView, QuizView quizView,
//			UserProfileView userProfileView) {
//		this.dashboardView = dashboardView;
//		this.learnWordsView = learnWordsView;
//		this.quizView = quizView;
//		this.userProfileView = userProfileView;
//	}
	public void setViews(DashboardView dashboardView) {
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
	private void handleHomeBtnClick(ActionEvent event) {
		showDashboard();
	}

	@FXML
	private void handleLearnBtnClick(ActionEvent event) {
		mainLayout.setCenter(learnWordsView.createView());
	}

	@FXML
	private void handleQuizBtnClick(ActionEvent event) {
		mainLayout.setCenter(quizView.createView());
	}

	@FXML
	private void handleProfileBtnClick(ActionEvent event) {
		mainLayout.setCenter(userProfileView.createView());
	}

	@FXML
	private void handleLogoutBtnClick(ActionEvent event) {
		if (onLogout != null) {
			onLogout.run();
		}
	}

	private void showDashboard() {
		mainLayout.setCenter(dashboardView.createView());
	}
}