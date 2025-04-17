package com.lingotower.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
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
@Order(2) // מבטיח שהמילים נטענות לאחר הקטגוריות
public class WordDataInitializer implements CommandLineRunner {

    @Autowired
    private WordService wordService;

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
        System.out.println("⚙️ אתחול מילים...");
        loadWordsFromJson();
        System.out.println("✔️ אתחול מילים הסתיים.");
    }

    private void loadWordsFromJson() {
        Map<String, String> categoryMapping = new HashMap<>();
        categoryMapping.put("everyday.json", "Everyday Life and Essential Vocabulary");
        categoryMapping.put("people.json", "People and Relationships");
        categoryMapping.put("work.json", "Work and Education");
        categoryMapping.put("health.json", "Health and Well-being");
        categoryMapping.put("travel.json", "Travel and Leisure");
        categoryMapping.put("environment.json", "Environment and Nature");

        for (Map.Entry<String, String> entry : categoryMapping.entrySet()) {
            String resourcePath = "classpath:" + entry.getKey();
            String categoryName = entry.getValue();
            loadWordsFromJson(resourcePath, categoryName);
        }
    }

    private void loadWordsFromJson(String resourcePath, String categoryName) {
        try {
            System.out.println(" 📂 טוען מילים מהקובץ: " + resourcePath + " לקטגוריה: " + categoryName);
            Resource resource = resourceLoader.getResource(resourcePath);

            if (!resource.exists()) {
                System.out.println("  ⚠️ קובץ מילים לא נמצא: " + resourcePath);
                return;
            }

            Category category = categoryService.getOrCreateCategory(categoryName);
            Word[] wordsArray = objectMapper.readValue(resource.getInputStream(), Word[].class);
            System.out.println("  └── נטענו " + wordsArray.length + " מילים מהקובץ");

            int addedCount = 0, existingCount = 0;
            for (Word word : wordsArray) {
                word.setCategory(category);
                Optional<Word> existingWord = wordService.findByWord(word.getWord());
                if (existingWord.isEmpty()) {
                    String translatedText = translationService.translateText(word.getWord(), "en", "he");
                    word.setTranslation(translatedText);
                    wordService.saveWord(word, "en", "he");
                    addedCount++;
                } else {
                    existingCount++;
                }
            }
            System.out.println("  └── נוספו " + addedCount + " מילים חדשות, " + existingCount + " מילים קיימות.");

        } catch (IOException e) {
            System.err.println("  ❌ שגיאה בטעינת מילים מהקובץ " + resourcePath + ": " + e.getMessage());
        }
    }
}