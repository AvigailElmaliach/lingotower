package com.lingotower.dto.admin;
import com.lingotower.model.Role;
public class AdminResponseDTO {
    private String username;
    private Role role;
    // Public constructor, Getters needed by Jackson/RestTemplate
    public AdminResponseDTO() {}
    public AdminResponseDTO(String u, Role r) { username = u; role = r; }
    public String getUsername() { return username; }
    public Role getRole() { return role; }
    public void setUsername(String u) {username=u;} // Setter might be needed
    public void setRole(Role r) {role=r;}       // Setter might be needed
}
