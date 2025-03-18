package com.lingotower.dto.admin;

	public class AdminUpdateDTO {
	    private String username;
	    private String role;
	    private String password; // אופציונלי: לשינוי סיסמה

	    public String getUsername() {
	        return username;
	    }

	    public void setUsername(String username) {
	        this.username = username;
	    }

	    public String getRole() {
	        return role;
	    }

	    public void setRole(String role) {
	        this.role = role;
	    }

	    public String getPassword() {
	        return password;
	    }

	    public void setPassword(String password) {
	        this.password = password;
	    }
	}
