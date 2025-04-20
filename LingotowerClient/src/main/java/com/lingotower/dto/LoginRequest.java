package com.lingotower.dto;

/**
 * Represents a login request containing user credentials.
 */
public class LoginRequest {

    // The identifier can be either a username or an email
    private String identifier; 
    private String password; // The user's password

    /**
     * Default constructor for creating an empty LoginRequest object.
     */
    public LoginRequest() {
    }

    /**
     * Constructor for creating a LoginRequest object with identifier and password.
     *
     * @param identifier The username or email of the user.
     * @param password   The password of the user.
     */
    public LoginRequest(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    /**
     * Gets the identifier (username or email).
     *
     * @return The identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier (username or email).
     *
     * @param identifier The identifier to set.
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Gets the password.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
