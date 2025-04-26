package com.lingotower.ui.views.admin;

import com.lingotower.model.Admin;
import com.lingotower.service.AdminService;
import com.lingotower.ui.controllers.admin.user.UserManagementController;
import com.lingotower.ui.views.AbstractContentView;

/**
 * View class for the User Management screen in admin panel.
 */
public class UserManagementView extends AbstractContentView {

	private Admin currentAdmin;
	private Runnable returnToDashboard;
	private AdminService adminService;

	/**
	 * Constructs a new UserManagementView.
	 */
	public UserManagementView() {
		super("/admin/UserManagementView.fxml");
	}

	@Override
	protected void initializeController() {
		UserManagementController controller = getController();
		if (controller != null) {
			// Set admin if available
			if (currentAdmin != null) {
				controller.setAdmin(currentAdmin);
			}

			// Set return callback if available
			if (returnToDashboard != null) {
				controller.setReturnToDashboard(returnToDashboard);
			}

			// Set admin service if available
			if (adminService != null) {
				controller.setAdminService(adminService);
			}
		}
	}

	/**
	 * Sets the admin for this view.
	 * 
	 * @param admin The admin to set
	 * @return This view instance for method chaining
	 */
	public UserManagementView setAdmin(Admin admin) {
		this.currentAdmin = admin;
		UserManagementController controller = getController();
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
	public UserManagementView setReturnToDashboard(Runnable callback) {
		this.returnToDashboard = callback;
		UserManagementController controller = getController();
		if (controller != null) {
			controller.setReturnToDashboard(callback);
		}
		return this;
	}

	/**
	 * Sets the admin service.
	 * 
	 * @param adminService The admin service to use
	 * @return This view instance for method chaining
	 */
	public UserManagementView setAdminService(AdminService adminService) {
		this.adminService = adminService;
		UserManagementController controller = getController();
		if (controller != null) {
			controller.setAdminService(adminService);
		}
		return this;
	}

	/**
	 * Loads the users from the server.
	 */
	public void loadUsers() {
		UserManagementController controller = getController();
		if (controller != null) {
			controller.loadUsers();
		}
	}

	@Override
	public void refresh() {
		UserManagementController controller = getController();
		if (controller != null) {
			controller.loadUsers();
		}
	}

	@Override
	public UserManagementController getController() {
		return (UserManagementController) super.getController();
	}
}