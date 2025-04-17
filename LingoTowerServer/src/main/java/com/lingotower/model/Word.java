package com.lingotower.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.*;

@Entity
@Table(name = "word", uniqueConstraints = @UniqueConstraint(columnNames = { "word", "category_id" }), indexes = {
		@Index(name = "idx_category_id", columnList = "category_id"),
		@Index(name = "idx_difficulty", columnList = "difficulty") })
public class Word {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String word;

	private String translation;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@ManyToOne
	@JoinColumn(name = "admin_by_added_id")
	private Admin adminByAdded;
	@ManyToMany(mappedBy = "learnedWords")
	private List<User> users = new ArrayList<>();

	private String sourceLanguage;
	private String targetLanguage;

	@Enumerated(EnumType.STRING)
	private Difficulty difficulty;

//	@OneToMany(mappedBy = "word", cascade = CascadeType.ALL, orphanRemoval = true)
//	private List<ExampleSentence> sentences;
	@OneToMany(mappedBy = "word", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<ExampleSentence> sentences;
	public Word() {
	}

	public Word(String word, String translation, String sourceLanguage) {
		this.word = word;
		this.translation = translation;
		this.sourceLanguage = sourceLanguage;

	}

	public Word(String word, String translation, String sourceLanguage, String targetLanguage) {
		this.word = word;
		this.translation = translation;
		this.sourceLanguage = sourceLanguage;
		this.targetLanguage = targetLanguage;
	}

	public Word(Object object, String word, String translation, String sourceLanguage, String targetLanguage,
			Difficulty difficulty, Category category) {
		this.word = word;
		this.translation = translation;
		this.sourceLanguage = sourceLanguage;
		this.targetLanguage = targetLanguage;
		this.difficulty = difficulty;
		this.category = category;
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

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
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

	public void addUser(User user) {
		users.add(user);
	}

	public String getSourceLanguage() {
		return sourceLanguage;
	}

	public void setSourceLanguage(String sourceLanguage) {
		this.sourceLanguage = sourceLanguage;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	public String getTargetLanguage() {
		return targetLanguage;
	}

	public void setTargetLanguage(String targetLanguage) {
		this.targetLanguage = targetLanguage;
	}

	public List<ExampleSentence> getSentences() {
		return sentences;
	}

	public void setSentences(List<ExampleSentence> sentences) {
		this.sentences = sentences;
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
		return "Word{" + "id=" + id + ", word='" + word + '\'' + ", translation='" + translation + '\'' + ", category='"
				+ (category != null ? category.getName() : "None") + '\'' + '}';
	}

}
