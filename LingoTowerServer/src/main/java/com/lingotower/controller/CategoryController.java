package com.lingotower.controller;

import com.lingotower.dto.category.CategoryDTO;
import com.lingotower.dto.translation.TranslationResponseDTO;
import com.lingotower.model.Category;
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

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {

	private final CategoryService categoryService;
    private final UserService userService;  // שירות ניהול משתמשים
    private final WordService wordService;
    @Autowired
    public CategoryController(CategoryService categoryService, UserService userService,WordService wordService) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.wordService=wordService;
    }
    
    private CategoryDTO convertToDTO(Category category) {
        return new CategoryDTO(category.getId(), category.getName());
    }

    private List<CategoryDTO> convertToDTOList(List<Category> categories) {
        return categories.stream()
                         .map(this::convertToDTO)
                         .collect(Collectors.toList());
    }
    
    //-------------------------//--------------//
    @GetMapping("/category/{categoryId}/translate")
    public ResponseEntity<List<TranslationResponseDTO>> getTranslatedWordsByCategory(
            @PathVariable Long categoryId,
            HttpServletRequest request) {

        String sourceLang = (String) request.getAttribute("sourceLanguage");
        String targetLang = (String) request.getAttribute("targetLanguage");
        
        List<TranslationResponseDTO> translatedWords = wordService.getTranslatedWordsByCategory(categoryId, sourceLang, targetLang);
        
        return translatedWords.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                : ResponseEntity.ok(translatedWords);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories(Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

//    @GetMapping
//    public List<CategoryDTO> getAllCategories() {
//        List<CategoryDTO> categoryDTOList = categoryService.getAllCategories();
//        return categoryDTOList;  // מחזירים ישירות את רשימת ה-DTO
//    }

    @GetMapping("/id/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(convertToDTO(category));
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); 
        }
    }
//    @GetMapping("/word/{word}")
//    public ResponseEntity<CategoryDTO> getCategoryByWord(@PathVariable String word) {
//        try {
//            // חיפוש הקטגוריה על פי המילה
//            Category category = categoryService.getCategoryByWord(word);
//            return ResponseEntity.ok(convertToDTO(category));
//        } catch (CategoryNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//    }
    @PostMapping
    public ResponseEntity<CategoryDTO> addCategory(@RequestBody CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        Category savedCategory = categoryService.addCategory(category);
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
// // ✅ שמירת קטגוריה (POST)
//    @PostMapping("/{categoryName}")
//    public ResponseEntity<String> saveCategory(@PathVariable String categoryName, @RequestBody List<Word> words) throws IOException, java.io.IOException {
//        categoryService.writeCategoryToFile(categoryName, words);
//        return ResponseEntity.ok("✅ הקטגוריה נשמרה בהצלחה!");
//    }
//
//    // ✅ טעינת קטגוריה (GET)
//    @GetMapping("/{categoryName}")
//    public ResponseEntity<Category> getCategory(@PathVariable String categoryName) throws IOException, java.io.IOException {
//        Category category = categoryService.readCategoryFromFile(categoryName);
//        return ResponseEntity.ok(category);
//    }
}
