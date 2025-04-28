package com.lingotower.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lingotower.constants.LanguageConstants;
import com.lingotower.data.AdminRepository;
import com.lingotower.data.CategoryRepository;
import com.lingotower.data.UserRepository;
import com.lingotower.dto.category.CategoryDTO;
import com.lingotower.dto.mapper.CategoryMapper;
import com.lingotower.exception.CategoryAlreadyExistsException;
import com.lingotower.exception.CategoryNotFoundException;
import com.lingotower.exception.UserNotFoundException;
import com.lingotower.model.Admin;
import com.lingotower.model.Category;
import com.lingotower.model.User;

/**
 * Service class for managing categories.
 */
@Service
public class CategoryService {

    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    @Autowired
    public CategoryService(UserService userService, CategoryRepository categoryRepository,
                            CategoryMapper categoryMapper, AdminRepository adminRepository, UserRepository userRepository) {
        this.userService = userService;
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all categories accessible to a specific user or admin.
     *
     * @param username the username of the user or admin
     * @return list of categories in DTO format
     * @throws UserNotFoundException if the user is not found
     */
    public List<CategoryDTO> getAllCategoriesForUser(String username) {
        Optional<Admin> optionalAdmin = adminRepository.findByUsername(username);
        if (optionalAdmin.isPresent()) {
            return getCategoriesForAdmin(optionalAdmin.get());
        }

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            return getCategoriesForUser(optionalUser.get());
        }

        throw new UserNotFoundException("Unauthorized user: " + username);
    }

    /**
     * Retrieves all categories for an admin.
     *
     * @param admin the admin
     * @return list of categories
     */
    private List<CategoryDTO> getCategoriesForAdmin(Admin admin) {
        return categoryRepository.findAll().stream()
                .map(category -> new CategoryDTO(category.getId(), category.getName(), category.getTranslation()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all categories for a user considering the target language.
     *
     * @param user the user
     * @return list of categories with language adjustments
     */
    private List<CategoryDTO> getCategoriesForUser(User user) {
        String targetLanguage = user.getTargetLanguage();

        return categoryRepository.findAll().stream()
                .map(category -> new CategoryDTO(
                        category.getId(),
                        targetLanguage.equals(LanguageConstants.HEBREW) ? category.getTranslation() : category.getName(),
                        targetLanguage.equals(LanguageConstants.HEBREW) ? category.getName() : category.getTranslation()))
                .collect(Collectors.toList());
    }

    /**
     * Finds a category by name.
     *
     * @param name the name of the category
     * @return optional category
     */
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    /**
     * Finds categories that do not have translations.
     *
     * @return list of untranslated categories
     */
    public List<Category> findCategoriesWithoutTranslation() {
        return categoryRepository.findByTranslationIsNullOrTranslationIs("");
    }

    /**
     * Gets an existing category by name or creates a new one if not found.
     *
     * @param name the category name
     * @return existing or newly created category
     */
    public Category getOrCreateCategory(String name) {
        return categoryRepository.findByName(name).orElseGet(() -> {
            Category newCategory = new Category();
            newCategory.setName(name);
            return categoryRepository.save(newCategory);
        });
    }

    /**
     * Retrieves a category by ID.
     *
     * @param id the category ID
     * @return category
     * @throws CategoryNotFoundException if category not found
     */
    public Category getCategoryById(Long id) throws CategoryNotFoundException {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }

    /**
     * Retrieves a category by ID and formats it according to user's language.
     *
     * @param id           the category ID
     * @param userLanguage the user's language
     * @return category DTO
     * @throws CategoryNotFoundException if category not found
     */
    public CategoryDTO getCategoryById(Long id, String userLanguage) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        String categoryName = userLanguage.equals(LanguageConstants.HEBREW) ? category.getTranslation() : category.getName();
        return new CategoryDTO(category.getId(), categoryName, category.getTranslation());
    }

    /**
     * Adds a new category to the system.
     *
     * @param category the category to add
     * @return added category
     * @throws CategoryAlreadyExistsException if category with same name exists
     */
    public Category addCategory(Category category) {
        Optional<Category> existingCategory = categoryRepository.findByName(category.getName());
        if (existingCategory.isPresent()) {
            throw new CategoryAlreadyExistsException("Category with this name already exists");
        }

        return categoryRepository.save(category);
    }

    /**
     * Adds a new category from a DTO.
     *
     * @param dto the category DTO
     * @return added category
     * @throws CategoryAlreadyExistsException if category with same name exists
     */
    public Category addCategoryFromDTO(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setTranslation(dto.getTranslation());
        return addCategory(category);
    }

    /**
     * Saves or updates a category.
     *
     * @param category the category
     * @return saved category
     */
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * Deletes a category by ID.
     *
     * @param id the category ID
     * @throws CategoryNotFoundException if category not found
     */
    public void deleteCategory(Long id) throws CategoryNotFoundException {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    /**
     * Updates a category by ID.
     *
     * @param id          the category ID
     * @param categoryDTO the new category data
     * @return updated category
     * @throws CategoryNotFoundException if category not found
     */
    public Category updateCategory(Long id, CategoryDTO categoryDTO) throws CategoryNotFoundException {
        Category existingCategory = getCategoryById(id);
        existingCategory.setName(categoryDTO.getName());
        return categoryRepository.save(existingCategory);
    }

    /**
     * Deletes all categories.
     */
    public void deleteAllCategories() {
        categoryRepository.deleteAll();
    }
}
