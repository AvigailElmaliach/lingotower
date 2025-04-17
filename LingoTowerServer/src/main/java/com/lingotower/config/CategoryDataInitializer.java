package com.lingotower.config;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingotower.model.Category;
import com.lingotower.service.CategoryService;
import com.lingotower.service.TranslationService;

@Component
@Order(1) // מבטיח שהקטגוריות נטענות ראשונות
public class CategoryDataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private TranslationService translationService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("אתחול קטגוריות...");
        loadCategoriesFromJson("classpath:category.json");
        System.out.println("אתחול קטגוריות הסתיים.");
    }

    private void loadCategoriesFromJson(String resourcePath) {
        try {
            System.out.println(" 📂 טוען קטגוריות מהקובץ: " + resourcePath);
            Resource resource = resourceLoader.getResource(resourcePath);

            if (resource.exists()) {
                Category[] categories = objectMapper.readValue(resource.getInputStream(), Category[].class);
                System.out.println("נטענו " + categories.length + " קטגוריות מהקובץ");

                for (Category categoryFromJson : categories) {
                    String categoryName = categoryFromJson.getName();
                    Optional<Category> existingCategory = categoryService.findByName(categoryName);

                    if (existingCategory.isPresent()) {
                        System.out.println("   קטגוריה '" + categoryName + "' כבר קיימת (ID: " + existingCategory.get().getId() + ")");
                    } else {
                        Category newCategory = new Category();
                        newCategory.setName(categoryName);
                        String translatedCategoryName = translationService.translateText(categoryName, "en", "he");
                        newCategory.setTranslation(translatedCategoryName);

                        try {
                            Category savedCategory = categoryService.addCategory(newCategory);
                            System.out.println("קטגוריה '" + categoryName + "' נוספה (ID: " + savedCategory.getId() + ")");
                        } catch (Exception e) {
                            System.err.println("שגיאה בהוספת קטגוריה '" + categoryName + "': " + e.getMessage());
                        }
                    }
                }
            } else {
                System.out.println("קובץ קטגוריות לא נמצא: " + resourcePath);
            }
        } catch (IOException e) {
            System.err.println("  שגיאה בטעינת קטגוריות: " + e.getMessage());
        }
    }
}