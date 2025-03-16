package com.lingotower.service;

import com.lingotower.model.Category;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

public class CategoryService {

    private static final String BASE_URL = "http://localhost:8080/categories";

    private RestTemplate restTemplate;

    public CategoryService() {
        this.restTemplate = new RestTemplate();
    }

//    public List<Category> getAllCategories() {
//        ResponseEntity<List> response = restTemplate.exchange(BASE_URL, HttpMethod.GET, null, List.class);
//        return response.getBody();
//    }
    public List<Category> getAllCategories() {
        ResponseEntity<List<Category>> response = restTemplate.exchange(
            BASE_URL,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Category>>() {}
        );
        return response.getBody();
    }

    public Category getCategoryById(Long id) {
        String url = BASE_URL + "/" + id;
        ResponseEntity<Category> response = restTemplate.exchange(url, HttpMethod.GET, null, Category.class);
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            return null;
        }
        return response.getBody();
    }

    public Category saveCategory(Category category) {
        return restTemplate.postForObject(BASE_URL, category, Category.class);
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        String url = BASE_URL + "/" + id;
        restTemplate.put(url, categoryDetails);
        return categoryDetails;
    }

    public void deleteCategory(Long id) {
        String url = BASE_URL + "/" + id;
        restTemplate.delete(url);
    }
    public void testSaveCategory() {
        Category newCategory = new Category();
        newCategory.setName("New Category");
        CategoryService categoryService = new CategoryService();
        Category savedCategory = categoryService.saveCategory(newCategory);
        if (savedCategory != null) {
            System.out.println("Category saved successfully: " + savedCategory);
        } else {
            System.out.println("Failed to save category.");
        }
    }



}
