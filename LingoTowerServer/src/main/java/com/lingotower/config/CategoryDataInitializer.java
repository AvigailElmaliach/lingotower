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

	/**
	 * Runs after the application context is loaded to initialize category data. It
	 * loads categories from a JSON file specified by 'classpath:category.json'.
	 * 
	 * @param args Command line arguments passed to the application.
	 */
	@Override
	public void run(String... args) {
		loadCategoriesFromJson("classpath:category.json");
	}

	/**
	 * Loads category data from a JSON file. It reads the file, parses the JSON into
	 * CategoryDTO objects, and then processes each DTO to create or update Category
	 * entities in the database.
	 * 
	 * @param resourcePath The classpath path to the JSON file containing category
	 *                     data.
	 */
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

	/**
	 * Retrieves a Resource object for the given resource path.
	 * 
	 * @param resourcePath The classpath path to the resource.
	 * @return The Resource object.
	 */
	private Resource getResource(String resourcePath) {
		return resourceLoader.getResource(resourcePath);
	}

	/**
	 * Parses a JSON resource into an array of CategoryDTO objects.
	 * 
	 * @param resource The Resource object containing the JSON data.
	 * @return An array of CategoryDTO objects parsed from the JSON.
	 * @throws IOException If an I/O error occurs while reading the resource.
	 */
	private CategoryDTO[] parseJsonToCategoryDTO(Resource resource) throws IOException {
		return objectMapper.readValue(resource.getInputStream(), CategoryDTO[].class);
	}

	/**
	 * Processes an array of CategoryDTO objects to create or update Category
	 * entities in the database. For each DTO, it checks if a category with the same
	 * name already exists. If not, it creates a new Category entity and adds it to
	 * the database.
	 * 
	 * @param categoryDtos An array of CategoryDTO objects to process.
	 */
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

	/**
	 * Creates a new Category entity with the given name and translation. If the
	 * provided translation is null or empty, it attempts to translate the name from
	 * English to Hebrew using the TranslationService.
	 * 
	 * @param name        The name of the category.
	 * @param translation The translation of the category name.
	 * @return The newly created Category entity.
	 */
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