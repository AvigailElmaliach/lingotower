package com.lingotower.dto.admin;

import com.lingotower.model.Role;

public class AdminCreateDTO {
    private String username;
    private String password;
    private String email;
    private Role role;
   private String language;


    public AdminCreateDTO() {
    }

    public AdminCreateDTO(String username, String password,Role role,String email,String language ) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email= email;
    }
    
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

	public String getEmail() {
		return email;
	}
	public void setEmail(String email)
	{
		this.email=email;
	}

	public String getLanguage() {
	return language;
	}
	public void setLanguage(String language) {
	this.language=language;
	}
	
}
