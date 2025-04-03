package com.lingotower.dto;

public class RegisterRequest {

    private String username;
    private String password;
    private String email;
    private String sourceLanguage;
    private String targetLanguage;

    public RegisterRequest() {
    }

    public RegisterRequest(String username, String password, String email, String sourceLanguage) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = determineTargetLanguage(sourceLanguage);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
