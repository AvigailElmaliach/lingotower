package com.lingotower.dto.translation;

public class TranslationResponseDTO {
	private String word;
	private String translatedText;

	public TranslationResponseDTO() {
	}

	public TranslationResponseDTO(String word, String translatedText) {
		this.word = word;
		this.translatedText = translatedText;
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
}
