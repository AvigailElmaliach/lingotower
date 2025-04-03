package com.lingotower.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingotower.model.Category;
import com.lingotower.model.Word;
import com.lingotower.service.CategoryService;
import com.lingotower.service.TranslationService;
import com.lingotower.service.WordService;

@Component
public class DataInitializer implements CommandLineRunner {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private WordService wordService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ResourceLoader resourceLoader;

	private TranslationService translationService;

	 @Autowired
	    public DataInitializer(TranslationService translationService) {
	        this.translationService = translationService;
	    }
	
	@Override
	public void run(String... args) throws Exception {
		System.out.println("🔹 מתחיל לטעון נתונים מקובצי JSON...");

		try {
			// שלב 1: לטעון קטגוריות
			loadCategoriesFromJson("classpath:category.json");

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
				String resourcePath = "classpath:" + entry.getKey();
				String categoryName = entry.getValue();
				loadWordsFromJson(resourcePath, categoryName);
			}

			System.out.println("✅ כל הנתונים נטענו בהצלחה!");
			updateCategoriesWithoutTranslation();
			// שלב 4: עדכון מילים ללא תרגום
            updateWordsWithoutTranslation();
			System.out.println("יששששש");

			

		} catch (Exception e) {
			handleError(e);
		}
	}
	

	private void loadCategoriesFromJson(String resourcePath) {
		try {
			System.out.println("📂 מנסה לטעון קטגוריות מהקובץ: " + resourcePath);
			Resource resource = resourceLoader.getResource(resourcePath);

			if (resource.exists()) {
				Category[] categories = objectMapper.readValue(resource.getInputStream(), Category[].class);
				System.out.println("✅ נטענו " + categories.length + " קטגוריות מהקובץ");

				for (Category categoryFromJson : categories) {
					// Check if category exists by name
					String categoryName = categoryFromJson.getName();
					Optional<Category> existingCategory = categoryService.findByName(categoryName);

					if (existingCategory.isPresent()) {
						System.out.println(
								"⚠ קטגוריה '" + categoryName + "' כבר קיימת עם ID " + existingCategory.get().getId());
					} else {
						// Create a new category with only the name
						Category newCategory = new Category();
						newCategory.setName(categoryName);
	
	  String translatedCategoryName = translationService.translateText(categoryName, "en", "he");
      newCategory.setTranslation(translatedCategoryName);
      
      
      
						// Save the category and let the database assign an ID
						try {
							Category savedCategory = categoryService.addCategory(newCategory);
							System.out.println(
									"✔ קטגוריה '" + categoryName + "' נוספה בהצלחה עם ID " + savedCategory.getId());
						} catch (Exception e) {
							System.out.println("❌ שגיאה בהוספת קטגוריה '" + categoryName + "': " + e.getMessage());
						}
					}
				}
			} else {
				System.out.println("⚠ קובץ קטגוריות לא נמצא: " + resourcePath);
			}
		} catch (Exception e) {
			System.out.println("❌ שגיאה בטעינת קטגוריות: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void loadWordsFromJson(String resourcePath, String categoryName) {
	    try {
	        System.out.println("📂 טוען מילים מהקובץ: " + resourcePath + " לקטגוריה: " + categoryName);
	        Resource resource = resourceLoader.getResource(resourcePath);

	        if (!resource.exists()) {
	            System.out.println("⚠ קובץ מילים לא נמצא: " + resourcePath);
	            return;
	        }

	        Category category = categoryService.getOrCreateCategory(categoryName);
	        Word[] wordsArray = objectMapper.readValue(resource.getInputStream(), Word[].class);
	        System.out.println("✅ נטענו " + wordsArray.length + " מילים מהקובץ");

	        int addedCount = 0, existingCount = 0;
	        for (Word word : wordsArray) {
	            word.setCategory(category);

	            // בדיקה אם המילה כבר קיימת במסד הנתונים
	            Optional<Word> existingWord = wordService.findByWord(word.getWord());
	            if (existingWord.isEmpty()) {
	                // תרגום ושמירה
	                String translatedText = translationService.translateText(word.getWord(), "en", "he");
	                word.setTranslation(translatedText);

	                wordService.saveWord(word, "en", "he");
	                addedCount++;
	            } else {
	                existingCount++;
	            }
	        }

	        System.out.println("✔ נוספו " + addedCount + " מילים חדשות, " + existingCount + " מילים כבר קיימות.");
	    } catch (IOException e) {
	        System.out.println("❌ שגיאה בטעינת מילים מהקובץ " + resourcePath + ": " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	private void updateWordsWithoutTranslation() {
        try {
            System.out.println(" מחפש מילים ישנות ללא תרגום...");
            // חיפוש מילים שאין להם תרגום
            List<Word> wordsWithoutTranslation = wordService.findWordsWithoutTranslation();
            
            int translatedCount = 0;
            
            // עבור כל מילה, תבצע תרגום
            for (Word word : wordsWithoutTranslation) {
                String translatedText = translationService.translateText(word.getWord(), "en", "he");
                word.setTranslation(translatedText);
                wordService.saveWord(word, "en", "he");
                translatedCount++;
            }
            
            System.out.println("✔ נוספו תרגומים ל-" + translatedCount + " מילים.");
            
        } catch (Exception e) {
            System.out.println("❌ שגיאה בעדכון מילים ללא תרגום: " + e.getMessage());
            e.printStackTrace();
        }
    }

	private void updateCategoriesWithoutTranslation() {
	    try {
	        System.out.println("🔍 מחפש קטגוריות ללא תרגום...");

	        // חיפוש קטגוריות שאין להן תרגום
	        List<Category> categoriesWithoutTranslation = categoryService.findCategoriesWithoutTranslation();

	        int translatedCount = 0;

	        // עדכון כל קטגוריה עם תרגום
	        for (Category category : categoriesWithoutTranslation) {
	            String translatedName = translationService.translateText(category.getName(), "en", "he");
	            category.setTranslation(translatedName);
	            categoryService.saveCategory(category);
	            translatedCount++;
	        }

	        System.out.println("✔ נוספו תרגומים ל-" + translatedCount + " קטגוריות.");

	    } catch (Exception e) {
	        System.out.println("❌ שגיאה בעדכון קטגוריות ללא תרגום: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	private void handleError(Exception e) {
		// טיפול בשגיאות - הצגת הודעה מפורטת
		System.err.println("❌ שגיאה במהלך טעינת נתונים: " + e.getMessage());
		e.printStackTrace();
	}
}