package com.lingotower.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {

	private Long id;
	private String username;
	private String password;
	private String email;
	private String language;
	private LocalDateTime atCreated;
	private Admin admin;
	private List<Word> learnedWords = new ArrayList<>();
	private String role;

	public User() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public LocalDateTime getAtCreated() {
		return atCreated;
	}

	public void setAtCreated(LocalDateTime atCreated) {
		this.atCreated = atCreated;
	}

	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	public List<Word> getLearnedWords() {
		return learnedWords;
	}

	public void setLearnedWords(List<Word> learnedWords) {
		this.learnedWords = learnedWords;
	}

	public void addLearnedWord(Word word) {
		if (!learnedWords.contains(word)) {
			learnedWords.add(word);
		}
	}

	public void removeLearnedWord(Word word) {
		learnedWords.remove(word);
	}

	public String getRole() { // Add this method
		return role;
	}

	public void setRole(String role) { // Add this method
		this.role = role;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		User user = (User) o;
		return id != null && id.equals(user.id);
	}

	@Override
	public String toString() {
		return "User{" + "id=" + id + ", username='" + username + '\'' + ", language='" + language + '\'' + '}';
	}
}
