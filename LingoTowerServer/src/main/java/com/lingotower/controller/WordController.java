package com.lingotower.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingotower.dto.translation.TranslationResponseDTO;
import com.lingotower.dto.word.WordDTO;
import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;
import com.lingotower.model.Word;
import com.lingotower.service.CategoryService;
import com.lingotower.service.TranslationService;
import com.lingotower.service.WordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/words")
public class WordController {
    private final WordService wordService;
    private final CategoryService categoryService;
    private TranslationService translationService;
    @Autowired
    private ObjectMapper objectMapper;


    public WordController(WordService wordService,CategoryService categoryService,TranslationService translationService) {
        this.wordService = wordService;
        this.categoryService=categoryService;
        this.translationService=translationService;
    }

    @GetMapping
    public ResponseEntity<List<Word>> getAllWords() {
        return ResponseEntity.ok(wordService.getAllWords());
    }

    @GetMapping("/category/{categoryId}/translate")
    public ResponseEntity<List<TranslationResponseDTO>> getTranslatedWordsByCategory(
            @PathVariable Long categoryId,
            @RequestParam String sourceLang,
            @RequestParam String targetLang) {
    	
        List<TranslationResponseDTO> translatedWords = wordService.getTranslatedWordsByCategory(categoryId,sourceLang, targetLang);
        
        return translatedWords.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                : ResponseEntity.ok(translatedWords);
    }

    @GetMapping("/category/{categoryId}/difficulty/{difficulty}/translate")
    public ResponseEntity<List<TranslationResponseDTO>> getTranslatedWordsByCategoryAndDifficulty(
            @PathVariable Long categoryId,
            @RequestParam String sourceLang,
            @PathVariable Difficulty difficulty,
            @RequestParam String targetLang) {
        List<TranslationResponseDTO> translatedWords = wordService.getTranslatedWordsByCategoryAndDifficulty(categoryId, difficulty,sourceLang, targetLang);
        return translatedWords.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                : ResponseEntity.ok(translatedWords);
    }
    // קבלת 10 מילים אקראיות לפי קטגוריה ורמת קושי
    @GetMapping("/category/{categoryId}/difficulty/{difficulty}/translate/random")
    public ResponseEntity<List<TranslationResponseDTO>> getRandomTranslatedWordsByCategoryAndDifficulty(
            @PathVariable Long categoryId,
            @PathVariable Difficulty difficulty,
            @RequestParam String sourceLang,
            @RequestParam String targetLang) {
        
        List<TranslationResponseDTO> translatedWords = wordService.getRandomTranslatedWordsByCategoryAndDifficulty(categoryId, difficulty, sourceLang, targetLang);
        
        if (translatedWords.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        return ResponseEntity.ok(translatedWords);
    }


    @GetMapping("/{id}/translate")
    public ResponseEntity<TranslationResponseDTO> getTranslatedWordById(
            @PathVariable Long id,
            @RequestParam String targetLang) {
        try {
            TranslationResponseDTO translatedWord = wordService.getTranslatedWordById(id, targetLang);
            return ResponseEntity.ok(translatedWord);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addWordWithTranslation(///כרגע פונקציה מיותרת
            @RequestBody WordDTO wordDTO,
            @RequestParam String targetLang) {
        wordService.addWordWithTranslation(wordDTO, targetLang);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllWords() {
        try {
            wordService.deleteAllWords();
            return ResponseEntity.ok("כל המילים נמחקו בהצלחה");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("שגיאה במחיקת המילים: " + e.getMessage());
        }
    }
   
    @PostMapping("/upload")
    public ResponseEntity<String> uploadWords(@RequestParam("category") String categoryName,
                                              @RequestParam("file") MultipartFile file) {
        try {
        	System.out.println("Category: " + categoryName);  // הדפסה של הקטגוריה
            System.out.println("File name: " + file.getOriginalFilename());  // הדפסה של שם הקובץ

            Category category = categoryService.getOrCreateCategory(categoryName);
            List<Word> words = file.getOriginalFilename().endsWith(".csv") ?
                               parseCsv(file, category) : parseJson(file, category);
            
            wordService.saveWords(words);
            return ResponseEntity.ok("המילים נוספו בהצלחה");
        } catch (Exception e) {
        	e.printStackTrace();  
            return ResponseEntity.badRequest().body("שגיאהעעעע: " + e.getMessage());
        }
    }
    private List<Word> parseJson(MultipartFile file, Category category) throws Exception {
        Word[] wordsArray = objectMapper.readValue(file.getInputStream(), Word[].class);
        for (Word word : wordsArray) {
            word.setCategory(category);  
            word.setTranslation(translationService.translateText(word.getWord(), word.getSourceLanguage(), word.getTargetLanguage()));
        }
        return List.of(wordsArray);
    }
    private List<Word> parseCsv(MultipartFile file, Category category) throws Exception {
        List<Word> words = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length < 4) continue;

            String wordText = parts[0].trim();
            String sourceLang = parts[1].trim();
            String targetLang = parts[2].trim();
            Difficulty difficulty = Difficulty.valueOf(parts[3].trim().toUpperCase());
            String translation = translationService.translateText(wordText, sourceLang, targetLang);

            words.add(new Word(null, wordText, translation, sourceLang, targetLang, difficulty, category));
        }
        return words;
    }
 
}
