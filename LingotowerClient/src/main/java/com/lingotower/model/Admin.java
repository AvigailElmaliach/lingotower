package com.lingotower.model;


import java.util.ArrayList;
import java.util.List;

public class Admin {

    private Long id;
    private String username;
    private String password;
    private String role;
    private List<User> users = new ArrayList<>();
    private List<Quiz> quizzes = new ArrayList<>();

    public Admin() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public void addQuiz(Quiz quiz) {
        quizzes.add(quiz);
        quiz.setAdminByCreated(this);
    }
}
