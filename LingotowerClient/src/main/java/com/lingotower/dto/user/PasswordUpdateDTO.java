package com.lingotower.dto.user;

/**
 * Data Transfer Object (DTO) for password update requests.
 * Contains the old username and the new password to be set for the user.
 */
public class PasswordUpdateDTO {

    private String oldUsername; // The current (old) username
    private String newPassword; // The new password to be set for the user

    /**
     * Default constructor for creating an empty PasswordUpdateDTO object.
     */
    public PasswordUpdateDTO() {
    }

    /**
     * Constructor for creating a PasswordUpdateDTO object with the old username and new password.
     *
     * @param oldUsername The old username of the user.
     * @param newPassword The new password to set.
     */
    public PasswordUpdateDTO(String oldUsername, String newPassword) {
        this.oldUsername = oldUsername;
        this.newPassword = newPassword;
    }

    /**
     * Gets the old username.
     *
     * @return The old username.
     */
    public String getOldUsername() {
        return oldUsername;
    }

    /**
     * Sets the old username.
     *
     * @param oldUsername The old username to set.
     */
    public void setOldUsername(String oldUsername) {
        this.oldUsername = oldUsername;
    }

    /**
     * Gets the new password.
     *
     * @return The new password.
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * Sets the new password.
     *
     * @param newPassword The new password to set.
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
