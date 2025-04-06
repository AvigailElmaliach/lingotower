package com.lingotower.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lingotower.data.CategoryRepository;
import com.lingotower.data.WordRepository;
import com.lingotower.dto.translation.TranslationRequestDTO;
import com.lingotower.dto.translation.TranslationResponseDTO;
import com.lingotower.dto.word.WordDTO;
import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;
import com.lingotower.model.Word;
import com.lingotower.util.TranslationUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class WordService {
	private final WordRepository wordRepository;
	private final CategoryRepository categoryRepository;
	private final TranslationService translationService;
	
	 @PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	public WordService(WordRepository wordRepository, TranslationService translationService,
			CategoryRepository categoryRepository) {
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
		entityManager.clear();
	}

//	// שיטה לשמור מילה
//	public Word saveWord(Word word) {
//		return wordRepository.save(word);
//	}
	public Word saveWord(Word word, String sourceLang, String targetLang) {
	    if (word.getTranslation() == null || word.getTranslation().isEmpty()) {
	        String translatedText = translationService.translateText(word.getWord(), sourceLang, targetLang);
	        if (translatedText != null && !translatedText.isEmpty()) {
	            word.setTranslation(translatedText);
	        } else {
	            System.out.println(" תרגום לא נמצא עבור: " + word.getWord());
	        }
	    }
	    return wordRepository.save(word);
	}
	
	public List<Word> findWordsWithoutTranslation() {
	    // מניח שמדובר בשדה 'translation' במילת המפתח
	    return wordRepository.findByTranslationIsNull();
	}


	// שיטה לחפש מילה לפי הטקסט שלה
	public Optional<Word> findByWord(String wordText) {
		return wordRepository.findByWord(wordText);
	}

//	public void addWordWithTranslation(WordDTO wordDTO, String targetLang) {
//		final String translatedText = translationService.translateText(wordDTO.getWord(), wordDTO.getLanguage(),
//				targetLang);

	public void addWordWithTranslation(WordDTO wordDTO, String targetLang) {
	    // מתרגמים את המילה לשפה הרצויה
	    final String translatedText = translationService.translateText(wordDTO.getWord(), wordDTO.getSourceLanguage(), targetLang);

	    // מציאת קטגוריה קיימת או יצירת חדשה
	    Category category = categoryRepository.findByName(wordDTO.getCategory()).orElseGet(() -> {
	        Category newCategory = new Category(wordDTO.getCategory());
	        return categoryRepository.save(newCategory);
	    });

	    // יצירת מילה חדשה עם התרגום
	    Word word = new Word(wordDTO.getWord(), translatedText, wordDTO.getSourceLanguage());
	    word.setCategory(category);
	    word.setDifficulty(wordDTO.getDifficulty()); // גם פה חשוב לוודא שממלאים את כל הפרטים

	    // שמירת המילה עם התרגום
	    wordRepository.save(word);
	}
	

 // שיטה להחזרת מילה לפי מזהה ושפת משתמש
    public TranslationResponseDTO getTranslatedWordById(Long id, String userLanguage) {
        Word word = wordRepository.findById(id).orElseThrow(() -> new RuntimeException("Word not found"));

        // מבצע את המיפוי של המילה בהתאם לשפת המשתמש
        String translation = "he".equals(userLanguage) ? word.getTranslation() : word.getWord();
        return new TranslationResponseDTO(word.getWord(), translation);
    }

    // שיטה להחזרת מילה לפי טקסט ומקור שפה
    public Optional<Word> getWordByWordAndSourceLanguage(String word, String sourceLanguage) {
        return wordRepository.findByWordAndSourceLanguage(word, sourceLanguage);
    }
    

    private List<TranslationResponseDTO> mapWordsToLanguage(List<Word> words, String userLanguage) {
        return words.stream()
                .map(word -> {
                    if( "he".equals(userLanguage))
                    return new TranslationResponseDTO(word.getTranslation(), word.getWord());
                    return new TranslationResponseDTO(word.getWord(), word.getTranslation());
                })
                .collect(Collectors.toList());
    }


    public List<TranslationResponseDTO> getTranslatedWordsByCategory(Long categoryId, String userLanguage) {
        List<Word> words = wordRepository.findByCategoryId(categoryId);

        if (words.isEmpty()) {
            return Collections.emptyList();
        }

        return mapWordsToLanguage(words, userLanguage);
    }



    public List<TranslationResponseDTO> getTranslatedWordsByCategoryAndDifficulty(Long categoryId, Difficulty difficulty, String userLanguage) {
        List<Word> words = wordRepository.findByCategoryIdAndDifficulty(categoryId, difficulty);

        if (words.isEmpty()) {
            return Collections.emptyList();
        }

        return mapWordsToLanguage(words, userLanguage);
    }


    public List<TranslationResponseDTO> getRandomTranslatedWordsByCategoryAndDifficulty(Long categoryId, Difficulty difficulty, String userLanguage) {
        List<Word> words = wordRepository.findByCategoryIdAndDifficulty(categoryId, difficulty);

        if (words.isEmpty()) {
            return Collections.emptyList();
        }

        Random random = new Random();
        Collections.shuffle(words, random);
        List<Word> randomWords = words.stream().limit(10).collect(Collectors.toList());

        return mapWordsToLanguage(randomWords, userLanguage);
    }


}
