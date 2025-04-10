package com.lingotower.service;

import java.security.Principal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lingotower.data.AdminRepository;
import com.lingotower.data.CategoryRepository;
import com.lingotower.data.UserRepository;
import com.lingotower.dto.category.CategoryDTO;
import com.lingotower.dto.mapper.CategoryMapper;
import com.lingotower.exception.CategoryAlreadyExistsException;
//import com.lingotower.exception.CategoryAlreadyExistsException;
import com.lingotower.exception.CategoryNotFoundException;
import com.lingotower.exception.UserNotFoundException;
import com.lingotower.model.Admin;
import com.lingotower.model.Category;
import com.lingotower.model.User;

@Service
public class CategoryService {

	private final UserService userService;
	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;
	private AdminRepository adminRepository;
	private UserRepository userRepository;

	@Autowired
	public CategoryService(UserService userService, CategoryRepository categoryRepository,
			CategoryMapper categoryMapper, AdminRepository adminRepository, UserRepository userRepository) {
		this.userService = userService;
		this.categoryRepository = categoryRepository;
		this.categoryMapper = categoryMapper;
		this.adminRepository = adminRepository;
		this.userRepository = userRepository;
	}

////	    
	public List<CategoryDTO> getAllCategoriesForUser(String username) {
		Optional<Admin> optionalAdmin = adminRepository.findByUsername(username);
		if (optionalAdmin.isPresent()) {
			Admin admin = optionalAdmin.get();
			return getCategoriesForAdmin(admin);
		}

		Optional<User> optionalUser = userRepository.findByUsername(username);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			return getCategoriesForUser(user);
		}

		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized user: " + username);
	}

	private List<CategoryDTO> getCategoriesForAdmin(Admin admin) {
		return categoryRepository.findAll().stream()
				.map(category -> new CategoryDTO(category.getId(), category.getName(), category.getTranslation()))
				.collect(Collectors.toList());
	}

	private List<CategoryDTO> getCategoriesForUser(User user) {
		String targetLanguage = user.getTargetLanguage();

		return categoryRepository.findAll().stream().map(category -> new CategoryDTO(category.getId(), // מזהה הקטגוריה
				targetLanguage.equals("he") ? category.getTranslation() : category.getName(),
				targetLanguage.equals("he") ? category.getName() : category.getTranslation()))
				.collect(Collectors.toList());
	}

	///

	private CategoryDTO convertToDTO(Category category) {
		return new CategoryDTO(category.getId(), category.getName(), category.getTranslation());
	}
//
//	public List<CategoryDTO> getAllCategories(String targetLanguage) {
//		List<Category> categories = categoryRepository.findAll();
//
//		for (Category category : categories) {
//			if ("he".equalsIgnoreCase(targetLanguage)) {
//				category.setName(category.getTranslation());
//			} else {
//				category.setName(category.getName());
//			}
//		}
//
//		return categories.stream().map(this::convertToDTO).collect(Collectors.toList());
//	}

	public Optional<Category> findByName(String name) {
		return categoryRepository.findByName(name);
	}

	public List<Category> findCategoriesWithoutTranslation() {
		return categoryRepository.findByTranslationIsNullOrTranslationIs("");
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

	public CategoryDTO getCategoryById(Long id, String userLanguage) {
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new CategoryNotFoundException("Category not found"));

		String categoryName = userLanguage.equals("he") ? category.getTranslation() : category.getName();

		return new CategoryDTO(category.getId(), categoryName, category.getTranslation());// לשים לב שcategoryName מביא
																							// את השם בשפת המשתמש
	}

	public Category addCategory(Category category) {
		Optional<Category> existingCategory = categoryRepository.findByName(category.getName());
		if (existingCategory.isPresent()) {
			throw new CategoryAlreadyExistsException("קטגוריה עם שם זה כבר קיימת");
		}

		return categoryRepository.save(category);
	}

	public Category addCategoryFromDTO(CategoryDTO dto) {
	    Category category = new Category();
	    category.setName(dto.getName());
	    category.setTranslation(dto.getTranslation());
	    return addCategory(category);
	}
	public Category saveCategory(Category category) {
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