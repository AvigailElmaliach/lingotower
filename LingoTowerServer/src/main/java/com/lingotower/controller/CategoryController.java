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

	/**
	 * Constructor for the CategoryController, injecting required services and
	 * repositories.
	 * 
	 * @param categoryService The service layer for category-related operations.
	 * @param userService     The service layer for user-related operations.
	 * @param wordService     The service layer for word-related operations.
	 * @param userRepository  The repository for user data access.
	 * @param adminRepository The repository for admin data access.
	 */
	@Autowired
	public CategoryController(CategoryService categoryService, UserService userService, WordService wordService,
			UserRepository userRepository, AdminRepository adminRepository) {
		this.categoryService = categoryService;
		this.userService = userService;
		this.wordService = wordService;
		this.userRepository = userRepository;
		this.adminRepository = adminRepository;
	}

	/**
	 * Converts a Category entity to a CategoryDTO.
	 * 
	 * @param category The Category entity to convert.
	 * @return The corresponding CategoryDTO.
	 */
	private CategoryDTO convertToDTO(Category category) {
		return new CategoryDTO(category.getId(), category.getName(), category.getTranslation());
	}

	/**
	 * Converts a list of Category entities to a list of CategoryDTOs.
	 * 
	 * @param categories The list of Category entities to convert.
	 * @return A list of corresponding CategoryDTOs.
	 */
	private List<CategoryDTO> convertToDTOList(List<Category> categories) {
		return categories.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/**
	 * Retrieves all categories for the authenticated user.
	 * 
	 * @param principal The Principal object representing the currently logged-in
	 *                  user.
	 * @return ResponseEntity containing a list of CategoryDTO and HTTP status OK if
	 *         successful, or HTTP status UNAUTHORIZED if the user cannot be
	 *         determined.
	 */
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

	/**
	 * Retrieves a specific category by its ID for the authenticated user. The
	 * category's translation is based on the user's target language.
	 * 
	 * @param id        The ID of the category to retrieve.
	 * @param principal The Principal object representing the currently logged-in
	 *                  user.
	 * @return ResponseEntity containing the CategoryDTO and HTTP status OK.
	 */
	@GetMapping("/id/{id}")
	public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id, Principal principal) {
		User user = userService.getUserByUsername(principal.getName());
		String userLanguage = user.getTargetLanguage();

		CategoryDTO categoryDTO = categoryService.getCategoryById(id, userLanguage);
		return ResponseEntity.ok(categoryDTO);
	}

	/**
	 * Adds a new category.
	 * 
	 * @param categoryDTO The DTO containing the information for the new category.
	 * @return ResponseEntity containing the saved CategoryDTO and HTTP status
	 *         CREATED.
	 */
	@PostMapping
	public ResponseEntity<CategoryDTO> addCategory(@RequestBody CategoryDTO categoryDTO) {
		Category savedCategory = categoryService.addCategoryFromDTO(categoryDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedCategory));
	}

	/**
	 * Deletes a category by its ID.
	 * 
	 * @param id The ID of the category to delete.
	 * @return ResponseEntity with HTTP status NO CONTENT if the category was
	 *         deleted, or HTTP status NOT FOUND if the category does not exist.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
		try {
			categoryService.deleteCategory(id);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (CategoryNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	/**
	 * Updates an existing category.
	 * 
	 * @param id          The ID of the category to update.
	 * @param categoryDTO The DTO containing the updated information for the
	 *                    category.
	 * @return ResponseEntity containing the updated CategoryDTO and HTTP status OK
	 *         if successful, or HTTP status NOT FOUND if the category does not
	 *         exist.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
		try {
			Category updatedCategory = categoryService.updateCategory(id, categoryDTO);
			return ResponseEntity.ok(convertToDTO(updatedCategory));
		} catch (CategoryNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	/**
	 * Deletes all categories.
	 * 
	 * @return ResponseEntity with HTTP status NO CONTENT.
	 */
	@DeleteMapping
	public ResponseEntity<Void> deleteAllCategories() {
		categoryService.deleteAllCategories();
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}