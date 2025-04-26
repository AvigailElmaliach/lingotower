package com.lingotower.ui.controllers.admin.user;

import java.util.List;

import org.slf4j.Logger;

import com.lingotower.model.User;
import com.lingotower.security.TokenStorage;
import com.lingotower.service.AdminService;
import com.lingotower.service.UserService;
import com.lingotower.utils.LoggingUtility;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Delegate class for user loading and searching functionality
 */
public class UserLoaderDelegate {
	private static final Logger logger = LoggingUtility.getLogger(UserLoaderDelegate.class);

	private final UserManagementController controller;
	private AdminService adminService;
	private UserService userService;
	private ObservableList<User> usersList = FXCollections.observableArrayList();

	public UserLoaderDelegate(UserManagementController controller) {
		this.controller = controller;
		this.userService = new UserService();
	}

	/**
	 * Sets the admin service
	 */
	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
		logger.debug("AdminService set in UserLoaderDelegate");
		TokenStorage.logTokenStatus("After setting AdminService");
	}

	/**
	 * Loads users in background thread
	 */
	public void loadUsers() {
		controller.getStatusLabel().setText("Loading users...");
		controller.getStatusLabel().setVisible(true);
		controller.getStatusLabel().getStyleClass().remove("error-message");

		long startTime = System.currentTimeMillis();

		Thread loadThread = new Thread(() -> loadUsersInBackground(startTime));
		loadThread.setDaemon(true);
		loadThread.setName("UserLoader");
		loadThread.start();
	}

	/**
	 * Handle search functionality
	 */
	public void handleSearch(String searchText) {
		if (searchText == null)
			return;

		searchText = searchText.trim().toLowerCase();
		logger.debug("Searching for users with text: '{}'", searchText);

		// If search is empty, show all users
		if (searchText.isEmpty()) {
			controller.getUserTableView().setItems(usersList);
			return;
		}

		filterAndDisplayUsers(searchText);
	}

	/**
	 * Gets the user list
	 */
	public ObservableList<User> getUsersList() {
		return usersList;
	}

	/**
	 * Background implementation for loading users
	 */
	private void loadUsersInBackground(long startTime) {
		try {
			logger.info("Loading users in background thread...");
			TokenStorage.logTokenStatus("Before loading users");

			List<User> users = fetchUsersFromService();
			logger.info("Users loaded: {}", users != null ? users.size() : "null");

			updateUiWithUsers(users, startTime);
		} catch (Exception e) {
			handleUserLoadingError(e, startTime);
		}
	}

	/**
	 * Fetch users with fallback strategy
	 */
	private List<User> fetchUsersFromService() {
		List<User> users;

		// Try to load users with AdminService first, then fallback to UserService
		if (adminService != null) {
			users = adminService.getAllUsers();
		} else {
			if (userService == null) {
				userService = new UserService();
			}
			users = userService.getAllUsers();
		}

		return users;
	}

	/**
	 * Update UI with loaded users
	 */
	private void updateUiWithUsers(List<User> users, long startTime) {
		Platform.runLater(() -> {
			// Clear current list
			usersList.clear();

			if (users != null && !users.isEmpty()) {
				usersList.addAll(users);
				controller.getUserTableView().setItems(usersList);
				new UserUIDelegate(controller).showStatusMessage("Loaded " + users.size() + " users", false);
			} else {
				new UserUIDelegate(controller).showStatusMessage("No users found", true);
			}

			long duration = System.currentTimeMillis() - startTime;
			LoggingUtility.logPerformance(logger, "load_users", duration, "success");
			LoggingUtility.logAction(logger, "load",
					controller.getCurrentAdmin() != null ? controller.getCurrentAdmin().getUsername() : "system",
					"users", "success");
		});
	}

	/**
	 * Handle user loading errors
	 */
	private void handleUserLoadingError(Exception e, long startTime) {
		logger.error("Error loading users: {}", e.getMessage(), e);

		Platform.runLater(() -> {
			new UserUIDelegate(controller).showStatusMessage("Error loading users: " + e.getMessage(), true);

			long duration = System.currentTimeMillis() - startTime;
			LoggingUtility.logPerformance(logger, "load_users", duration, "failed");
			LoggingUtility.logAction(logger, "load",
					controller.getCurrentAdmin() != null ? controller.getCurrentAdmin().getUsername() : "system",
					"users", "error: " + e.getMessage());
		});
	}

	/**
	 * Filter users based on search text
	 */
	private void filterAndDisplayUsers(String searchText) {
		ObservableList<User> filteredList = FXCollections.observableArrayList();

		for (User user : usersList) {
			if (user.getUsername().toLowerCase().contains(searchText)
					|| (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchText))) {
				filteredList.add(user);
			}
		}

		// Update the table view with filtered results
		controller.getUserTableView().setItems(filteredList);
		new UserUIDelegate(controller).showStatusMessage("Found " + filteredList.size() + " matching users", false);
		logger.info("Search completed: found {} matching users", filteredList.size());
	}
}
