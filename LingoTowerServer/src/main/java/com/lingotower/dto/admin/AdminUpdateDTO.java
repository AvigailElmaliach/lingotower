package com.lingotower.dto.admin;

import com.lingotower.model.Role;

public class AdminUpdateDTO {
	    private String username;
	    private Role role;
	    private String password; // אופציונלי: לשינוי סיסמה

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
	}
