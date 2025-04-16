package com.lingotower.ui.views;

import com.lingotower.ui.controllers.DashboardViewController;
import com.lingotower.ui.controllers.MainApplicationController;

public class DashboardView extends AbstractContentView {

	public DashboardView() {
		super("/fxml/DashboardView.fxml");
	}

	@Override
	protected void initializeController() {
		// Nothing to initialize when first created
	}

	@Override
	public void refresh() {
		DashboardViewController controller = getController();
		if (controller != null) {
			controller.loadCategories();
		}
	}

	public void setMainController(MainApplicationController mainController) {
		DashboardViewController controller = getController();
		if (controller != null) {
			controller.setMainController(mainController);
		}
	}

	@Override
	public DashboardViewController getController() {
		return (DashboardViewController) super.getController();
	}
}