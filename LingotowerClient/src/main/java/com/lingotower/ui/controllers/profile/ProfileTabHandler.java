private void updateUserModel(User user, String username, String email, String languageCode, String password) {
    user.setUsername(username);
    user.setEmail(email);
    user.setLanguage(languageCode);
    if (password != null && !password.isEmpty()) {
        user.setPassword(password);
    }
}
private void saveChangesToServer(User user, String password, Runnable onSaveSuccess) {
    try {
        boolean profileUpdated = false;
        boolean passwordUpdated = true; 

        
        if (user.getId() == null) {
            logger.error("Cannot update profile: User ID is missing");
            uiStateManager.showErrorMessage(
                    "Cannot update profile: User ID is missing. Please log out and log in again.");
            return;
        }

        String oldUsername = user.getUsername();

        logger.info("Updating user profile for ID: {}", user.getId());
        profileUpdated = userService.updateUser(oldUsername, user);

        if (!password.isEmpty()) {
            logger.info("Updating password for user: {}", oldUsername);
            passwordUpdated = userService.updateUserPassword(oldUsername, password);
        } else {
            logger.debug("No new password provided, skipping password update.");
        }

        handleSaveResults(profileUpdated, passwordUpdated, password, onSaveSuccess);

    } catch (Exception ex) {
        logger.error("Error updating profile: {}", ex.getMessage(), ex);
        uiStateManager.showErrorMessage("Error updating profile: " + ex.getMessage());
    }
}
private void handleSaveResults(boolean profileUpdated, boolean passwordUpdated, String password,
                                 Runnable onSaveSuccess) {
    if (profileUpdated && (!password.isEmpty() ? passwordUpdated : true)) {
        logger.info(
                "Profile updated successfully" + (!password.isEmpty() ? " and password updated successfully" : ""));
        uiStateManager.showSuccessMessage("Profile updated successfully! Log out and log in again to see changes");
        passwordField.clear();
        confirmPasswordField.clear();
        if (onSaveSuccess != null) {
            onSaveSuccess.run();
        }
    } else if (!profileUpdated && (!password.isEmpty() ? !passwordUpdated : false)) {
        logger.error("Failed to update profile information"
                + (!password.isEmpty() ? " and password update failed" : ""));
        uiStateManager.showErrorMessage("Failed to update profile information");
    } else if (!profileUpdated) {
        logger.error("Failed to update profile information");
        uiStateManager.showErrorMessage("Failed to update profile information");
    } else if (!passwordUpdated && !password.isEmpty()) {
        logger.error("Profile information updated successfully, but password update failed");
        uiStateManager.showErrorMessage("Profile information updated successfully, but password update failed");
    }
}