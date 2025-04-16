package com.lingotower.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lingotower.model.Category;

public class CategoryService extends BaseService {

	private static final String BASE_URL = "http://localhost:8080/categories";
	private List<Category> categories = new ArrayList<>();
	private long nextId = 1;

	public CategoryService() {
		super(); // זה יאתחל את ה-RestTemplate עם מטפל השגיאות
	}

	public List<Category> getAllCategories() {
		try {
			// Create headers with authentication
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Make the request to http://localhost:8080/categories
			ResponseEntity<List<Category>> response = restTemplate.exchange(BASE_URL, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<Category>>() {
					});

			if (response.getStatusCode().is2xxSuccessful()) {
				return response.getBody() != null ? response.getBody() : new ArrayList<>();
			} else {
				System.err.println("Error response from server: " + response.getStatusCode());
				return new ArrayList<>();
			}
		} catch (Exception e) {
			System.err.println("Error fetching categories: " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public Category getCategoryById(Long id) {
		HttpHeaders headers = createAuthHeaders();
		HttpEntity<?> entity = new HttpEntity<>(headers);

		String url = BASE_URL + "/" + id;
		ResponseEntity<Category> response = restTemplate.exchange(url, HttpMethod.GET, entity, Category.class);

		if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
			return null;
		}
		return response.getBody();
	}

	public Category saveCategory(Category category) {
		HttpHeaders headers = createAuthHeaders();
		HttpEntity<Category> entity = new HttpEntity<>(category, headers);

		return restTemplate.postForObject(BASE_URL, entity, Category.class);
	}

	public Category updateCategory(Long id, Category categoryDetails) {
		HttpHeaders headers = createAuthHeaders();
		HttpEntity<Category> entity = new HttpEntity<>(categoryDetails, headers);

		String url = BASE_URL + "/" + id;
		ResponseEntity<Category> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Category.class);

		return response.getBody();
	}

	public boolean deleteCategory(Long id) {
		HttpHeaders headers = createAuthHeaders();
		HttpEntity<?> entity = new HttpEntity<>(headers);

		String url = BASE_URL + "/" + id;
		ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);

		return response.getStatusCode() == HttpStatus.NO_CONTENT;
	}

	public Category addCategory(Category category) {
		try {
			// Create headers with authentication
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<Category> entity = new HttpEntity<>(category, headers);

			// Make the POST request to the API
			return restTemplate.postForObject(BASE_URL, entity, Category.class);
		} catch (Exception e) {
			System.err.println("Error adding category: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

}