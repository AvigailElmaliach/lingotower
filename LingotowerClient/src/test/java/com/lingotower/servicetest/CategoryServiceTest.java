package com.lingotower.servicetest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.lingotower.model.Category;
import com.lingotower.service.CategoryService;

public class CategoryServiceTest {

    @Test
    public void testGetAllCategories() {
        CategoryService categoryService = new CategoryService();
        List<Category> categories = categoryService.getAllCategories();
        
        assertNotNull(categories, "הקטגוריות לא אמורות להיות null");
        assertTrue(categories.size() > 0, "הקטגוריות אמורות לכלול נתונים");
        
        // אופציונלי: הדפס את הקטגוריות
        categories.forEach(category -> System.out.println(category));
    }

    @Test
    public void testGetCategoryById() {
        CategoryService categoryService = new CategoryService();
        
        // נסה לשלוף קטגוריה לפי ID
        Category category = categoryService.getCategoryById(1L);  // ID דוגמה
        
        assertNotNull(category, "הקטגוריה לא נמצאה");
        System.out.println("קטגוריה שהתקבלה: " + category);
    }
}
