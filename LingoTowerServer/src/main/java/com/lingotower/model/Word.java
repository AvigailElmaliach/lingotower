package com.lingotower.model;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne; 

@Entity
public class Word {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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

	private String language;

	public Word() {}


	public Long getId() {
		return id;
	}
	public Word(String word, String translation, String language) {
        this.word = word;
        this.translation = translation;
        this.language = language;
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
	 public String getLanguage() {
	        return language;
	    }

	    public void setLanguage(String language) {
	        this.language = language;
	    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Word word = (Word) o;
		return id != null && id.equals(word.id);
	}


	@Override
	public String toString() {
		return "Word{" +
				"id=" + id +
				", word='" + word + '\'' +
				", translation='" + translation + '\'' +
				", category='" + (category != null ? category.getName() : "None") + '\'' +
				'}';
	}


}
