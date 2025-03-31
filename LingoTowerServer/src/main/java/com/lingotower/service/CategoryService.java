package com.lingotower.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lingotower.data.CategoryRepository;
import com.lingotower.dto.category.CategoryDTO;
import com.lingotower.exception.CategoryAlreadyExistsException;
import com.lingotower.exception.CategoryNotFoundException;
import com.lingotower.model.Category;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;

	@Autowired
	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	private CategoryDTO convertToDTO(Category category) {
		return new CategoryDTO(category.getId(), category.getName());
	}

	public List<CategoryDTO> getAllCategories() {
		List<Category> categories = categoryRepository.findAll();

		// Define the correct order for category names
		Map<String, Integer> categoryOrder = new HashMap<>();
		categoryOrder.put("Everyday Life and Essential Vocabulary", 1);
		categoryOrder.put("People and Relationships", 2);
		categoryOrder.put("Work and Education", 3);
		categoryOrder.put("Health and Well-being", 4);
		categoryOrder.put("Travel and Leisure", 5);
		categoryOrder.put("Environment and Nature", 6);

		// Sort by the predefined order
		categories.sort(Comparator.comparing(c -> categoryOrder.getOrDefault(c.getName(), Integer.MAX_VALUE)));

		return categories.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public Optional<Category> findByName(String name) {
		return categoryRepository.findByName(name);
	}

	public Category getOrCreateCategory(String name) {
		return categoryRepository.findByName(name).orElseGet(() -> {
			Category newCategory = new Category();
			newCategory.setName(name);
			return categoryRepository.save(newCategory);
		});
	}

	public Category getCategoryById(Long id) throws CategoryNotFoundException {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
	}

	public Category addCategory(Category category) {
		// Check if category with this name already exists
		Optional<Category> existingCategory = categoryRepository.findByName(category.getName());
		if (existingCategory.isPresent()) {
			throw new CategoryAlreadyExistsException("קטגוריה עם שם זה כבר קיימת");
		}

		// Save the new category and let the database assign an ID
		return categoryRepository.save(category);
	}

	public void deleteCategory(Long id) throws CategoryNotFoundException {
		if (!categoryRepository.existsById(id)) {
			throw new CategoryNotFoundException("Category not found with id: " + id);
		}
		categoryRepository.deleteById(id);
	}

	public Category updateCategory(Long id, CategoryDTO categoryDTO) throws CategoryNotFoundException {
		Category existingCategory = getCategoryById(id); // throws exception if not found
		existingCategory.setName(categoryDTO.getName());
		return categoryRepository.save(existingCategory);
	}

	public void deleteAllCategories() {
		categoryRepository.deleteAll();
	}
}