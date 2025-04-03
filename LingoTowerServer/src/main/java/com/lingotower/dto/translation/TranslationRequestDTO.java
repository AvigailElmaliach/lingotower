package com.lingotower.dto.translation;

public class TranslationRequestDTO {
    private String word;//מילה זה גם משפט
    private boolean swapLanguages; // True מתבצעת החלפה בין שפת המקור והיעד

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
    public boolean isSwapLanguages() { return swapLanguages; }
    public void setSwapLanguages(boolean swapLanguages) { this.swapLanguages = swapLanguages; }
    public Boolean getSwapLanguages() {  return swapLanguages; }
	
	}

