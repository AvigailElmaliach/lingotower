package com.lingotower.model;

import javax.persistence.*;
import java.util.List;
//import com.lingotower.model.Quiz; //לבדוק אם הכרחי

@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionText;
    private String correctAnswer;

    @ElementCollection
    private List<String> wrongAnswers;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

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
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
