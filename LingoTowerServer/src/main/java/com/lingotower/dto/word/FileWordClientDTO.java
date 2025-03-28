package com.lingotower.dto.word;

import com.lingotower.model.Difficulty;

public class FileWordClientDTO {
	private String word;
    private String sourceLang;
    private String targetLang;
    private Difficulty difficulty;
    
    public FileWordClientDTO(String word,String sourceLang,String targetLang, Difficulty difficulty) {
        this.word = word;
        this.sourceLang=sourceLang;
        this.targetLang=targetLang;
        this.difficulty = difficulty;
    }   
        
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getSourceLang() {
        return sourceLang;
    }

    public void setSourceLang(String sourceLang) {
        this.sourceLang = sourceLang;
    }
    
    public String getTargetLang() {
        return targetLang;
    }

    public void setTargetLang(String targetLang) {
        this.targetLang = targetLang;
    } 
    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
