package com.lingotower.model;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

//import javax.persistence.CascadeType;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.OneToMany;      
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;


@Entity
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true, nullable = false)
	private String name;
	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL,orphanRemoval = true)
	 @JsonIgnore 
    private List<Quiz> quizzes = new ArrayList<>();
	 @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
	 @JsonIgnore
	    private List<Question> questions = new ArrayList<>();
	 
//	 @OneToMany(mappedBy = "category")
//	 private List<Word> words;
	public Category() {}

	public Category(String name) {
	 this.name=name;
	}
//	 public Category(String name, List<Word> words) {
//	        this.name = name;
//	        this.words = words;
//	    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public void addQuiz(Quiz quiz) {
        quizzes.add(quiz);
        quiz.setCategory(this);
    }
    public List<Question> getQuestions() { return questions; }

    public void addQuestion(Question question) {
        questions.add(question);
        question.setCategory(this);
    }


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Category category = (Category) o;
		return id != null && id.equals(category.id);
	}
	

	/*   @Override
	    public String toString() {
	        return "Category{" +
	                "id=" + id +
	                ", name='" + name + '\'' +
	                '}';
	    }*/
}




