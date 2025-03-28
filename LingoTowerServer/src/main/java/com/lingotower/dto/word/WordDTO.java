package com.lingotower.dto.word;

import com.lingotower.model.Difficulty;

public class WordDTO {
    private String word;
    private Difficulty difficulty;
    private String language;
    private String category;
    

    public WordDTO() {}

    public WordDTO(String word, Difficulty difficulty, String language, String category) {
        this.word = word;
        this.difficulty = difficulty;
        this.language = language;
        this.category = category;
        
    
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }




}

