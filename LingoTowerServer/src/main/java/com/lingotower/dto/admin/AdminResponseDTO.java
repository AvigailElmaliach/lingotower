package com.lingotower.dto.admin;

import com.lingotower.model.Role;
//כדי לא להחזיר מידע רגיש ללקוח כמו סיסמה הגדרתי מחלקה זאת

/**
 * AdminResponseDTO is a Data Transfer Object (DTO) class that returns data about admin users.
 * The purpose of this class is to return sensitive information to the client without including 
 * the password or any other sensitive data.
 * The class contains the username and the role of the user.
 * 
 * @author [Your Name]
 * @version 1.0
 */
public class AdminResponseDTO {

    /**
     * The username of the admin.
     */
    private String username;

    /**
     * The role of the user (e.g., ADMIN).
     */
    private Role role;

    /**
     * Constructs an AdminResponseDTO with the username and role.
     * 
     * @param username the username of the admin.
     * @param role the role of the admin.
     */
    public AdminResponseDTO(String username, Role role) {
        this.username = username;
        this.role = role;
    }

    /**
     * Returns the username of the admin.
     * 
     * @return the username of the admin.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the admin.
     * 
     * @param username the username of the admin.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the role of the admin.
     * 
     * @return the role of the admin.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the role of the admin.
     * 
     * @param role the role of the admin (e.g., ROLE.ADMIN).
     */
    public void setRole(Role role) {
        this.role = role;
    }
}
