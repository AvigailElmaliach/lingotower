package com.lingotower.dto.translation;

public class TranslationDTO {
	private String word;
	private String translation;
	private String sourceLanguage;
	private String targetLanguage;
	private int difficultyLevel;

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
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

	public int getDifficultyLevel() {
		return difficultyLevel;
	}

	public void setDifficultyLevel(int difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
	}

	public TranslationDTO(String word, String translation, String sourceLanguage, String targetLanguage,
			int difficultyLevel) {
		this.word = word;
		this.translation = translation;
		this.sourceLanguage = sourceLanguage;
		this.targetLanguage = targetLanguage;
		this.difficultyLevel = difficultyLevel;
	}

	@Override
	public String toString() {
		return "TranslationDTO{" + "word='" + word + '\'' + ", translation='" + translation + '\''
				+ ", sourceLanguage='" + sourceLanguage + '\'' + ", targetLanguage='" + targetLanguage + '\''
				+ ", difficultyLevel=" + difficultyLevel + '}';
	}
}
