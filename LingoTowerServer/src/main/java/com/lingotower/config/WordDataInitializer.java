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
import com.lingotower.dto.word.WordDTO;
import com.lingotower.service.CategoryService;
import com.lingotower.service.TranslationService;
import com.lingotower.service.WordService;

@Component
@Order(2) // Ensures words are loaded after categories
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
    public void run(String... args) {
        System.out.println("Initializing words...");
        loadAllCategoriesAndWords();
        System.out.println("Word initialization completed.");
    }

    private void loadAllCategoriesAndWords() {
        Map<String, String> categoryMapping = getCategoryMapping();

        for (Map.Entry<String, String> entry : categoryMapping.entrySet()) {
            String resourcePath = "classpath:" + entry.getKey();
            String categoryName = entry.getValue();
            loadWordsForCategory(resourcePath, categoryName);
        }
    }

    private Map<String, String> getCategoryMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("everyday.json", "Everyday Life and Essential Vocabulary");
        mapping.put("people.json", "People and Relationships");
        mapping.put("work.json", "Work and Education");
        mapping.put("health.json", "Health and Well being");
        mapping.put("travel.json", "Travel and Leisure");
        mapping.put("environment.json", "Environment and Nature");
        return mapping;
    }

    private void loadWordsForCategory(String resourcePath, String categoryName) {
        try {
            Word[] wordsArray = loadWordsFromFile(resourcePath);
            Category category = categoryService.getOrCreateCategory(categoryName);

            int addedCount = 0;

            for (Word word : wordsArray) {
                if (processSingleWord(word, category)) {
                    addedCount++;
                }
                
                WordDTO wordDTO = new WordDTO();
                wordDTO.setWord(word.getWord());
                wordDTO.setCategory(categoryName);  
                wordDTO.setTranslate(word.getTranslation());
                wordDTO.setDifficulty(word.getDifficulty());  
                wordDTO.setSourceLanguage("en");
                wordDTO.setTargetLanguage("he");

            }

            int existingCount = wordsArray.length - addedCount;
            System.out.println("Category: " + categoryName + " - Added: " + addedCount + ", Existing: " + existingCount);

        } catch (IOException e) {
            System.err.println("Error loading words from file " + resourcePath + ": " + e.getMessage());
        }
    }

    private Word[] loadWordsFromFile(String resourcePath) throws IOException {
        Resource resource = resourceLoader.getResource(resourcePath);
        if (!resource.exists()) {
            throw new IOException("File not found: " + resourcePath);
        }
        return objectMapper.readValue(resource.getInputStream(), Word[].class);
    }

    private boolean processSingleWord(Word word, Category category) {
        word.setCategory(category);
        Optional<Word> existingWord = wordService.findByWord(word.getWord());

        if (existingWord.isPresent()) {
            return false;
        }

        if (word.getTranslation() == null || word.getTranslation().isBlank()) {
            String translated = translationService.translateText(word.getWord(), "en", "he");
            word.setTranslation(translated);
        }

        wordService.saveWord(word, "en", "he");
        return true;
    }
}
