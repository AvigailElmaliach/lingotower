package com.lingotower.dto.user;

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String language;

    public UserDTO(Long id, String username, String email, String language) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.language = language;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getLanguage() { return language; }
}
