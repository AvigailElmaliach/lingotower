package com.lingotower.model;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private Long id;
    private String name;
    private List<Quiz> quizzes = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();

    public Category() {}

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

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

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public void addQuiz(Quiz quiz) {
        quizzes.add(quiz);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    // שיטה להשוואת אובייקטים על פי ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id != null && id.equals(category.id);
    }
    @Override
    public String toString() {
        return "Category{id=" + id + ", name='" + name + "'}";
    }

}
