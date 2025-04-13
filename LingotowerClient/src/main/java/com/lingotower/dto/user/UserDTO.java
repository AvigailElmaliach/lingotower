package com.lingotower.dto.user;
public class UserDTO { // DTO received from /admins/users
    private Long id;
    private String username;
    private String email;
    private String language; // Matches server DTO
    // Public constructor, Getters, Setters needed by Jackson/RestTemplate
    public UserDTO() {}
    public UserDTO(Long id, String u, String e, String l) {this.id=id; username=u; email=e; language=l;}
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getUsername(){return username;}
    public void setUsername(String u){username=u;}
    public String getEmail(){return email;}
    public void setEmail(String e){email=e;}
    public String getLanguage(){return language;}
    public void setLanguage(String l){language=l;}
}
