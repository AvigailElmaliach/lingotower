package com.lingotower.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingotower.dto.category.CategoryDTO;
import com.lingotower.exception.CategoryAlreadyExistsException;
import com.lingotower.exception.CategoryNotFoundException;
import com.lingotower.model.Category;
import com.lingotower.model.Word;
import java.io.File;
import java.io.IOException;
import com.lingotower.exception.FileExceptionHandler;
//import io.jsonwebtoken.io.IOException;

import com.lingotower.data.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
//import com.lingotower.service.TranslationService;
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
//    @Autowired
//    private TranslationService translationService;
    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    private CategoryDTO convertToDTO(Category category) {
        return new CategoryDTO(category.getId(), category.getName());
    }

    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                         .map(this::convertToDTO)
                         .collect(Collectors.toList());
    }
    // שיטה לחפש קטגוריה לפי שם
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    // שיטה לקבל או ליצור קטגוריה חדשה
    public Category getOrCreateCategory(String name) {
        Optional<Category> existingCategory = findByName(name);
        if (existingCategory.isPresent()) {
            return existingCategory.get();
        } else {
            Category newCategory = new Category();
            newCategory.setName(name);
            return categoryRepository.save(newCategory);
        }
    }

    public Category getCategoryById(Long id) throws CategoryNotFoundException {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }
    public Category addCategory(Category category) {
        // בדיקה אם קטגוריה עם אותו שם כבר קיימת
        Optional<Category> existingCategory = categoryRepository.findByName(category.getName());
        if (existingCategory.isPresent()) {
            // אם הקטגוריה קיימת, זרוק שגיאה או החזר ערך מתאים
            throw new CategoryAlreadyExistsException("קטגוריה עם שם זה כבר קיימת");
        }

        // אם לא קיימת, שמור את הקטגוריה
        return categoryRepository.save(category);
    }
    
    public void deleteCategory(Long id) throws CategoryNotFoundException {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    public Category updateCategory(Long id, CategoryDTO categoryDTO) throws CategoryNotFoundException {
        Category existingCategory = getCategoryById(id); // זורק חריגה אם לא נמצא
        existingCategory.setName(categoryDTO.getName());
        return categoryRepository.save(existingCategory);
    }

    public void deleteAllCategories() {
        categoryRepository.deleteAll();
    }
//    // ✅ כתיבה לקובץ JSON
//    public void writeCategoryToFile(String categoryName, List<Word> words) throws IOException {
//        Category category = new Category(categoryName, words);
//        objectMapper.writeValue(new File(categoryName + ".json"), category);
//    }
//
//    // ✅ קריאה מקובץ JSON (תיקון ההכרזה על החריגות)
//    public Category readCategoryFromFile(String categoryName) throws IOException {
//    	File file = new File(categoryName + ".json");
//    	if (!file.exists()) {
//    	    throw new RuntimeException("הקובץ לא נמצא: " + file.getAbsolutePath());
//    	}
//        return objectMapper.readValue(new File(categoryName + ".json"), Category.class);
//    }
  
}
