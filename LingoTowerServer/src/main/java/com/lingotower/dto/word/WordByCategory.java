package com.lingotower.dto.word;

import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;

public class WordByCategory {

		private Long id;
	    private String word;
	    private String translatedText;
	    private Category category;
	    private Difficulty difficulty;
	    

	    public WordByCategory() {}
	    
	    public WordByCategory(Long id, String word, String translatedText, Category category, Difficulty difficulty) {
	        this.id = id;
	        this.word = word;
	        this.translatedText = translatedText;
	        this.category = category;
	        this.difficulty = difficulty;
	    }
	   
	    public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public String getWord() {
	        return word;
	    }

	    public void setWord(String word) {
	        this.word = word;
	    }

	    public String getTranslatedText() {
	        return translatedText;
	    }

	    public void setTranslatedText(String translatedText) {
	        this.translatedText = translatedText;
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
	}

















