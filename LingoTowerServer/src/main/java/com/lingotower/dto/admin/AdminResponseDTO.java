package com.lingotower.dto.admin;

import com.lingotower.model.Role;



/**
 * AdminResponseDTO is a Data Transfer Object (DTO) class that returns data about admin users.
 * The purpose of this class is to return sensitive information to the client without including 
 * the password or any other sensitive data.
 * The class contains the username, email, ID number, and role of the user.
 * 
 * @author [Your Name]
 * @version 1.1
 */
public class AdminResponseDTO {

    /**
     * The username of the admin.
     */
    private String username;

    /**
     * The email of the admin.
     */
    private String email;

    /**
     * The ID number (Teudat Zehut) of the admin.
     */
    private Long id;

    /**
     * The role of the user (e.g., ADMIN).
     */
    private Role role;

    /**
     * Constructs an AdminResponseDTO with username, email, ID number, and role.
     * 
     * @param username the username of the admin.
     * @param email the email of the admin.
     * @param idNumber the ID number of the admin.
     * @param role the role of the admin.
     */
    public AdminResponseDTO(Long id,String username, String email, Role role) {
    	this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }


	public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
} 
