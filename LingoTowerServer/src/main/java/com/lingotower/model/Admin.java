package com.lingotower.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Admin extends BaseUser {

    

    @OneToMany(mappedBy = "admin")
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "adminByCreated", cascade = CascadeType.ALL)
    private List<Quiz> quizzes = new ArrayList<>();

    public Admin() {}
    
    public Admin(String username, String password, String email, String language, Role role) {
        super(username, password, email, language, role);  // מעביר את הערכים ל־BaseUser
    }
    
  

    public List<User> getUsers() {
        return users;
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public void addQuiz(Quiz quiz) {
        quizzes.add(quiz);
        quiz.setAdminByCreated(this);
    }
}
