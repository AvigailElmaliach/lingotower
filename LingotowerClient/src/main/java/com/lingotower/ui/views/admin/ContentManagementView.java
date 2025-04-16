package com.lingotower.ui.views.admin;

import com.lingotower.model.Admin;
import com.lingotower.ui.controllers.admin.ContentManagementController;
import com.lingotower.ui.views.AbstractContentView;

/**
 * View class for the Content Management screen in admin panel.
 */
public class ContentManagementView extends AbstractContentView {

	private Admin currentAdmin;
	private Runnable returnToDashboard;

	/**
	 * Constructs a new ContentManagementView.
	 */
	public ContentManagementView() {
		super("/admin/ContentManagementView.fxml");
	}

	@Override
	protected void initializeController() {
		ContentManagementController controller = getController();
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
	public ContentManagementView setAdmin(Admin admin) {
		this.currentAdmin = admin;
		ContentManagementController controller = getController();
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
	public ContentManagementView setReturnToDashboard(Runnable callback) {
		this.returnToDashboard = callback;
		ContentManagementController controller = getController();
		if (controller != null) {
			controller.setReturnToDashboard(callback);
		}
		return this;
	}

	@Override
	public void refresh() {
		ContentManagementController controller = getController();
		if (controller != null) {
			// No direct refresh method, but we could call specific methods
			// controller.loadCategories();
			// controller.loadWords();
		}
	}

	@Override
	public ContentManagementController getController() {
		return (ContentManagementController) super.getController();
	}
}