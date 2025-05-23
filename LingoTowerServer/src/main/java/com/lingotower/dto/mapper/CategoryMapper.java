package com.lingotower.dto.mapper;

import org.springframework.stereotype.Component;

import com.lingotower.dto.category.CategoryDTO;
import com.lingotower.model.Category;

@Component
public class CategoryMapper {

	public CategoryDTO toDto(Category category) {
		if (category == null) {
			return null;
		}

		CategoryDTO categoryDTO = new CategoryDTO();
		categoryDTO.setId(category.getId());
		categoryDTO.setName(category.getName());

		return categoryDTO;
	}

	public Category toEntity(CategoryDTO categoryDTO) {
		if (categoryDTO == null) {
			return null;
		}

		Category category = new Category();
		category.setId(categoryDTO.getId());
		category.setName(categoryDTO.getName());

		return category;
	}
}