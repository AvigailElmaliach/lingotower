package com.lingotower.config;


import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingotower.constants.LanguageConstants;
import com.lingotower.dto.category.CategoryDTO;
import com.lingotower.model.Category;
import com.lingotower.service.CategoryService;
import com.lingotower.service.TranslationService;

@Component
@Order(1)
public class CategoryDataInitializer implements CommandLineRunner {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private TranslationService translationService;

	@Override
	public void run(String... args) {
		loadCategoriesFromJson("classpath:category.json");
	}

	private void loadCategoriesFromJson(String resourcePath) {
		try {
			Resource resource = getResource(resourcePath);
			if (resource == null || !resource.exists()) {
				System.out.println("Category file not found: " + resourcePath);
				return;
			}

			CategoryDTO[] categoryDtos = parseJsonToCategoryDTO(resource);
			processCategories(categoryDtos);

		} catch (IOException e) {
			System.err.println("Error loading categories: " + e.getMessage());
		}
	}

	private Resource getResource(String resourcePath) {
		return resourceLoader.getResource(resourcePath);
	}

	private CategoryDTO[] parseJsonToCategoryDTO(Resource resource) throws IOException {
		return objectMapper.readValue(resource.getInputStream(), CategoryDTO[].class);
	}

	private void processCategories(CategoryDTO[] categoryDtos) {
		for (CategoryDTO dto : categoryDtos) {
			String name = dto.getName();
			String translation = dto.getTranslation();

			Optional<Category> existingCategory = categoryService.findByName(name);

			if (existingCategory.isEmpty()) {
				Category newCategory = createCategory(name, translation);
				categoryService.addCategory(newCategory);
			}
		}
	}

	private Category createCategory(String name, String translation) {
		Category newCategory = new Category();
		newCategory.setName(name);

		if (translation == null || translation.trim().isEmpty()) {
			translation = translationService.translateText(name, LanguageConstants.ENGLISH, LanguageConstants.HEBREW);
		}

		newCategory.setTranslation(translation);
		return newCategory;
	}
}
