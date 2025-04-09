package com.lingotower.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Word {

	private Long id;
	private String word;
	@JsonAlias({ "translation", "translatedText" })
	private String translatedText;
	private Category category;
	private Admin adminByAdded;
	private List<User> users = new ArrayList<>();
	private String language;
	private Difficulty difficulty;
	private String sourceLanguage;
	private String targetLanguage;

	public Word() {
	}

	public Word(String word, String translation, String language) {
		this.word = word;
		this.translatedText = translation;
		this.language = language;
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

	public Admin getAdminByAdded() {
		return adminByAdded;
	}

	public void setAdminByAdded(Admin adminByAdded) {
		this.adminByAdded = adminByAdded;
	}

	public List<User> getUsers() {
		return users;
	}

	public void addUser(User user) {
		users.add(user);
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Word word = (Word) o;
		return id != null && id.equals(word.id);
	}

	@Override
	public String toString() {
		return "Word{" + "id=" + id + ", word='" + word + '\'' + ", translation='" + translatedText + '\''
				+ ", category=" + (category != null ? category.getName() : "None") + '}';
	}
}
