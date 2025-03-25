package com.lingotower.service;

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

	public CategoryService() {
		super(); // זה יאתחל את ה-RestTemplate עם מטפל השגיאות
	}

	public List<Category> getAllCategories() {
		// יצירת ישות HTTP עם כותרות אימות
		HttpHeaders headers = createAuthHeaders();
		HttpEntity<?> entity = new HttpEntity<>(headers);

		ResponseEntity<List<Category>> response = restTemplate.exchange(BASE_URL, HttpMethod.GET, entity,
				new ParameterizedTypeReference<List<Category>>() {
				});
		return response.getBody();
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

	public void deleteCategory(Long id) {
		HttpHeaders headers = createAuthHeaders();
		HttpEntity<?> entity = new HttpEntity<>(headers);

		String url = BASE_URL + "/" + id;
		restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
	}
}