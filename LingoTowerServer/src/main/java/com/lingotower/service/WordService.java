package com.lingotower.service;

import com.lingotower.model.Word;
import com.lingotower.data.CategoryRepository;
import com.lingotower.data.WordRepository;
import com.lingotower.dto.translation.TranslationRequestDTO;
import com.lingotower.dto.translation.TranslationResponseDTO;
import com.lingotower.dto.word.WordDTO;
import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class WordService {
    private final WordRepository wordRepository;
    private final CategoryRepository categoryRepository;
    
    @Autowired
    private final TranslationService translationService;
    
    public WordService(WordRepository wordRepository, TranslationService translationService, CategoryRepository categoryRepository) {
        this.wordRepository = wordRepository;
        this.translationService = translationService;
        this.categoryRepository = categoryRepository;
    }
    public void saveWords(List<Word> words) {
        wordRepository.saveAll(words);
    }
    public List<Word> getAllWords() {
        return wordRepository.findAll();
    }
    public void deleteAllWords() {
        wordRepository.deleteAll();
    }
    // שיטה לשמור מילה
    public Word saveWord(Word word) {
        return wordRepository.save(word);
    }

    // שיטה לחפש מילה לפי הטקסט שלה
    public Optional<Word> findByWord(String wordText) {
        return wordRepository.findByWord(wordText);
    }

  

   
  
    public void addWordWithTranslation(WordDTO wordDTO, String targetLang) {
        final String translatedText = translationService.translateText(wordDTO.getWord(), wordDTO.getLanguage(), targetLang);

        // מציאת קטגוריה קיימת או יצירת חדשה
        Category category = categoryRepository.findByName(wordDTO.getCategory())
                .orElseGet(() -> {
                    Category newCategory = new Category(wordDTO.getCategory());
                    return categoryRepository.save(newCategory);
                });

        Word word = new Word(
            wordDTO.getWord(),
            translatedText,
            wordDTO.getLanguage()
        );

        word.setCategory(category);
        word.setDifficulty(wordDTO.getDifficulty());//לבדוק את זה

        wordRepository.save(word);
    }

 // קבלת מילים מתורגמות לפי קטגוריה
    public List<TranslationResponseDTO> getTranslatedWordsByCategory(Long categoryId, String sourceLang, String targetLang) {
        List<Word> words = wordRepository.findByCategoryId(categoryId);
        System.out.println("Found words: " + words);
        // אם אין מילים בקטגוריה, נחזיר רשימה ריקה
        if (words.isEmpty()) {
            return Collections.emptyList();
        }

        List<TranslationResponseDTO> translatedWords = new ArrayList<>();
        List<TranslationRequestDTO> wordsToTranslate = new ArrayList<>();

        for (Word word : words) {
            if (word.getTranslation() != null && word.getTranslation().equals(targetLang)) {
                // אם יש תרגום קיים לאותה שפה - נוסיף אותו לרשימה
                translatedWords.add(new TranslationResponseDTO(word.getWord(), word.getTranslation()));
            } else {
                // אם אין תרגום מתאים, נוסיף את המילה לרשימת תרגום
                wordsToTranslate.add(new TranslationRequestDTO(word.getWord(), sourceLang, targetLang));
            }
        }

        // אם יש מילים שצריך לתרגם - נפעיל את שירות התרגום
        if (!wordsToTranslate.isEmpty()) {
            translatedWords.addAll(translationService.translateWords(wordsToTranslate));
        }

        return translatedWords;
    }


//    // קבלת מילים מתורגמות לפי קטגוריה
//    public List<TranslationResponseDTO> getTranslatedWordsByCategory(Long categoryId, String sourceLang,String targetLang) {
//        List<Word> words = wordRepository.findByCategoryId(categoryId);
//        
//        if (words.isEmpty()) {
//            return Collections.emptyList();
//        }
//        
//        // החזרת המילים עם התרגום שנשמר במאגר
//        return words.stream()
//                .map(word -> new TranslationResponseDTO(word.getWord(), word.getTranslation()))
//                .collect(Collectors.toList());
//    }

    public List<TranslationResponseDTO> getTranslatedWordsByCategoryAndDifficulty(Long categoryId, Difficulty difficulty,String sourceLang, String targetLang) {
        List<Word> words = wordRepository.findByCategoryIdAndDifficulty(categoryId, difficulty);
        
        // אם לא נמצאו מילים
        if (words.isEmpty()) {
            return Collections.emptyList();
        }

        // החזרת המילים עם התרגום שנשמר במאגר
        return words.stream()
                .map(word -> new TranslationResponseDTO(word.getWord(), word.getTranslation()))
                .collect(Collectors.toList());
    }
    
    public List<TranslationResponseDTO> getRandomTranslatedWordsByCategoryAndDifficulty(Long categoryId, Difficulty difficulty, String sourceLang, String targetLang) {
        // קבלת המילים המסוננות לפי קטגוריה ורמת קושי
        List<TranslationResponseDTO> translatedWords = getTranslatedWordsByCategoryAndDifficulty(categoryId, difficulty, sourceLang, targetLang);
        
        
        if (translatedWords.isEmpty()) {
            return Collections.emptyList();
        }
        
       
        Random random = new Random();
        Collections.shuffle(translatedWords, random);

        // מחזירים 10 מילים אם יש יותר מ-10, אחרת מחזירים את כל המילים
        return translatedWords.size() > 10 ? translatedWords.subList(0, 10) : translatedWords;
    }


    // קבלת מילה לפי מזהה
    public TranslationResponseDTO getTranslatedWordById(Long id, String targetLang) {
        Word word = wordRepository.findById(id).orElseThrow(() -> new RuntimeException("Word not found"));
        return new TranslationResponseDTO(word.getWord(), word.getTranslation());
    }
  
   
}
