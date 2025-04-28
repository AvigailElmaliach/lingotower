package com.lingotower.dto.category;

public class CategoryDTO {

	private Long id;
	private String name;
	private String translation;

	public CategoryDTO() {
	}

	public CategoryDTO(Long id, String name, String translation) {
		this.id = id;
		this.name = name;
		this.translation = translation;
	}

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

	public String getTranslation() {
		return translation;
	}

	public void setTranslatedName(String translation) {
		this.translation = translation;
	}
}
