package com.lingotower.ui.views.admin;

import com.lingotower.model.Admin;
import com.lingotower.ui.controllers.admin.AdminViewController;
import com.lingotower.ui.views.AbstractContentView;

import javafx.stage.Stage;

/**
 * View class for the main Admin Dashboard screen.
 */
public class AdminView extends AbstractContentView {

	private Admin currentAdmin;
	private Runnable onLogout;
	private Stage primaryStage; // Store the primaryStage

	/**
	 * Constructs a new AdminView.
	 */
	public AdminView() {
		super("/admin/AdminView.fxml");
	}

	@Override
	protected void initializeController() {
		AdminViewController controller = getController();
		if (controller != null) {
			// Debug
			System.out.println("In AdminView.initializeController, setting primaryStage: "
					+ (primaryStage != null ? "Not null" : "NULL"));

			// Set primary stage first
			if (primaryStage != null) {
				controller.setPrimaryStage(primaryStage);
			} else {
				System.err.println("ERROR: primaryStage is null in AdminView.initializeController!");
			}

			// Set admin if available
			if (currentAdmin != null) {
				controller.setAdmin(currentAdmin);
			}

			// Set logout callback if available
			if (onLogout != null) {
				controller.setOnLogout(onLogout);
			}
		} else {
			System.err.println("ERROR: Controller is null in AdminView.initializeController!");
		}
	}

	/**
	 * Sets the admin for this view.
	 * 
	 * @param admin The admin to set
	 * @return This view instance for method chaining
	 */
	public AdminView setAdmin(Admin admin) {
		this.currentAdmin = admin;
		AdminViewController controller = getController();
		if (controller != null) {
			controller.setAdmin(admin);
		}
		return this;
	}

	/**
	 * Sets the stage for this view.
	 * 
	 * @param stage The primary stage
	 * @return This view instance for method chaining
	 */
	public AdminView setPrimaryStage(Stage stage) {
		// Store the stage
		this.primaryStage = stage;

		// Debug
		System.out.println("Setting primaryStage in AdminView: " + (stage != null ? "Not null" : "NULL"));

		// Set it on the controller if already created
		AdminViewController controller = getController();
		if (controller != null) {
			controller.setPrimaryStage(stage);
		}
		return this;
	}

	/**
	 * Sets the logout callback.
	 * 
	 * @param onLogout The callback to run on logout
	 * @return This view instance for method chaining
	 */
	public AdminView setOnLogout(Runnable onLogout) {
		this.onLogout = onLogout;
		AdminViewController controller = getController();
		if (controller != null) {
			controller.setOnLogout(onLogout);
		}
		return this;
	}

	@Override
	public void refresh() {
		// Refresh system stats
		AdminViewController controller = getController();
		if (controller != null) {
			// No direct refresh method exists, but we could call specific refresh methods
			// if they are added to the controller
		}
	}

	@Override
	public AdminViewController getController() {
		return (AdminViewController) super.getController();
	}
}