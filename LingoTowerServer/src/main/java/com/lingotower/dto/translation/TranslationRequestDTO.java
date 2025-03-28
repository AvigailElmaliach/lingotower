package com.lingotower.dto.translation;

public class TranslationRequestDTO {
    private String word;
    private String sourceLang;
    private String targetLang;
    

    public TranslationRequestDTO(String word, String sourceLang, String targetLang) {
		this.word=word;
		this.sourceLang=sourceLang;
		this.targetLang=targetLang;
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
}

