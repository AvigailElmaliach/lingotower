package com.lingotower.dto.user;

public class UserUpdateDTO {
    private String username;
    private String email;
    private String sourceLanguage;
    private String targetLanguage;

    
    public UserUpdateDTO() {}

    public UserUpdateDTO(String username, String email, String sourceLanguage) {
        this.username = username;
        this.email = email;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = determineTargetLanguage(sourceLanguage);
    }

    
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }
    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = determineTargetLanguage(sourceLanguage);
    }
    
    
    

    public String getTargetLanguage() {
        return targetLanguage;
    }

    private String determineTargetLanguage(String sourceLanguage) {
        return "he".equals(sourceLanguage) ? "en" : "he";
    }

}
