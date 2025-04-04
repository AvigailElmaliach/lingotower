package com.lingotower.model;

import jakarta.persistence.*;

@Entity
@Table(name = "example_sentence")
public class ExampleSentence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

  
    @Column(nullable = false, columnDefinition = "TEXT")
    private String sentenceText;

    @ManyToOne
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    public ExampleSentence() {}

    public ExampleSentence(String sentenceText, Word word) {
        this.sentenceText = sentenceText;
        this.word = word;
    }

    public Long getId() {
        return id;
    }

    public String getSentenceText() {
        return sentenceText;
    }

    public void setSentenceText(String sentenceText) {
        this.sentenceText = sentenceText;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }
}
