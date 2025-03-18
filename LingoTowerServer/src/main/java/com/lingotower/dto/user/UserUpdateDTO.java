package com.lingotower.dto.user;

public class UserUpdateDTO {
    private String username;
    private String email;
    private String language;

    
    public UserUpdateDTO() {}

    public UserUpdateDTO(String username, String email, String language) {
        this.username = username;
        this.email = email;
        this.language = language;
    }

    
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getLanguage() {
        return language;
    }

}
