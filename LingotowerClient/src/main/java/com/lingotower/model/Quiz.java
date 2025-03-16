package com.lingotower.model;

import java.util.ArrayList;
import java.util.List;

public class Quiz {

    private Long id;
    private String name;
    private Category category;
    private Difficulty difficulty;
    private Admin adminByCreated;
    private List<Question> questions = new ArrayList<>();

    public Quiz() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Admin getAdminByCreated() {
        return adminByCreated;
    }

    public void setAdminByCreated(Admin adminByCreated) {
        this.adminByCreated = adminByCreated;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void addQuestion(Question question) {
        questions.add(question);
        question.setQuiz(this);
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + (category != null ? category.getName() : "None") +
                ", difficulty=" + difficulty +
                ", adminByCreated=" + (adminByCreated != null ? adminByCreated.getId() : "None") +
                '}';
    }
}

