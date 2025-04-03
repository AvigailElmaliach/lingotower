package com.lingotower.model;

import jakarta.persistence.*;

/**
 * Represents a base class for users in the system. 
 * This class contains common user fields such as username, password, email, language, and role. 
 * It is used as a parent class for different types of users (e.g., Admin, User).
 * 
 * The class is annotated with @MappedSuperclass to allow inheritance of its fields 
 * to subclasses without being directly mapped to a database table.
 */
@MappedSuperclass
public abstract class BaseUser {

    /**
     * The unique identifier for the user.
     * This field will be inherited by any subclass of BaseUser.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The username of the user.
     * It is used to uniquely identify a user in the system.
     */
    private String username;

    /**
     * The password of the user.
     * It is used for authentication and should be stored securely (e.g., hashed).
     */
    private String password;

    /**
     * The email address of the user.
     * It can be used for communication or as a unique identifier.
     */
    private String email;

    /**
     * The preferred language of the user.
     * This field can be used for localization purposes.
     */
    private String sourceLanguage="en";
    private String targetLanguage="he";

    /**
     * The role of the user. This defines the level of access or permissions a user has.
     * Roles can include values like USER, ADMIN, SUPERADMIN, etc.
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * Default constructor for BaseUser.
     * Initializes a new BaseUser object without setting any field values.
     */
    public BaseUser() {}

    /**
     * Constructor to initialize a BaseUser with specific values.
     * 
     * @param username The username of the user.
     * @param password The password of the user.
     * @param email The email address of the user.
     * @param language The language preference of the user.
     * @param role The role of the user.
     */
    public BaseUser(String username, String password, String email, String sourceLanguage,String targetLanguage, Role role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.role = role;
    }

    // Getters and setters

    /**
     * @return The unique ID of the user.
     */
    public Long getId() {
        return id;
    }

    /**
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     * 
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return The password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     * 
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return The email address of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     * 
     * @param email The email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The preferred language of the user.
     */
    public String getSourceLanguage() {
        return sourceLanguage;
    }

    /**
     * Sets the language preference of the user.
     * 
     * @param language The language to set.
     */
    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }
    
    
    public String getTargetLanguage () {
    	return targetLanguage;
    }
    
    
    public void setTargetLanguage (String targetLanguage) {
    	this.targetLanguage=targetLanguage;
    }

    /**
     * @return The role of the user.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the role of the user.
     * 
     * @param role The role to set.
     */
    public void setRole(Role role) {
        this.role = role;
    }
   
}
