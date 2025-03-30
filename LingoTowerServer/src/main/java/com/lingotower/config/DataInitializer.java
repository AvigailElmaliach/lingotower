package com.lingotower.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingotower.model.Category;
import com.lingotower.model.Word;
import com.lingotower.service.CategoryService;
import com.lingotower.service.WordService;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WordService wordService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔹 מתחיל לטעון נתונים מקובצי JSON...");

        try {
            // שלב 1: לטעון קטגוריות
            loadCategoriesFromJson("src/main/resources/category.json");

            // שלב 2: יצירת מיפוי בין שמות קבצים לקטגוריות
            Map<String, String> categoryMapping = new HashMap<>();
            categoryMapping.put("everyday.json", "Everyday Life and Essential Vocabulary");
            categoryMapping.put("people.json", "People and Relationships");
            categoryMapping.put("work.json", "Work and Education");
            categoryMapping.put("health.json", "Health and Well-being");
            categoryMapping.put("travel.json", "Travel and Leisure");
            categoryMapping.put("environment.json", "Environment and Nature");

            // שלב 3: טעינת מילים לפי קובץ והוספת קטגוריה
            for (Map.Entry<String, String> entry : categoryMapping.entrySet()) {
                String filePath = "src/main/resources/" + entry.getKey();
                String categoryName = entry.getValue();
                loadWordsFromJson(filePath, categoryName);
            }

            System.out.println("✅ כל הנתונים נטענו בהצלחה!");

        } catch (Exception e) {
            handleError(e);
        }
    }

    private void loadCategoriesFromJson(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Category[] categories = objectMapper.readValue(file, Category[].class);
                for (Category category : categories) {
                    // אם הקטגוריה כבר קיימת, לא נבצע הוספה חדשה
                    if (categoryService.findByName(category.getName()) == null) {
                        categoryService.getOrCreateCategory(category.getName());
                        System.out.println("✔ קטגוריה " + category.getName() + " נוספה בהצלחה.");
                    } else {
                        System.out.println("⚠ קטגוריה " + category.getName() + " כבר קיימת.");
                    }
                }
            } else {
                System.out.println("⚠ קובץ קטגוריות לא נמצא: " + filePath);
            }
        } catch (IOException e) {
            handleError(e);
        }
    }

    private void loadWordsFromJson(String filePath, String categoryName) {
        try {
            System.out.println("📂 טוען מילים מהקובץ: " + filePath + " לקטגוריה: " + categoryName);

            // שליפת הקטגוריה או יצירת חדשה
            Category category = categoryService.getOrCreateCategory(categoryName);

            // קריאה מקובץ JSON
            Word[] wordsArray = objectMapper.readValue(new File(filePath), Word[].class);

            // שיוך כל מילה לקטגוריה ושמירה למסד הנתונים
            for (Word word : wordsArray) {
                word.setCategory(category);

                // אם המילה כבר קיימת, לא נוסיף אותה
                if (wordService.findByWord(word.getWord()) == null) {
                    wordService.saveWord(word);
                    System.out.println("✔ מילה " + word.getWord() + " נוספה בהצלחה.");
                } else {
                    System.out.println("⚠ מילה " + word.getWord() + " כבר קיימת.");
                }
            }
        } catch (IOException e) {
            handleError(e);
        }
    }

    private void handleError(Exception e) {
        // טיפול בשגיאות - הצגת הודעה מפורטת
        System.err.println("❌ שגיאה במהלך טעינת נתונים: " + e.getMessage());
        e.printStackTrace();
    }
}
