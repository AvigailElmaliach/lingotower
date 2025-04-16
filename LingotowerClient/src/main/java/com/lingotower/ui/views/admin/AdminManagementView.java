package com.lingotower.ui.views.admin;

import com.lingotower.model.Admin;
import com.lingotower.ui.controllers.admin.AdminManagementController;
import com.lingotower.ui.views.AbstractContentView;

/**
 * View class for the Admin Management screen in admin panel.
 */
public class AdminManagementView extends AbstractContentView {

	private Admin currentAdmin;
	private Runnable returnToDashboard;

	/**
	 * Constructs a new AdminManagementView.
	 */
	public AdminManagementView() {
		super("/admin/AdminManagementView.fxml");
	}

	@Override
	protected void initializeController() {
		AdminManagementController controller = getController();
		if (controller != null) {
			// Set this view as the parent view for the controller
			controller.setParentView(this);

			// Set admin if available
			if (currentAdmin != null) {
				controller.setAdmin(currentAdmin);
			}

			// Set return callback if available
			if (returnToDashboard != null) {
				controller.setReturnToDashboard(returnToDashboard);
			}
		}
	}

	/**
	 * Sets the admin for this view.
	 * 
	 * @param admin The admin to set
	 * @return This view instance for method chaining
	 */
	public AdminManagementView setAdmin(Admin admin) {
		this.currentAdmin = admin;
		AdminManagementController controller = getController();
		if (controller != null) {
			controller.setAdmin(admin);
		}
		return this;
	}

	/**
	 * Sets the return to dashboard callback.
	 * 
	 * @param callback The callback to run when returning to dashboard
	 * @return This view instance for method chaining
	 */
	public AdminManagementView setReturnToDashboard(Runnable callback) {
		this.returnToDashboard = callback;
		AdminManagementController controller = getController();
		if (controller != null) {
			controller.setReturnToDashboard(callback);
		}
		return this;
	}

	/**
	 * Loads the admins from the server.
	 */
	public void loadAdmins() {
		AdminManagementController controller = getController();
		if (controller != null) {
			controller.loadAdmins();
		}
	}

	@Override
	public void refresh() {
		AdminManagementController controller = getController();
		if (controller != null) {
			controller.loadAdmins();
		}
	}

	@Override
	public AdminManagementController getController() {
		return (AdminManagementController) super.getController();
	}
}