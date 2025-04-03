package com.lingotower.dto.admin;

import com.lingotower.model.Role;

public class AdminCreateDTO {
    private String username;
    private String password;
    private String email;
    private Role role;
   private String sourceLanguage;
   private String targetLanguage;


    public AdminCreateDTO() {
    }

    public AdminCreateDTO(String username, String password,Role role,String email,String sourceLanguage ,String targetLanguage ) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email= email;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage=targetLanguage;
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

	  public String getSourceLanguage() { return sourceLanguage; }
	    public String getTargetLanguage() { return targetLanguage; }
	    
	public void setSourceLanguage(String language) {
	this.sourceLanguage=sourceLanguage;
	}
	
}
