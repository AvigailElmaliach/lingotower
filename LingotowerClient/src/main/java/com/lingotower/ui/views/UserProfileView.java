package com.lingotower.ui.views;

import com.lingotower.model.User;
import com.lingotower.ui.controllers.UserProfileController;

public class UserProfileView extends AbstractContentView {

	private User currentUser;

	public UserProfileView() {
		super("/fxml/UserProfile.fxml");
	}

	@Override
	protected void initializeController() {
		// Set the user if available
		if (currentUser != null) {
			UserProfileController controller = getController();
			if (controller != null) {
				controller.setUser(currentUser);
			}
		}
	}

	public void setUser(User user) {
		this.currentUser = user;
		UserProfileController controller = getController();
		if (controller != null) {
			controller.setUser(user);
		}
	}

	@Override
	public void refresh() {
		UserProfileController controller = getController();
		if (controller != null) {
			controller.refresh();
		}
	}

	@Override
	public UserProfileController getController() {
		return (UserProfileController) super.getController();
	}
}