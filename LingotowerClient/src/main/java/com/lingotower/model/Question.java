package com.lingotower.model;

import java.util.List;

public class Question {
    private Long id;
    private String questionText;
    private String correctAnswer;
    private List<String> wrongAnswers;
    private Quiz quiz; //לבדוק אם כדאי לשנות טיפוס ללונג או אינט
    private Category category; // כנל

   
    public Question() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public List<String> getWrongAnswers() { return wrongAnswers; }
    public void setWrongAnswers(List<String> wrongAnswers) { this.wrongAnswers = wrongAnswers; }

    public Quiz getQuiz() { return quiz; }
    public void setQuizId(Quiz quiz) { this.quiz = quiz; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public void setQuiz(Quiz quiz) {//לבדוק אם השינוי שבוצע בטיפוסים משפיע על זה בהמשך
        this.quiz = quiz;
    }

}
