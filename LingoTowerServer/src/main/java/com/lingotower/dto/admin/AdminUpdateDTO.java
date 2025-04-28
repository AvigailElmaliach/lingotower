package com.lingotower.dto.admin;

import com.lingotower.model.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class AdminUpdateDTO {

    @NotBlank(message = "Username is required")
    private String username;
    private Role role;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Password must be at least 8 characters long and contain at least one letter and one number")
    private String password; 
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotEmpty(message = "Current password is required for update")
    private String oldPassword;

    public AdminUpdateDTO() {
    }

    public AdminUpdateDTO(@NotBlank(message = "Username is required") String username, Role role,
                          @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Password must be at least 8 characters long and contain at least one letter and one number") String password,
                          @Email(message = "Invalid email format") @NotBlank(message = "Email is required") String email,
                          @NotEmpty(message = "Current password is required for update") String oldPassword) {
        this.username = username;
        this.role = role;
        this.password = password;
        this.email = email;
        this.oldPassword = oldPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}