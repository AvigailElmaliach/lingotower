package com.lingotower.dto.word;

import com.lingotower.model.Difficulty;

public class WordDTO {
    private String word;
    private Difficulty difficulty;
    private String sourceLanguage;
    private String targetLanguage;
    private String category;
    private String translate; 
    
    public WordDTO() {}

    public WordDTO(String word, Difficulty difficulty, String sourceLanguage, String targetLanguage, String category, String translate) {
        this.word = word;
        this.difficulty = difficulty;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.category = category;
        this.translate = translate; 
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

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTranslate() {
        return translate; 
    }

    public void setTranslate(String translate) {
        this.translate = translate; 
    }
}
