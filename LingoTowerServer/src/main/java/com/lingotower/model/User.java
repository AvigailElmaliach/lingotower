package com.lingotower.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User extends BaseUser {

    @CreationTimestamp
    private LocalDateTime atCreated;

    @ManyToOne
    @JoinColumn(name = "admin_id") 
    private Admin admin;

    @ManyToMany
    @JoinTable(
        name = "user_learned_words",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "word_id")
    )
    private List<Word> learnedWords = new ArrayList<>();

    public User() {}

    public User(String username, String password, String email, String sourceLanguage,String targetLanguage, Role role) {
        super(username, password,email,sourceLanguage,targetLanguage, role);
    }

    public LocalDateTime getAtCreated() {
        return atCreated;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public List<Word> getLearnedWords() {
        return learnedWords;
    }

    public void addLearnedWord(Word word) {
        if (!learnedWords.contains(word)) {
            learnedWords.add(word);
        }
    }

    public void removeLearnedWord(Word word) {
        learnedWords.remove(word);
    }
}
