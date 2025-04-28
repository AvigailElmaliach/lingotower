package com.lingotower.ui.controllers.admin.admin;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingotower.dto.admin.AdminResponseDTO;
import com.lingotower.dto.admin.AdminUpdateDTO;
import com.lingotower.model.Admin;
import com.lingotower.security.TokenStorage;
import com.lingotower.service.AdminService;
import com.lingotower.utils.LoggingUtility;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Repository class to handle CRUD operations for Admin entities. Separates data
 * access logic from the controller.
 */
public class AdminRepository {
	private static final Logger logger = LoggingUtility.getLogger(AdminRepository.class);

	private final AdminService adminService;
	private final ObservableList<Admin> adminsList = FXCollections.observableArrayList();

	/**
	 * Constructor initializing with the AdminService.
	 */
	public AdminRepository() {
		this.adminService = new AdminService();
		logger.debug("AdminRepository initialized");
	}

	/**
	 * Gets the observable list of admins.
	 * 
	 * @return The observable list that can be bound to UI elements
	 */
	public ObservableList<Admin> getAdminsList() {
		return adminsList;
	}

	/**
	 * Loads all admins asynchronously.
	 * 
	 * @param onSuccess Callback when admins are loaded successfully
	 * @param onError   Callback when an error occurs
	 */
	public void loadAdmins(Runnable onSuccess, StatusMessageCallback onError) {
		long startTime = System.currentTimeMillis();

		// Create a background thread to load admins
		Thread loadThread = new Thread(() -> {
			try {
				logger.info("Loading admins in background thread...");
				TokenStorage.logTokenStatus("Before loading admins");

				// Get all admins through the service
				List<Admin> admins = adminService.getAllAdmins();
				logger.info("Admins loaded: {}", (admins != null ? admins.size() : "null"));

				// Update UI on JavaFX thread
				Platform.runLater(() -> {
					// Clear current list
					adminsList.clear();

					if (admins != null && !admins.isEmpty()) {
						adminsList.addAll(admins);
						if (onSuccess != null) {
							onSuccess.run();
						}
					} else {
						if (onError != null) {
							onError.onStatusMessage("No admins found", true);
						}
					}
				});

				long duration = System.currentTimeMillis() - startTime;
				LoggingUtility.logPerformance(logger, "load_admins", duration,
						"count: " + (admins != null ? admins.size() : 0));

			} catch (Exception e) {
				logger.error("Error loading admins: {}", e.getMessage(), e);

				long duration = System.currentTimeMillis() - startTime;
				LoggingUtility.logPerformance(logger, "load_admins", duration, "error: " + e.getMessage());

				// Show error on JavaFX thread
				Platform.runLater(() -> {
					if (onError != null) {
						onError.onStatusMessage("Error loading admins: " + e.getMessage(), true);
					}
				});
			}
		});

		// Start the background thread
		loadThread.setDaemon(true);
		loadThread.setName("AdminLoader");
		loadThread.start();
	}

	/**
	 * Creates a new admin.
	 *
	 * @param newAdmin  The admin to create
	 * @param onSuccess Callback when creation is successful
	 * @param onError   Callback when an error occurs
	 */
	public void createAdmin(Admin newAdmin, AdminCallback onSuccess, StatusMessageCallback onError) {
		if (newAdmin == null) {
			logger.error("Cannot create null admin");
			if (onError != null) {
				onError.onStatusMessage("Invalid admin data: Admin is null", true);
			}
			return;
		}

		long startTime = System.currentTimeMillis();
		Thread createThread = new Thread(() -> {
			try {
				logger.info("Creating new admin: {}", newAdmin.getUsername());
				LoggingUtility.logAction(logger, "add_admin", "system", "admin:" + newAdmin.getUsername(),
						"processing");

				// Save using service and get potential error message
				String errorMessage = adminService.createAdmin(newAdmin);
				long duration = System.currentTimeMillis() - startTime;

				Platform.runLater(() -> {
					if (errorMessage == null) {
						logger.info("Admin created successfully");
						LoggingUtility.logAction(logger, "add_admin", "system", "admin:" + newAdmin.getUsername(),
								"success");
						LoggingUtility.logPerformance(logger, "create_admin", duration, "success");

						adminsList.add(newAdmin);

						if (onSuccess != null) {
							onSuccess.onAdminOperation(newAdmin);
						}
					} else {
						logger.warn("Failed to create admin: service returned error - {}", errorMessage);
						LoggingUtility.logAction(logger, "add_admin", "system", "admin:" + newAdmin.getUsername(),
								"failed: " + errorMessage);
						LoggingUtility.logPerformance(logger, "create_admin", duration, "failed");

						if (onError != null) {
							onError.onStatusMessage("Failed to create admin: " + errorMessage, true);
						}
					}
				});
			} catch (Exception e) {
				logger.error("Error creating admin: {}", e.getMessage(), e);
				LoggingUtility.logAction(logger, "add_admin", "system", "admin:" + newAdmin.getUsername(),
						"error: " + e.getMessage());

				long duration = System.currentTimeMillis() - startTime;
				LoggingUtility.logPerformance(logger, "create_admin", duration, "error");

				Platform.runLater(() -> {
					if (onError != null) {
						onError.onStatusMessage("Error creating admin: " + e.getMessage(), true);
					}
				});
			}
		});

		createThread.setDaemon(true);
		createThread.setName("AdminCreator");
		createThread.start();
	}

	/**
	 * Updates an existing admin on the server asynchronously and handles the UI
	 * update through a callback. Provides specific error messages based on the
	 * server response.
	 *
	 * @param id             The ID of the admin to update.
	 * @param adminUpdateDTO DTO containing the updated admin details.
	 * @param onSuccess      Runnable to execute on successful update.
	 * @param onError        Callback to handle and display status messages
	 *                       (including errors).
	 */
	public void updateAdmin(Long id, AdminUpdateDTO adminUpdateDTO, Runnable onSuccess, StatusMessageCallback onError) {
		Thread updateThread = new Thread(() -> {
			try {
				// Call the admin update service
				AdminResponseDTO response = adminService.updateAdmin(id, adminUpdateDTO);
				Platform.runLater(() -> {
					if (response != null) {
						if (onSuccess != null) {
							onSuccess.run();
						}
					} else {
						if (onError != null) {
							onError.onStatusMessage("Failed to update admin", true); // Generic failure message
						}
					}
				});
			} catch (RestClientException e) {
				String errorMessage = "Failed to update admin";
				String detailedErrorMessage = e.getMessage();
				if (detailedErrorMessage != null && !detailedErrorMessage.isEmpty()) {
					errorMessage = detailedErrorMessage;
					if (errorMessage.contains("Incorrect current password")) {
						errorMessage = "Incorrect current password";
					} else if (errorMessage.contains("Admin not found")) {
						errorMessage = "Admin not found";
					}
				}
				String finalErrorMessage = errorMessage;
				Platform.runLater(() -> onError.onStatusMessage(finalErrorMessage, true));
			} catch (Exception e) {
				Platform.runLater(() -> onError.onStatusMessage("Error updating admin: " + e.getMessage(), true));
			}
		});
		updateThread.setDaemon(true);
		updateThread.setName("AdminUpdater");
		updateThread.start();
	}

	/**
	 * Deletes an admin.
	 * 
	 * @param admin     The admin to delete
	 * @param onSuccess Callback when deletion is successful
	 * @param onError   Callback when an error occurs
	 */
	public void deleteAdmin(Admin admin, Runnable onSuccess, StatusMessageCallback onError) {
		if (admin == null || admin.getId() == null) {
			logger.error("Cannot delete null admin or admin without ID");
			if (onError != null) {
				onError.onStatusMessage("Invalid admin data for deletion", true);
			}
			return;
		}

		long startTime = System.currentTimeMillis();

		Thread deleteThread = new Thread(() -> {
			try {
				logger.info("Deleting admin with ID: {}, username: {}", admin.getId(), admin.getUsername());
				LoggingUtility.logAction(logger, "delete_admin", "system", "admin:" + admin.getUsername(),
						"processing");

				// Delete using service
				boolean success = adminService.deleteAdmin(admin.getId());
				long duration = System.currentTimeMillis() - startTime;

				Platform.runLater(() -> {
					if (success) {
						logger.info("Admin deleted successfully");
						LoggingUtility.logAction(logger, "delete_admin", "system", "admin:" + admin.getUsername(),
								"success");
						LoggingUtility.logPerformance(logger, "delete_admin", duration, "success");

						// Remove from the list
						adminsList.remove(admin);

						if (onSuccess != null) {
							onSuccess.run();
						}
					} else {
						logger.warn("Failed to delete admin: service returned false");
						LoggingUtility.logAction(logger, "delete_admin", "system", "admin:" + admin.getUsername(),
								"failed");
						LoggingUtility.logPerformance(logger, "delete_admin", duration, "failed");

						if (onError != null) {
							onError.onStatusMessage("Failed to delete admin. Please check permissions and try again.",
									true);
						}
					}
				});
			} catch (Exception e) {
				logger.error("Error deleting admin: {}", e.getMessage(), e);
				LoggingUtility.logAction(logger, "delete_admin", "system", "admin:" + admin.getUsername(),
						"error: " + e.getMessage());

				long duration = System.currentTimeMillis() - startTime;
				LoggingUtility.logPerformance(logger, "delete_admin", duration, "error");

				Platform.runLater(() -> {
					if (onError != null) {
						onError.onStatusMessage("Error deleting admin: " + e.getMessage(), true);
					}
				});
			}
		});

		deleteThread.setDaemon(true);
		deleteThread.setName("AdminDeleter");
		deleteThread.start();
	}

	/**
	 * Filters the admin list by search text.
	 * 
	 * @param searchText The text to search for in username or email
	 * @return The filtered list of admins
	 */
	public ObservableList<Admin> filterAdmins(String searchText) {
		if (searchText == null || searchText.trim().isEmpty()) {
			return adminsList;
		}

		String lowerCaseSearchText = searchText.trim().toLowerCase();
		ObservableList<Admin> filteredList = FXCollections.observableArrayList();

		for (Admin admin : adminsList) {
			if (admin.getUsername().toLowerCase().contains(lowerCaseSearchText)
					|| (admin.getEmail() != null && admin.getEmail().toLowerCase().contains(lowerCaseSearchText))) {
				filteredList.add(admin);
			}
		}

		return filteredList;
	}

	/**
	 * Finds the index of an admin in the list by ID.
	 * 
	 * @param id The admin ID to find
	 * @return The index or -1 if not found
	 */
	private int findAdminIndexById(Long id) {
		if (id == null)
			return -1;

		for (int i = 0; i < adminsList.size(); i++) {
			Admin admin = adminsList.get(i);
			if (admin != null && admin.getId() != null && admin.getId().equals(id)) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Callback interface for status messages.
	 */
	public interface StatusMessageCallback {
		void onStatusMessage(String message, boolean isError);
	}

	/**
	 * Callback interface for admin operations.
	 */
	public interface AdminCallback {
		void onAdminOperation(Admin admin);
	}
}