package com.lingotower.dto.user;
public class UserCreateDTO {
    private String username;
    private String password;
    private String email;
    private String language;

    public UserCreateDTO(String username, String password, String email, String language) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.language = language;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getLanguage() { return language; }
}
