package com.lingotower.dto.quiz;

import com.lingotower.model.Difficulty;

import java.util.List;

public class QuizRequest {
    private Long categoryId;
    private Difficulty difficulty;
    private Long userId;
    
    public Long getCategoryId() {
    return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
    	this.categoryId=categoryId;
    }
    
    public Difficulty getDifficulty() {
    return difficulty;
    }
    
    public void setDifficlty() {
    this.difficulty=difficulty;
    }
    
    public Long getUserId() {
    	return userId;
    }
    
    public void setUserId() {
    	this.userId=userId;
    }
    
}
