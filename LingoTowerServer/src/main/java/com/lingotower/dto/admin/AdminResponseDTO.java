package com.lingotower.dto.admin;
//מתמשים כאשר מחזירים נתונים ללקוח ללא סיסמה מידע רגיש

public class AdminResponseDTO {
    private String username;
    private String role;
    
    public AdminResponseDTO(String username, String role) {
        this.username = username;
        this.role = role;
    }


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
}
