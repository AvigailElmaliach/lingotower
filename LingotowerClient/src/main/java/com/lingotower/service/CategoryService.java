package com.lingotower.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lingotower.model.Category;

/**
 * Service for managing categories.
 */
public class CategoryService extends BaseService {

	/**
	 * Constructor for CategoryService.
	 */
	public CategoryService() {
		super(); // Initialize the base service
		logger.debug("CategoryService initialized");
	}

	/**
	 * Fetches all categories from the server.
	 * 
	 * @return A list of all categories or an empty list if none are found or an
	 *         error occurs
	 */
	public List<Category> getAllCategories() {
		try {
			logger.info("Fetching all categories");

			String url = buildUrl(CATEGORIES_PATH);
			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<List<Category>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<Category>>() {
					});

			if (response.getStatusCode().is2xxSuccessful()) {
				List<Category> categories = response.getBody();
				logger.info("Successfully retrieved {} categories", categories != null ? categories.size() : 0);
				return categories != null ? categories : new ArrayList<>();
			} else {
				logger.error("Failed to retrieve categories. Status code: {}", response.getStatusCode());
				return new ArrayList<>();
			}
		} catch (Exception e) {
			logger.error("Error fetching categories: {}", e.getMessage(), e);
			return new ArrayList<>();
		}
	}

	/**
	 * Fetches a category by its ID.
	 * 
	 * @param id The ID of the category to fetch
	 * @return The category if found, null otherwise
	 */
	public Category getCategoryById(Long id) {
		try {
			logger.info("Fetching category with ID: {}", id);

			String url = buildUrl(CATEGORIES_PATH, id.toString());
			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<Category> response = restTemplate.exchange(url, HttpMethod.GET, entity, Category.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				logger.info("Successfully retrieved category with ID: {}", id);
				return response.getBody();
			} else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
				logger.warn("Category with ID {} not found", id);
				return null;
			} else {
				logger.error("Failed to retrieve category with ID: {}. Status code: {}", id, response.getStatusCode());
				return null;
			}
		} catch (Exception e) {
			logger.error("Error fetching category with ID {}: {}", id, e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Creates a new category.
	 * 
	 * @param category The category to create
	 * @return The created category with its assigned ID, or null if creation failed
	 */
	public Category addCategory(Category category) {
		try {
			logger.info("Adding new category: {}", category.getName());

			String url = buildUrl(CATEGORIES_PATH);
			HttpEntity<Category> entity = createAuthEntity(category);

			ResponseEntity<Category> response = restTemplate.postForEntity(url, entity, Category.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				Category createdCategory = response.getBody();
				logger.info("Successfully created category with ID: {}",
						createdCategory != null ? createdCategory.getId() : "unknown");
				return createdCategory;
			} else {
				logger.error("Failed to create category. Status code: {}", response.getStatusCode());
				return null;
			}
		} catch (Exception e) {
			logger.error("Error creating category: {}", e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Updates an existing category.
	 * 
	 * @param id       The ID of the category to update
	 * @param category The updated category information
	 * @return The updated category or null if update failed
	 */
	public Category updateCategory(Long id, Category category) {
		try {
			logger.info("Updating category with ID: {}", id);

			String url = buildUrl(CATEGORIES_PATH, id.toString());
			HttpEntity<Category> entity = createAuthEntity(category);

			ResponseEntity<Category> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Category.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				Category updatedCategory = response.getBody();
				logger.info("Successfully updated category with ID: {}", id);
				return updatedCategory;
			} else {
				logger.error("Failed to update category with ID: {}. Status code: {}", id, response.getStatusCode());
				return null;
			}
		} catch (Exception e) {
			logger.error("Error updating category with ID {}: {}", id, e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Deletes a category by its ID.
	 * 
	 * @param id The ID of the category to delete
	 * @return true if deletion was successful, false otherwise
	 */
	public boolean deleteCategory(Long id) {
		try {
			logger.info("Deleting category with ID: {}", id);

			String url = buildUrl(CATEGORIES_PATH, id.toString());
			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);

			boolean success = response.getStatusCode() == HttpStatus.NO_CONTENT;
			if (success) {
				logger.info("Successfully deleted category with ID: {}", id);
			} else {
				logger.error("Failed to delete category with ID: {}. Status code: {}", id, response.getStatusCode());
			}
			return success;
		} catch (Exception e) {
			logger.error("Error deleting category with ID {}: {}", id, e.getMessage(), e);
			return false;
		}
	}
}