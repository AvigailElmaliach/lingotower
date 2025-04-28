package com.lingotower.controller;

import com.lingotower.data.AdminRepository;
import com.lingotower.data.UserRepository;
import com.lingotower.dto.category.CategoryDTO;
import com.lingotower.dto.translation.TranslationResponseDTO;
import com.lingotower.model.Category;
import com.lingotower.model.Admin;
import com.lingotower.model.BaseUser;
import com.lingotower.model.User;
import com.lingotower.model.Word;
import com.lingotower.service.CategoryService;
import com.lingotower.service.UserService;
import com.lingotower.service.WordService;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;

import com.lingotower.exception.*;
import com.lingotower.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {

	private final CategoryService categoryService;
	private final UserService userService;
	private final WordService wordService;
	private final UserRepository userRepository;
	private final AdminRepository adminRepository;

	@Autowired
	public CategoryController(CategoryService categoryService, UserService userService, WordService wordService,
			UserRepository userRepository, AdminRepository adminRepository) {
		this.categoryService = categoryService;
		this.userService = userService;
		this.wordService = wordService;
		this.userRepository = userRepository;
		this.adminRepository = adminRepository;
	}

	private CategoryDTO convertToDTO(Category category) {
		return new CategoryDTO(category.getId(), category.getName(), category.getTranslation());
	}

	private List<CategoryDTO> convertToDTOList(List<Category> categories) {
		return categories.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	@GetMapping
	public ResponseEntity<List<CategoryDTO>> getAllCategories(Principal principal) {
		String username = principal.getName();

		try {
			List<CategoryDTO> categories = categoryService.getAllCategoriesForUser(username);
			return ResponseEntity.ok(categories);
		} catch (ResponseStatusException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id, Principal principal) {
		User user = userService.getUserByUsername(principal.getName());
		String userLanguage = user.getTargetLanguage();

		CategoryDTO categoryDTO = categoryService.getCategoryById(id, userLanguage);
		return ResponseEntity.ok(categoryDTO);
	}

	@PostMapping
	public ResponseEntity<CategoryDTO> addCategory(@RequestBody CategoryDTO categoryDTO) {
		Category savedCategory = categoryService.addCategoryFromDTO(categoryDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedCategory));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
		try {
			categoryService.deleteCategory(id);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (CategoryNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
		try {
			Category updatedCategory = categoryService.updateCategory(id, categoryDTO);
			return ResponseEntity.ok(convertToDTO(updatedCategory));
		} catch (CategoryNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteAllCategories() {
		categoryService.deleteAllCategories();
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
